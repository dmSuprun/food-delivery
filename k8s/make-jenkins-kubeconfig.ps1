# Builds a kubeconfig for the jenkins-deployer service account.
# Usage: .\make-jenkins-kubeconfig.ps1 -Out $HOME\jenkins-kubeconfig.yaml
# The file contains a token: upload it to Jenkins as a "Secret file" and keep it out of git.
param(
    [string]$Out = "$HOME\jenkins-kubeconfig.yaml",
    # Kubernetes API address as seen from the Jenkins container (Rancher Desktop VM)
    [string]$Server = "https://192.168.127.2:6443",
    [string]$Context = "rancher-desktop"
)

$tokenB64 = kubectl --context $Context -n jenkins get secret jenkins-deployer-token -o jsonpath='{.data.token}'
$caB64 = kubectl --context $Context -n jenkins get secret jenkins-deployer-token -o jsonpath='{.data.ca\.crt}'
if (-not $tokenB64 -or -not $caB64) { throw "Secret jenkins-deployer-token not found: apply k8s/jenkins-deployer.yaml first" }
$token = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($tokenB64))

@"
apiVersion: v1
kind: Config
clusters:
- name: rancher-desktop
  cluster:
    server: $Server
    certificate-authority-data: $caB64
users:
- name: jenkins-deployer
  user:
    token: $token
contexts:
- name: jenkins
  context:
    cluster: rancher-desktop
    user: jenkins-deployer
current-context: jenkins
"@ | Set-Content -Encoding ascii -Path $Out

Write-Host "kubeconfig written to $Out"
