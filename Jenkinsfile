pipeline {
    agent any

    tools {
        maven "M2_HOME"
    }

    environment {
        DOCKER_IMAGE = 'islem/devops-master-backend:1.0.0'  
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/skill-exchange-2025/DEVOPS.git'
            }
        }

        stage('Build') {
            steps {
                script {
                    // Build the project with Maven
                    sh 'mvn clean install'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    // Run unit tests
                    sh 'mvn test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def mvn = tool 'M2_HOME';
                    withSonarQubeEnv('scanner') {
                        sh "\"${mvn}/bin/mvn\" clean verify sonar:sonar -Dsonar.projectKey=DEVOPS -Dsonar.projectName='DEVOPS'"
                    }
                }
            }
        }

        // Docker Build and Push Stage
        stage('Docker Build and Push') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'islem', 
                                                      usernameVariable: 'DOCKER_USER', 
                                                      passwordVariable: 'DOCKER_PASS')]) {
                        // Docker login securely
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                        
                        // Build Docker image (assuming Dockerfile is in project root)
                        sh 'docker build -t $DOCKER_IMAGE -f Dockerfile .'
                        
                        // Push Docker image to Docker Hub
                        sh 'docker push $DOCKER_IMAGE'
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Le pipeline a réussi !'
        }
        failure {
            echo 'Le pipeline a échoué !'
        }
    }
}
