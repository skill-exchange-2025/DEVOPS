pipeline {
    agent any

    tools {
        maven "M2_HOME"
    }

    environment {
        DOCKER_IMAGE = 'islem/devops-master-backend:1.0.0'
        COMPOSE_FILE = 'docker-compose.yml'
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"  // Local repo caching
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
        retry(2)  // Retry failed builds once
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'islem',
                     url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
                sh 'ls -la'
            }
        }

        stage('Cache Dependencies') {
            steps {
                sh 'mvn dependency:go-offline'
                stash includes: '.m2/repository/**', name: 'm2-cache'  // Cache local repo
            }
        }

        stage('Build') {
            steps {
                unstash 'm2-cache'  // Restore cached dependencies
                sh 'mvn clean package -DskipTests'
                stash includes: 'target/*.jar', name: 'app-jar'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
                junit 'target/surefire-reports/**/*.xml'  // Better test reporting
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
                    unstash 'app-jar'
                    
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
                        sleep 30
                        docker-compose -f $COMPOSE_FILE ps -a
                        docker-compose -f $COMPOSE_FILE logs --tail=50
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                sh 'docker-compose -f $COMPOSE_FILE down || true'
                cleanWs()
            }
        }
        success {
            slackSend color: 'good', message: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
        failure {
            slackSend color: 'danger', 
                     message: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER} (${BUILD_URL})"
            archiveArtifacts artifacts: '**/target/*.log,**/target/surefire-reports/**', allowEmptyArchive: true
        }
    }
}