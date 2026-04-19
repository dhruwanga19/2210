# syntax=docker/dockerfile:1.7

# ---- Stage 1: compile Java sources ----
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /build
COPY src/ ./src/
RUN mkdir -p /out && javac -d /out src/*.java

# ---- Stage 2: runtime with Xvfb + noVNC ----
FROM debian:stable-slim

ENV DEBIAN_FRONTEND=noninteractive \
    DISPLAY=:99 \
    BOARD_SIZE=5 \
    EMPTY_POSITIONS=3 \
    DEPTH=3

RUN apt-get update && apt-get install -y --no-install-recommends \
        openjdk-17-jre \
        xvfb \
        x11vnc \
        fluxbox \
        novnc \
        websockify \
        python3 \
        supervisor \
        ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Compiled classes
COPY --from=builder /out /app/classes

# GIF assets must live in the JVM working directory because PlayGame loads
# them with relative paths (e.g. new ImageIcon("empty.gif"))
COPY *.gif /app/

COPY supervisord.conf /etc/supervisor/conf.d/game.conf

EXPOSE 6080

CMD ["/usr/bin/supervisord", "-n", "-c", "/etc/supervisor/supervisord.conf"]
