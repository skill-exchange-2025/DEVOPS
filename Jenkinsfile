pipeline {
    agent any

    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "localhost:8081"
        NEXUS_REPOSITORY = "maven-releases"
        ARTIFACT_VERSION = "5.0.0"
        DOCKER_IMAGE = "aymenghazouani/4twin7-devops"
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials-id" // Replace with your credentials ID
        SONAR_TOKEN = "sqp_a43bbd79cf113ee9b2f88f4113da6c1434689c13" // Replace with your SonarQube token
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sonar-token-id', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                                -Dsonar.projectKey=your-project-key \
                                -Dsonar.host.url=http://http://192.168.50.4:9000 \
                                -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    def imageTag = "${DOCKER_IMAGE}:${ARTIFACT_VERSION}"
                    sh "docker build -t ${imageTag} ."
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh """
                            echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
                            docker push ${imageTag}
                        """
                    }
                }
            }
        }

        stage('Docker Compose Deploy') {
            steps {
                script {
                    def imageTag = "${DOCKER_IMAGE}:${ARTIFACT_VERSION}"
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh """
                            echo "${DOCKER_PASSWORD}" | docker login -u "${DOCKER_USERNAME}" --password-stdin
                            docker pull ${imageTag}
                            docker-compose down || true
                            docker-compose up -d --force-recreate
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}