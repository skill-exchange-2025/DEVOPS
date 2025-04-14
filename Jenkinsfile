pipeline {
    agent any

    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "localhost:8081"
        NEXUS_REPOSITORY = "maven-releases"
        ARTIFACT_VERSION = "5.0.0"
        DOCKER_IMAGE = "aymenghazouani/4twin7-devops"
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials-id"
        SONAR_TOKEN = credentials('sonarqube-token')
        SONAR_HOST_URL = "http://localhost:9000"
        DOCKER_CACHE = "/path/to/docker/cache"
        IMAGE_NAME = "aymenghazouani/4twin7-devops"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('MySonarQubeServer') {
                        sh 'mvn clean verify sonar:sonar'
                    }
                }
            }
        }

        stage('Check Quality Gate') {
            steps {
                script {
                    try {
                        timeout(time: 1, unit: 'MINUTES') {
                            sh """
                            set -e
                            sleep 10
                            TASK_STATUS=\$(curl -s -u "${SONAR_TOKEN}:" "${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=devops" | grep -o '"status":"[^"]*"' | cut -d':' -f2 | tr -d '"')
                            if [ "\$TASK_STATUS" = "ERROR" ]; then
                                echo "Quality Gate failed!"
                            else
                                echo "Quality Gate passed!"
                            fi
                            """
                        }
                    } catch (Exception e) {
                        echo "Quality Gate check failed: ${e.message}"
                    }
                }
            }
        }

        stage('Setup Docker Cache') {
            steps {
                sh 'mkdir -p ${DOCKER_CACHE}'
                sh '''
                if [ -d ${DOCKER_CACHE} ] && [ "$(ls -A ${DOCKER_CACHE})" ]; then
                    echo "Restoring Docker cache..."
                    find ${DOCKER_CACHE} -name "*.tar" -exec docker load -i {} \\;
                fi
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                export DOCKER_BUILDKIT=1
                docker build --cache-from ${DOCKER_IMAGE}:latest -t ${DOCKER_IMAGE}:${IMAGE_TAG} .
                docker tag ${DOCKER_IMAGE}:${IMAGE_TAG} ${DOCKER_IMAGE}:latest
                mkdir -p ${DOCKER_CACHE}
                SAFE_IMAGE_NAME=$(echo ${DOCKER_IMAGE} | tr '/' '_')
                docker save ${DOCKER_IMAGE}:latest -o ${DOCKER_CACHE}/${SAFE_IMAGE_NAME}-latest.tar
                '''
            }
        }

        stage('Login to Docker Hub') {
            steps {
                sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh "docker push ${DOCKER_IMAGE}:${IMAGE_TAG}"
                sh "docker push ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh """
                sed -i 's|build: .|image: ${DOCKER_IMAGE}:${IMAGE_TAG}|g' docker-compose.yml
                docker-compose down || true
                docker-compose up -d
                """
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed'
            sh 'docker logout || true'
            archiveArtifacts artifacts: '**/target/surefire-reports/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: '**/target/site/jacoco/**/*', allowEmptyArchive: true
            sh 'tar -czf maven-cache.tar.gz -C ${WORKSPACE} .m2 || true'
            archiveArtifacts artifacts: 'maven-cache.tar.gz', allowEmptyArchive: true
        }
        success {
            echo 'Successfully built and deployed the application'
        }
        failure {
            echo 'Build or deployment failed'
        }
        cleanup {
            echo "Cleaning up Docker images..."
            sh "docker rmi ${DOCKER_IMAGE}:${IMAGE_TAG} || true"
            sh "docker rmi ${DOCKER_IMAGE}:latest || true"
            sh 'docker system prune -f || true'
        }
    }
}
