pipeline {
    agent any
    tools { maven "M2_HOME" }  // Ensure this matches Jenkins' Maven tool name

    environment {
        DOCKER_IMAGE = 'sloumaaa333/devops-master-backend:1.0.0'
        COMPOSE_FILE = 'docker-compose.yml'
        GIT_TOKEN = credentials('gittoken')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Load Git Token') {
            steps {
                script {
                    echo "Git Token loaded from Jenkins credentials."
                }
            }
        }

        stage('Build') {
            steps {
                // Set up Git to use the token for authentication
                sh """
                    git config --global url."https://$GIT_TOKEN@github.com".insteadOf "https://github.com"
                """

                // Build using Maven
                sh 'mvn clean package -DskipTests'

                // Stash the app JAR for later use
                stash includes: 'target/*.jar', name: 'app-jar'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    // Execute SonarQube analysis using Maven
                    withSonarQubeEnv('scanner') {
                        sh 'mvn sonar:sonar -Dsonar.projectKey=your_project_key -Dsonar.host.url=http://192.168.56.10:9000/'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/**/*.xml'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([
                    configFile(
                        fileId: 'deploymentRepo',
                        variable: 'MAVEN_SETTINGS',
                        replaceTokens: true
                    )
                ]) {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'deploymentRepo',
                            usernameVariable: 'NEXUS_USER',
                            passwordVariable: 'NEXUS_PASS'
                        )
                    ]) {
                        sh '''
                            echo "==== DEBUG INFORMATION ===="
                            echo "Nexus User: $NEXUS_USER"
                            ls -l $MAVEN_SETTINGS  # Check if file exists
                            cat $MAVEN_SETTINGS    # Verify content
                            echo "========================"

                            mvn -s $MAVEN_SETTINGS -X deploy -DskipTests
                        '''
                    }
                }
            }
        }

        stage('Docker Version Check') {
            steps {
                sh 'docker --version'
            }
        }

        stage('Docker Build and Push') {
            environment {
                DOCKER_IMAGE = "sloumaaa333/devops-master-backend:1.0.0"
            }
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

        stage('Docker Compose Up') {
            steps {
                script {
                    // Adjust the directory to where your docker-compose.yml file is
                    dir('C:/Users/islem/Desktop/devops/DEVOPS-master') {
                        sh 'docker-compose -f ${COMPOSE_FILE} up -d'  // -d for detached mode (background)
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                try {
                    // Ensure docker-compose is installed
                    sh 'docker-compose -f ${COMPOSE_FILE} down || true'
                } catch (Exception e) {
                    echo "Skipping docker-compose (not installed)"
                }
                cleanWs() // Clean the workspace here (no need for `node` block)
            }
        }
    }
}
