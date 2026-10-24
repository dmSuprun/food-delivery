pipeline {
    agent any

    tools {
        // Manage Jenkins -> Tools -> Maven installations -> Name: maven-3.9
        maven 'maven-3.9'
    }

    triggers {
        // Local Jenkins can't receive GitHub webhooks: poll the repo every ~2 min
        pollSCM('H/2 * * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    environment {
        // Local registry (registry:2) stands in for Azure Container Registry
        REGISTRY = 'localhost:5000'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.IMAGE_TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
                echo "Image tag: ${env.IMAGE_TAG}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B -ntp clean compile'
            }
        }

        stage('Unit tests') {
            steps {
                sh 'mvn -B -ntp test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -ntp package -DskipTests'
                archiveArtifacts 'services/*/target/*.jar'
            }
        }

        stage('Docker build') {
            steps {
                sh '''
                    rm -f images.txt
                    for dir in services/*/ frontend/; do
                        name=$(basename "$dir")
                        docker build -t "$REGISTRY/$name:$IMAGE_TAG" -t "$REGISTRY/$name:latest" "$dir"
                        echo "$REGISTRY/$name:$IMAGE_TAG" >> images.txt
                    done
                    docker images --filter "reference=$REGISTRY/*:$IMAGE_TAG"
                '''
                archiveArtifacts 'images.txt'
            }
        }

        stage('Container smoke test') {
            steps {
                sh '''
                    for dir in services/*/; do
                        name=$(basename "$dir")
                        docker run -d --name "smoke-$name" "$REGISTRY/$name:$IMAGE_TAG" > /dev/null
                    done
                    for dir in services/*/; do
                        name=$(basename "$dir")
                        status=starting
                        for i in $(seq 1 30); do
                            status=$(docker inspect -f '{{.State.Health.Status}}' "smoke-$name")
                            [ "$status" = "healthy" ] && break
                            sleep 2
                        done
                        echo "$name: $status"
                        [ "$status" = "healthy" ] || exit 1
                    done
                '''
            }
            post {
                always {
                    sh 'docker ps -aq --filter "name=smoke-" | xargs -r docker rm -f > /dev/null'
                }
            }
        }

        stage('Docker push') {
            steps {
                sh '''
                    while read -r image; do
                        docker push "$image"
                        docker push "${image%:*}:latest"
                    done < images.txt
                '''
            }
        }

        stage('Deploy to staging') {
            steps {
                sh '''
                    echo "[SIMULATION] az aks get-credentials --name aks-fooddelivery-staging"
                    while read -r image; do
                        svc=$(echo "$image" | cut -d/ -f2 | cut -d: -f1)
                        echo "[SIMULATION] kubectl -n staging set image deployment/$svc $svc=$image"
                    done < images.txt
                    echo "Staging deployment finished: $IMAGE_TAG"
                '''
            }
        }

        stage('Approval') {
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    script {
                        env.APPROVER = input(message: "Deploy ${env.IMAGE_TAG} to production?",
                                             ok: 'Deploy', submitterParameter: 'APPROVER')
                    }
                }
                echo "Production deployment approved by: ${env.APPROVER}"
            }
        }

        stage('Deploy to production') {
            steps {
                sh '''
                    echo "[SIMULATION] az aks get-credentials --name aks-fooddelivery-production"
                    while read -r image; do
                        svc=$(echo "$image" | cut -d/ -f2 | cut -d: -f1)
                        echo "[SIMULATION] kubectl -n production set image deployment/$svc $svc=$image"
                    done < images.txt
                    echo "Production deployment finished: $IMAGE_TAG"
                '''
            }
        }
    }

    post {
        success { echo 'CI/CD passed: images built, smoke-tested, pushed to the registry and deployed.' }
        aborted { echo 'Pipeline aborted: production deployment was not approved.' }
        failure { echo 'CI/CD failed: check the stage logs above.' }
    }
}
