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

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --oneline'
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
    }

    post {
        success { echo 'CI passed: all microservices built and unit-tested.' }
        failure { echo 'CI failed: check the stage logs above.' }
    }
}
