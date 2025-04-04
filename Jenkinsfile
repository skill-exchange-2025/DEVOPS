pipeline {
    agent any

    tools {
        maven "M2_HOME"  // Ensure this tool is configured in Jenkins Global Tools
    }

    environment {
        DOCKER_IMAGE = 'islem/devops-master-backend:1.0.0'
        COMPOSE_FILE = 'docker-compose.yml'  // Explicit compose file declaration
    }

    options {
        timeout(time: 30, unit: 'MINUTES')  // Prevent indefinite hangs
        buildDiscarder(logRotator(numToKeepStr: '5'))  // Keep only last 5 builds
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'islem',
                     url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
                sh 'ls -la'  // Debug file listing
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                stash includes: 'target/*.jar', name: 'app-jar'  // Cache the built artifact
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('scanner') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=DEVOPS -Dsonar.projectName=DEVOPS'
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                script {
                    unstash 'app-jar'  // Retrieve the built JAR
                    
                    withCredentials([usernamePassword(
                        credentialsId: 'islem',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh '''
                            # Secure login and build with error handling
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin || exit 1
                            docker build -t $DOCKER_IMAGE . || exit 1
                            docker push $DOCKER_IMAGE || exit 1
                        '''
                    }
                }
            }
        }

        stage('Docker Compose Up') {
            steps {
                script {
                    sh """
                        docker-compose -f $COMPOSE_FILE build --no-cache
                        docker-compose -f $COMPOSE_FILE up -d
                        sleep 30  # Wait for services
                        docker-compose -f $COMPOSE_FILE ps -a
                        docker-compose -f $COMPOSE_FILE logs --tail=50  # Show recent logs
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                sh 'docker-compose -f $COMPOSE_FILE down || true'  // Graceful cleanup
                cleanWs()  // Clean workspace
            }
        }
        success {
            slackSend color: 'good', message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        failure {
            slackSend color: 'danger', message: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            archiveArtifacts artifacts: '**/target/*.log', allowEmptyArchive: true
        }
    }
}