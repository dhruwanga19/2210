# CS 2210 — Asn2 Two-Player Dynamic Game

Java Swing game with a full production-style GitOps pipeline: signed container
images, EKS, Argo CD, Kyverno admission policies, and multiple layers of
security scanning on every push.

> The original assignment is a plain Swing GUI. This repo wraps it so the game
> is reachable from a browser on a hardened Kubernetes cluster. The work isn't
> a CS 2210 requirement — it's a portfolio demo showing the same codebase run
> through a real CI/CD and infrastructure story.

- **App repo**: you are here — `dhruwanga19/2210` — owns `Asn2/` + the CI
  pipeline that builds, scans, signs, and publishes the container image.
- **GitOps repo**: [`dhruwanga19/asn2-gitops`](https://github.com/dhruwanga19/asn2-gitops)
  — Terraform for AWS + Kubernetes manifests that Argo CD reconciles.

---

## Table of contents

1. [Architecture](#architecture)
2. [Security tooling](#security-tooling)
3. [Setup](#setup)
4. [Day-to-day workflow](#day-to-day-workflow)
5. [Local development](#local-development)
6. [Troubleshooting](#troubleshooting)
7. [Teardown](#teardown)
8. [Costs](#costs)

---

## Architecture

```
 ┌────────────── dhruwanga19/2210 (this repo) ──────────────┐
 │  Java source (Asn2/src)                                   │
 │  Dockerfile (Xvfb + x11vnc + noVNC + supervisord)         │
 │  CI: .github/workflows/ci.yml                             │
 │     gitleaks → CodeQL → hadolint → build →                │
 │     Syft (SBOM) → Trivy (fail HIGH) →                     │
 │     push ECR via OIDC → cosign keyless sign →             │
 │     PR against asn2-gitops with new image tag             │
 └──────────────────────────┬────────────────────────────────┘
                            │ PR merged in gitops repo
                            ▼
 ┌────────────── dhruwanga19/asn2-gitops ───────────────────┐
 │  infra/terraform  VPC, EKS 1.30, ECR, IAM/OIDC,           │
 │                   Route53, ACM, ALB Controller,           │
 │                   ExternalDNS, Kyverno, Argo CD           │
 │                   (Helm, remote state on S3 + DDB)        │
 │  clusters/prod    app-of-apps: Argo CD Applications →     │
 │                   Kustomize workloads + Kyverno policies  │
 └──────────────────────────┬────────────────────────────────┘
                            │ Argo CD syncs every 3 min
                            ▼
 ┌────────────────── EKS cluster (prod) ────────────────────┐
 │  ns/asn2    Deployment, Service, ALB Ingress + ACM,       │
 │             NetworkPolicy (default-deny)                  │
 │  ns/kyverno disallow-latest-tag, require-resource-limits, │
 │             disallow-privilege-escalation,                │
 │             verify-cosign-signature (admission gate)      │
 │                                                           │
 │  Pod:  root supervisord → Xvfb :99 → fluxbox → x11vnc →   │
 │        websockify (:6080) → gosu app → java PlayGame      │
 └──────────────────────────┬────────────────────────────────┘
                            │
                            ▼
            https://game.<your-domain>/
     (ALB terminates TLS → noVNC in browser)
```

---

## Security tooling

Every push runs a fan-out of blocking gates before an image can reach ECR, and
the cluster itself refuses anything that bypassed them.

| Stage              | Tool                  | Failing condition                  |
| ------------------ | --------------------- | ---------------------------------- |
| Secrets scan       | gitleaks              | any finding                        |
| Java SAST          | CodeQL (`ci.yml`)     | high + critical alerts             |
| Weekly deep SAST   | CodeQL (`codeql.yml`) | security-extended queries          |
| Dockerfile lint    | hadolint              | level = error                      |
| Image vulns        | Trivy (image)         | `HIGH`, `CRITICAL` (fixed)         |
| Misconfig scan     | Trivy (config)        | `HIGH`, `CRITICAL`                 |
| SBOM               | Syft                  | emitted as Sigstore attestation    |
| Image signing      | Cosign keyless        | gated at admission                 |
| IaC (TF)           | Checkov + tfsec       | any `HIGH` (gitops repo)           |
| K8s manifests      | kubeconform + kube-linter | lint errors (gitops repo)      |
| Runtime admission  | Kyverno               | policy violations (cluster)        |

---

## Setup

First-time provisioning (AWS, Terraform, secrets, DNS, first deploy) lives
in its own guide: **[`Asn2/DEPLOY.md`](Asn2/DEPLOY.md)**.

Skim it end-to-end before running anything — the steps are ordered and a
few of them (bootstrap → main → secrets → manifests → first push) depend
on each other.

---

## Day-to-day workflow

You only interact with *this* repo once setup is done:

1. Edit Java under `Asn2/src/`.
2. Push to a branch, open a PR. CI runs all gates + builds (no push to ECR).
3. Merge to `main`. Full pipeline → signed image → automated bump PR in gitops repo.
4. Merge the bump PR. Argo CD rolls the pod within 3 minutes.

Zero kubectl, zero terraform, zero manual AWS clicks.

---

## Local development

Plain Eclipse / IntelliJ works — the project has no build tool and no
dependencies. From `Asn2/`:

```bash
javac -d bin src/*.java
java -cp bin PlayGame 5 3 3
```

Or run the exact production container locally:

```bash
cd Asn2
docker build -t asn2-game:dev .
docker run --rm -p 6080:6080 asn2-game:dev
# open http://localhost:6080/vnc.html?autoconnect=1&resize=scale
```

Arguments: `BOARD_SIZE EMPTY_POSITIONS DEPTH` (settable via env or the
Kustomize Deployment env block).

---

## Troubleshooting

**`Resource not accessible by integration` in CodeQL**
Add `actions: read` to the job's `permissions:` block. Workflow-level
`permissions:` does not merge into a job that declares its own.

**`cosign: error signing` in CI**
The job's `permissions:` must include `id-token: write`. Check that
workflow-level `id-token: write` isn't being overridden by a narrower
job-level block.

**`AccessDenied` pushing to ECR**
Inspect the assumed-role session in CloudTrail. The OIDC trust policy
in `iam.tf` is pinned to:
- `repo:dhruwanga19/2210:ref:refs/heads/main`
- `repo:dhruwanga19/2210:pull_request`
If you renamed the repo or pushed from a different branch, update
`github_app_repo` in the gitops Terraform.

**ALB stuck at `<pending>`**
Check the LB controller:
```bash
kubectl -n kube-system logs deploy/aws-load-balancer-controller
```
Common causes: missing subnet tags (fixed in `vpc.tf`), quota exhaustion,
or the service account missing the IRSA annotation.

**Kyverno blocks the pod with "verify-cosign-signature" error**
The identity in `clusters/prod/workloads/kyverno-policies/verify-cosign-signature.yaml`
must match the actual workflow file path. If you renamed `ci.yml`, update
the policy's `subject:` regex. To bypass temporarily during debugging,
change `validationFailureAction: Enforce` → `Audit` on that policy only.

**noVNC loads but is blank**
Xvfb is up but `PlayGame` crashed. Check supervisord logs:
```bash
kubectl -n asn2 logs deploy/asn2-game -c game | tail -n 50
```
Most common cause is the GIF files not being present in `/app`; the Dockerfile
copies them explicitly, so a bad `.dockerignore` is the usual culprit.

**Image Updater doesn't bump the tag**
Argo CD Image Updater needs the IRSA role to read ECR. If you see `403`s in
its logs, verify the service account has the
`eks.amazonaws.com/role-arn` annotation and the role trust matches
`system:serviceaccount:argocd:argocd-image-updater`.

---

## Teardown

Order matters — Argo CD must be told to prune first, otherwise Terraform
will hang deleting the namespace.

```bash
# 1. Remove Argo CD managed resources
kubectl -n argocd delete application --all
kubectl -n argocd delete applicationset --all

# 2. Tear down Kubernetes-provisioned AWS resources (ALB, target groups)
kubectl -n asn2 delete ingress --all
sleep 60                    # let LB controller finalise

# 3. Terraform destroy
cd asn2-gitops/infra/terraform && terraform destroy
cd ../bootstrap             && terraform destroy

# 4. Delete any remaining S3 state bucket contents manually if destroy fails:
#    terraform's destroy leaves the bucket empty but present by design.
```

Route53 zone is referenced via data source — `destroy` leaves it alone.

---

## Costs

Rough on-demand monthly, `us-east-1`:

| Resource              | Cost     |
| --------------------- | -------- |
| EKS control plane     | $73      |
| 2 × t3.small (on-demand) | ~$30  |
| ALB + LCU             | ~$18     |
| NAT gateway (single)  | ~$32     |
| Route53 hosted zone   | $0.50    |
| ECR storage           | ~$0.10   |
| Data transfer (demo)  | ~$1–5    |
| **Total**             | **~$155** |

Cheaper variants:
- Nodes → 1 × t4g.small on Graviton: ~$10. Set `node_desired_size = 1`.
- Drop NAT gateway by moving nodes to a VPC with direct internet access
  (public subnet). Saves ~$32 but weakens the security posture.
- Run a scheduled action that scales the node group to 0 overnight and
  back up in the morning: cuts compute and ALB data ~60%.
- Fargate profile instead of managed nodes: pay per pod-second. For a
  single always-on pod this is close to parity; savings show up if the
  node group is underutilised.

---

## License

MIT for the infrastructure wrapper. The Java source under `Asn2/src/` is
coursework — do not reuse it wholesale in another academic submission.
