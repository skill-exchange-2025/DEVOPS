pipeline {
    agent any

    tools {
        maven "M2_HOME"  // Ensure this matches your Jenkins Maven tool name
    }

    environment {
        DOCKER_IMAGE = 'islem/devops-master-backend:1.0.0'
        COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'islem', 
                     url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                stash includes: 'target/*.jar', name: 'app-jar'  // Stash for Docker stage
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
                junit 'target/surefire-reports/**/*.xml'  // Publish test results
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    unstash 'app-jar'  // Retrieve the JAR
                    withCredentials([usernamePassword(
                        credentialsId: 'islem',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh '''
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker build -t $DOCKER_IMAGE .
                            docker push $DOCKER_IMAGE
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Gracefully handle docker-compose absence
                try {
                    sh 'docker-compose -f $COMPOSE_FILE down || true'
                } catch (Exception e) {
                    echo "Warning: docker-compose not installed: ${e.message}"
                }
                cleanWs()
            }
        }
    }
}