pipeline {
    agent any

    environment {
        APP_NAME = 'cicd-k8s-app'
        DOCKER_IMAGE = 'vladcitrus/cicd-k8s-app'
        K8S_NAMESPACE = 'cicd-k8s'
        K8S_DEPLOYMENT = 'cicd-k8s-app'
        K8S_CONTAINER = 'cicd-k8s-app'
        JAVA_HOME = 'C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.9.10-hotspot'
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git describe --tags --always > version.txt'
                script {
                    env.APP_VERSION = readFile('version.txt').trim()
                }
                echo "Application version: ${env.APP_VERSION}"
            }
        }

        stage('Test') {
            steps {
                dir('app') {
                    bat 'mvn test'
                }
            }
        }

        stage('Build Docker image') {
            steps {
                dir('app') {
                    bat '''
                    docker build ^
                      -t %DOCKER_IMAGE%:%APP_VERSION% ^
                      -t %DOCKER_IMAGE%:latest ^
                      .
                    '''
                }
            }
        }

        stage('Push Docker image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKERHUB_USERNAME',
                    passwordVariable: 'DOCKERHUB_TOKEN'
                )]) {
                    bat '''
                    echo %DOCKERHUB_TOKEN% | docker login -u %DOCKERHUB_USERNAME% --password-stdin
                    docker push %DOCKER_IMAGE%:%APP_VERSION%
                    docker push %DOCKER_IMAGE%:latest
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                bat '''
                kubectl apply -f k8s\\namespace.yaml
                kubectl apply -f k8s\\deployment.yaml
                kubectl apply -f k8s\\service.yaml
                kubectl apply -f k8s\\ingress.yaml

                kubectl set image deployment/%K8S_DEPLOYMENT% ^
                  %K8S_CONTAINER%=%DOCKER_IMAGE%:%APP_VERSION% ^
                  -n %K8S_NAMESPACE%

                kubectl set env deployment/%K8S_DEPLOYMENT% ^
                    APP_VERSION=%APP_VERSION% ^
                    -n %K8S_NAMESPACE%

                kubectl rollout status deployment/%K8S_DEPLOYMENT% -n %K8S_NAMESPACE%
                '''
            }
        }
    }
}
