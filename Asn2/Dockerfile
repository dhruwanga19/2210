# syntax=docker/dockerfile:1.7

# ---- Stage 1: compile Java sources ----
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /build
COPY src/ ./src/
RUN mkdir -p /out && javac -d /out src/*.java

# ---- Stage 2: runtime with Xvfb + noVNC ----
FROM eclipse-temurin:17-jre

ENV DEBIAN_FRONTEND=noninteractive \
    DISPLAY=:99 \
    BOARD_SIZE=5 \
    EMPTY_POSITIONS=3 \
    DEPTH=3 \
    APP_UID=1000 \
    APP_GID=1000

RUN apt-get update && apt-get install -y --no-install-recommends \
        xvfb \
        x11vnc \
        fluxbox \
        novnc \
        websockify \
        python3 \
        supervisor \
        gosu \
        curl \
        ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && (userdel -r ubuntu 2>/dev/null || true) \
    && groupadd --system --gid ${APP_GID} app \
    && useradd  --system --uid ${APP_UID} --gid app --home-dir /app --shell /usr/sbin/nologin app

WORKDIR /app

# Compiled classes
COPY --from=builder /out /app/classes

# GIF assets must live in the JVM working directory because PlayGame loads
# them with relative paths (e.g. new ImageIcon("empty.gif"))
COPY *.gif /app/

COPY supervisord.conf /etc/supervisor/conf.d/game.conf
COPY k8s-probe.sh /usr/local/bin/k8s-probe
RUN chmod +x /usr/local/bin/k8s-probe \
    && chown -R app:app /app

# Writable locations for Xvfb + fluxbox when dropping privileges
RUN mkdir -p /tmp/.X11-unix /var/log/supervisor \
    && chmod 1777 /tmp/.X11-unix \
    && chown -R app:app /var/log/supervisor

EXPOSE 6080

# Liveness: noVNC web root must respond. Kubernetes probes call /usr/local/bin/k8s-probe directly.
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
    CMD curl -fsS http://localhost:6080/ >/dev/null || exit 1

# NOTE: supervisord runs as root because x11vnc/Xvfb bind to the display socket.
# The Java game itself is started via `gosu app` inside supervisord.conf so the
# actual PlayGame JVM runs as UID 1000 with no capabilities. Kyverno enforces
# no-privilege-escalation at the pod level.
CMD ["/usr/bin/supervisord", "-n", "-c", "/etc/supervisor/supervisord.conf"]
