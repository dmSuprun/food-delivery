FROM jenkins/jenkins:lts-jdk21
USER root
# Docker CLI with buildx plugin; the daemon is the host's one via the mounted /var/run/docker.sock
COPY --from=docker:27-cli /usr/local/bin/docker /usr/local/bin/docker
COPY --from=docker:27-cli /usr/local/libexec/docker/cli-plugins/docker-buildx /usr/local/libexec/docker/cli-plugins/docker-buildx
# Jenkins runs as root (for docker.sock) while jenkins_home is owned by uid 1000:
# without this git refuses the workspace with "detected dubious ownership"
RUN git config --system --add safe.directory '*'
# kubectl and Helm for the Kubernetes deploy stages (stage 3 of the plan)
ARG KUBECTL_VERSION=v1.35.0
ARG HELM_VERSION=v4.1.4
RUN curl -fsSL -o /usr/local/bin/kubectl "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/amd64/kubectl" \
 && chmod +x /usr/local/bin/kubectl \
 && curl -fsSL "https://get.helm.sh/helm-${HELM_VERSION}-linux-amd64.tar.gz" | tar -xz -C /tmp \
 && mv /tmp/linux-amd64/helm /usr/local/bin/helm \
 && rm -rf /tmp/linux-amd64
