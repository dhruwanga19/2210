# Deploying Asn2 (Two-Player Dynamic Game)

The Swing GUI runs inside a container with `Xvfb` + `x11vnc` + `noVNC`, so it
is playable in a browser over HTTPS. GitHub Actions builds the image on every
push to `main`, publishes it to GHCR, and SSHes into the VPS to roll it out.

```
Browser ──HTTPS──▶ Caddy (:443) ──▶ noVNC (:6080) ──▶ x11vnc (:5900) ──▶ Xvfb (:99) ──▶ PlayGame
```

---

## 1. One-time VPS setup

Tested on Ubuntu 22.04+. Any cloud VPS with a public IP works (Oracle free
tier ARM, DO, Lightsail, Hetzner, etc.).

```bash
# as root, on the VPS
adduser --disabled-password --gecos "" deploy
usermod -aG sudo deploy

# Docker Engine + Compose plugin
curl -fsSL https://get.docker.com | sh
usermod -aG docker deploy

# Firewall
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable
```

Install an SSH key for the `deploy` user (the matching private key becomes
the `VPS_SSH_KEY` GitHub secret):

```bash
mkdir -p /home/deploy/.ssh
# paste the PUBLIC key into /home/deploy/.ssh/authorized_keys
chown -R deploy:deploy /home/deploy/.ssh
chmod 700 /home/deploy/.ssh
chmod 600 /home/deploy/.ssh/authorized_keys
```

Point DNS at the VPS. Create an **A** record `game.<your-domain>` → VPS IP.
If using Cloudflare, keep the record on **DNS only** (grey cloud) so Caddy
can solve the ACME HTTP-01 challenge.

Seed the host directory:

```bash
# as 'deploy' on the VPS
mkdir -p ~/asn2 && cd ~/asn2
# Copy Caddyfile and docker-compose.yml out of the repo (or scp them):
scp you@local:/path/to/Asn2/Caddyfile .
scp you@local:/path/to/Asn2/docker-compose.yml .

cat > .env <<'EOF'
GAME_DOMAIN=game.yourdomain.com
IMAGE_TAG=latest
EOF

docker compose up -d
```

The first start asks Caddy to obtain a certificate; watch `docker logs asn2-caddy`
until you see `certificate obtained successfully`.

Make the GHCR image public once (Settings → Packages → asn2-game → Change
visibility → Public), otherwise the VPS needs a `docker login ghcr.io`.

---

## 2. GitHub Actions secrets

On the `dhruwanga19/2210` repo → Settings → Secrets and variables → Actions:

| Secret         | Value                                        |
| -------------- | -------------------------------------------- |
| `VPS_HOST`     | public IP or hostname of the VPS             |
| `VPS_USER`     | `deploy`                                     |
| `VPS_SSH_KEY`  | full private key (PEM) matching authorized_keys |

Also: Settings → Actions → General → **Workflow permissions** → *Read and write
permissions*. This lets the built-in `GITHUB_TOKEN` push to GHCR.

---

## 3. Local smoke test before pushing

```bash
cd Asn2
docker build -t asn2-game .
docker run --rm -p 6080:6080 asn2-game
# open http://localhost:6080/vnc.html?autoconnect=1&resize=scale
```

You should land on the noVNC client and see the game board.

---

## 4. Day-to-day flow

1. Edit Java under `Asn2/src/`, commit, push to `main`.
2. GitHub Actions `deploy-asn2.yml` runs: builds multi-arch image, pushes to
   `ghcr.io/dhruwanga19/asn2-game`, then SSHes in and runs
   `docker compose pull && docker compose up -d`.
3. Reload `https://game.<your-domain>.com/` — the new build is live.

## 5. Tuning the game args

The container reads `BOARD_SIZE`, `EMPTY_POSITIONS`, and `DEPTH` env vars
(defaults: `5 3 3`). Set them in `~/asn2/.env` or uncomment the `environment:`
block in `docker-compose.yml` and re-run `docker compose up -d`.

## 6. Troubleshooting

- **"connection timed out" in noVNC** — Caddy couldn't reach `game:6080`.
  Check `docker logs asn2-game`; supervisord prints startup progress for each
  program.
- **Certificate errors** — ensure the DNS A record resolves to the VPS and
  port 80 is reachable from the internet (ACME HTTP-01).
- **Game exits when a user closes the window** — that's expected;
  `autorestart=true` in `supervisord.conf` brings it right back.
