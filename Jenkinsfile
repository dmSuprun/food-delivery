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
        // ACR from the lab 1 plan; Docker and AKS steps are simulated
        REGISTRY = 'acrfooddelivery.azurecr.io'
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
                    for dir in services/*/; do
                        svc=$(basename "$dir")
                        test -f "${dir}target/$svc.jar"
                        echo "[SIMULATION] docker build -t $REGISTRY/$svc:$IMAGE_TAG $dir"
                        echo "$REGISTRY/$svc:$IMAGE_TAG" >> images.txt
                    done
                '''
                archiveArtifacts 'images.txt'
            }
        }

        stage('Docker push') {
            steps {
                sh '''
                    echo "[SIMULATION] az acr login --name ${REGISTRY%%.*}"
                    while read -r image; do
                        echo "[SIMULATION] docker push $image"
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
                    echo "[SIMULATION] kubectl -n staging rollout status deployment --timeout=120s"
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
                    echo "[SIMULATION] kubectl -n production rollout status deployment --timeout=120s"
                    echo "Production deployment finished: $IMAGE_TAG"
                '''
            }
        }
    }

    post {
        success { echo 'CI/CD passed: built, tested, packaged and deployed to staging and production.' }
        aborted { echo 'Pipeline aborted: production deployment was not approved.' }
        failure { echo 'CI/CD failed: check the stage logs above.' }
    }
}
