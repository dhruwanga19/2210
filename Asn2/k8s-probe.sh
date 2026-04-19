#!/bin/sh
# Kubernetes readiness + liveness probe.
# - checks noVNC is serving its web UI on :6080
# - checks the Xvfb display socket exists
set -eu

curl -fsS http://127.0.0.1:6080/ >/dev/null
test -S /tmp/.X11-unix/X99
