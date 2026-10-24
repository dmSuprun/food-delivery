FROM jenkins/jenkins:lts-jdk21
USER root
# Docker CLI with buildx plugin; the daemon is the host's one via the mounted /var/run/docker.sock
COPY --from=docker:27-cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker:27-cli /usr/local/libexec/docker/cli-plugins/docker-buildx /usr/local/libexec/docker/cli-plugins/docker-buildx
# Jenkins runs as root (for docker.sock) while jenkins_home is owned by uid 1000:
# without this git refuses the workspace with "detected dubious ownership"
RUN git config --system --add safe.directory '*'
