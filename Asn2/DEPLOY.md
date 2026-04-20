# Deployment guide — Asn2 → EKS

End-to-end setup for the Asn2 game. Expect ~45 minutes, most of which is the
EKS control plane provisioning and ACM/DNS validation.

For the high-level architecture, security matrix, day-to-day workflow,
troubleshooting, teardown, and cost tables, see the top-level
[README](../README.md). This file covers **only** the one-time setup steps.

---

## Prerequisites

Accounts:
- **AWS** with admin (or equivalent) for first setup. Set a billing alert.
- **GitHub** account owning both repos (`dhruwanga19/2210`,
  `dhruwanga19/asn2-gitops`).
- **Domain** with a **Route53 public hosted zone**. The plan uses
  `game.<your-domain>` — you can also delegate a subdomain into Route53.

Local tools (bootstrap + debugging only; day-to-day work is via CI):

```
aws-cli            >= 2.15
terraform          >= 1.6      (tested on 1.9.8)
kubectl            >= 1.30
docker             (optional, for local smoke tests)
cosign             (optional, for verifying published images)
gh                 (optional, for PR ops)
```

---

## 1. Clone both repos

```bash
git clone https://github.com/dhruwanga19/2210.git
# Create the gitops repo on GitHub (empty, public or private), then:
git clone https://github.com/dhruwanga19/asn2-gitops.git
```

The app repo is this one. The gitops repo is scaffolded in a separate pass
and has its own README with the expected tree.

## 2. Configure AWS access locally

```bash
aws configure           # or `aws sso login --profile …`
aws sts get-caller-identity   # sanity check
```

## 3. Bootstrap Terraform state + GitHub OIDC

One-time, from the **gitops repo**:

```bash
cd asn2-gitops/infra/bootstrap
terraform init
terraform apply -var='region=us-east-1'
```

Outputs:
- `state_bucket` — S3 bucket for remote Terraform state
- `github_oidc_provider_arn` — OIDC provider the main stack consumes via data source

If the bucket name is already taken globally, override with
`-var='state_bucket_name=...'` and update `infra/terraform/versions.tf` to match.

## 4. Main Terraform apply

```bash
cd ../terraform
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars:
#   domain_name = "your-real-domain.com"
#   region      = "us-east-1"
terraform init
terraform apply
```

Provisions VPC, EKS 1.30, node group, ECR, IAM/OIDC roles, ACM cert, and
Argo CD via Helm. Takes 15–20 minutes.

Grab the outputs:

```bash
terraform output github_deploy_role_arn   # → GitHub secret in both repos
terraform output ecr_repository_url       # → gitops kustomization.yaml
terraform output game_certificate_arn     # → gitops ingress.yaml
terraform output kubeconfig_command       # run now to get kubectl access
```

## 5. Wire kubectl

```bash
$(terraform output -raw kubeconfig_command)
kubectl get nodes                # 2 × t3.small
kubectl -n argocd get pods       # argocd-server et al. Running
```

## 6. GitHub secrets + environment

On **`dhruwanga19/2210`** → Settings → Secrets and variables → Actions:

| Secret | Value |
| --- | --- |
| `AWS_DEPLOY_ROLE_ARN` | `terraform output github_deploy_role_arn` |
| `GITOPS_PAT`          | fine-grained PAT with `contents:write` + `pull_requests:write` scoped to `dhruwanga19/asn2-gitops` |

Also flip **Actions → Workflow permissions → Read and write**.

On **`dhruwanga19/asn2-gitops`**:

| Secret | Value |
| --- | --- |
| `AWS_DEPLOY_ROLE_ARN` | same ARN as above |

Then Settings → **Environments → New environment → `production`** →
add yourself as a **required reviewer**. This gates `terraform apply` on
human approval.

## 7. Update GitOps manifests with the real ARNs

In the gitops repo, edit
`clusters/prod/workloads/asn2-game/ingress.yaml`:

```yaml
alb.ingress.kubernetes.io/certificate-arn: <paste game_certificate_arn>
external-dns.alpha.kubernetes.io/hostname: game.<your-domain>
```

And `clusters/prod/workloads/asn2-game/kustomization.yaml`:

```yaml
images:
  - name: asn2-game
    newName: <paste ecr_repository_url>
    newTag: sha-PLACEHOLDER   # CI will rewrite on first push
```

Commit + push. Argo CD picks up the change; the Application sits in a
`Missing` state until the first real image arrives — that's expected.

## 8. First image build

```bash
touch Asn2/.ci-kick && git add Asn2/.ci-kick \
  && git commit -m "Trigger first CI" && git push
```

Watch the pipeline:
1. Gates (gitleaks / CodeQL / hadolint) run in parallel — ~2 min.
2. Build → SBOM → Trivy → push to ECR → cosign sign — ~4 min.
3. `bump-gitops` job opens a PR against `asn2-gitops`.

Merge that PR. Within 3 min Argo CD syncs, the pod starts, the ALB comes up
(~90s), Route53 propagates, and `https://game.<your-domain>/` serves the
auto-connecting noVNC client.

## 9. Verify end-to-end

```bash
# Pipelines green
gh run list --repo dhruwanga19/2210 --limit 3

# Image exists in ECR
aws ecr describe-images --repository-name asn2-game \
  --query 'imageDetails[0].imageTags'

# Signature verifies
cosign verify \
  --certificate-identity-regexp 'https://github.com/dhruwanga19/2210' \
  --certificate-oidc-issuer https://token.actions.githubusercontent.com \
  $(aws ecr describe-repositories --repository-names asn2-game \
      --query 'repositories[0].repositoryUri' --output text):latest

# Cluster state
kubectl -n argocd get app
kubectl -n asn2 get pods
kubectl -n asn2 get ingress       # ADDRESS column = ALB hostname

# End user
curl -sI https://game.<your-domain>/ | head -n 3
open https://game.<your-domain>/
```

Done. From here, push Java changes to `main` and the pipeline takes over —
see [README → Day-to-day workflow](../README.md#day-to-day-workflow).
