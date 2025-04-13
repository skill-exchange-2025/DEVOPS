pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'JAVA_HOME'
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials') // Create this in Jenkins
        IMAGE_NAME = "farouksouei/tpfoyer"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Debug Environment') {
            steps {
                sh 'echo "JAVA_HOME: $JAVA_HOME"'
                sh 'echo "PATH: $PATH"'
                sh 'java -version || true'
                sh 'mvn -version || true'
                sh 'docker --version || true'
            }
        }

        stage('Clone Repository') {
            steps {
                git branch: 'mohamedfarouksouei-4twin7', url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Login to Docker Hub') {
            steps {
                sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "docker push ${IMAGE_NAME}:latest"
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                // Update image in docker-compose.yml to use the pushed one
                sh """
                sed -i 's|build: .|image: ${IMAGE_NAME}:${IMAGE_TAG}|g' docker-compose.yml
                docker-compose down || true
                docker-compose up -d
                """
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed'
            sh 'docker logout'
        }
        success {
            echo 'Successfully built and deployed the application'
        }
        failure {
            echo 'Build or deployment failed'
        }
        cleanup {
            // Clean up Docker images to avoid disk space issues
            sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
            // Clean up unused Docker resources
            sh 'docker system prune -f || true'
        }
    }
}