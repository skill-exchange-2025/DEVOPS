pipeline {
    agent any
    tools { maven "M2_HOME" }  // Ensure this matches Jenkins' Maven tool name

    environment {
        DOCKER_IMAGE = 'islem/devops-master-backend:1.0.0'
        COMPOSE_FILE = 'docker-compose.yml'
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                stash includes: 'target/*.jar', name: 'app-jar'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/**/*.xml'
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
                try {
                    sh 'docker-compose -f $COMPOSE_FILE down || true'
                } catch (Exception e) {
                    echo "Skipping docker-compose (not installed)"
                }
                cleanWs()
            }
        }
    }
}