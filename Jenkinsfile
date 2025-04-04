pipeline {
    agent any

    tools {
        maven "M2_HOME"
    }

    environment {
        DOCKER_IMAGE = 'islem/devops-master-backend:1.0.0'
        GIT_PATH = '/mingw64/bin/git'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'islem',  // Check out the correct branch
             url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
        sh 'ls -la'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'  // Matches Dockerfile
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def mvn = tool 'M2_HOME';
                    withSonarQubeEnv('scanner') {
                        sh "\"${mvn}/bin/mvn\" sonar:sonar -Dsonar.projectKey=DEVOPS -Dsonar.projectName='DEVOPS'"
                    }
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'islem',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                        sh 'docker build -t $DOCKER_IMAGE .'  // Build from Dockerfile
                        sh 'docker push $DOCKER_IMAGE'
                    }
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                script {
                    sh 'docker-compose build'  // Rebuild services if needed
                    sh 'docker-compose up -d'  // Detached mode
                    sh 'sleep 10'  // Wait for containers to initialize
                    sh 'docker ps'  // Verify containers are running
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}