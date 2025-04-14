pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'JAVA_HOME'
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        SONAR_HOST_URL = 'http://192.168.50.4:9000'
        DOCKER_IMAGE = 'jihedxx/tpfoyer:latest'
    }

    stages {
        stage('Debug Environment') {
            steps {
                sh 'echo "JAVA_HOME: $JAVA_HOME"'
                sh 'java -version || true'
                sh 'mvn -version || true'
            }
        }

        stage('Cloner le dépôt') {
            steps {
                git branch: 'jihed', url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
            }
        }

        stage('Build Spring Boot Project') {
            steps {
                sh 'mvn clean package '
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'TOKEN')]) {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=tpfoyer \
                        -Dsonar.host.url=$SONAR_HOST_URL \
                        -Dsonar.login=$TOKEN
                    """
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker build -t $DOCKER_IMAGE .
                        docker push $DOCKER_IMAGE
                    """
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up -d'
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline execution completed'
        }
    }
}
