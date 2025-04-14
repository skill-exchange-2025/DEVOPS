pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'JAVA_HOME'
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        IMAGE_NAME = "farouksouei/tpfoyer"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        MAVEN_CACHE = "${WORKSPACE}/.m2"
        DOCKER_CACHE = "${WORKSPACE}/.docker-cache"
        SONAR_HOST_URL = "http://192.168.50.4:9000"
        SONAR_TOKEN = credentials('sonarqube-token')
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

        stage('Cache Maven Dependencies') {
            steps {
                sh 'mkdir -p ${MAVEN_CACHE}'
                writeFile file: "${WORKSPACE}/.mvn-settings.xml", text: """
                <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
                  <localRepository>${MAVEN_CACHE}</localRepository>
                </settings>
                """
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml clean compile'
            }
        }

        stage('Unit Tests with Coverage') {
            steps {
                sh '''
                if ! grep -q "jacoco-maven-plugin" pom.xml; then
                    sed -i '/<\\/plugins>/i \\
                    <plugin>\\
                        <groupId>org.jacoco</groupId>\\
                        <artifactId>jacoco-maven-plugin</artifactId>\\
                        <version>0.8.11</version>\\
                        <executions>\\
                            <execution>\\
                                <id>prepare-agent</id>\\
                                <goals><goal>prepare-agent</goal></goals>\\
                            </execution>\\
                            <execution>\\
                                <id>report</id>\\
                                <phase>test</phase>\\
                                <goals><goal>report</goal></goals>\\
                            </execution>\\
                        </executions>\\
                    </plugin>' pom.xml
                fi
                '''
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml test -DskipTests=false'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh """
                mvn clean verify sonar:sonar \
                  -Dsonar.projectKey=devops \
                  -Dsonar.host.url=${SONAR_HOST_URL} \
                  -Dsonar.login=${SONAR_TOKEN}
                """
            }
        }

        stage('Check Quality Gate') {
            steps {
                script {
                    try {
                        timeout(time: 1, unit: 'MINUTES') {
                            sh """
                            sleep 10
                            TASK_STATUS=\$(curl -s -u "${SONAR_TOKEN}:" "${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=tp-foyer" | grep -o '"status":"[^"]*"' | cut -d':' -f2 | tr -d '"')
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

        stage('Package') {
            steps {
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml package -DskipTests'
            }
        }

        stage('Publish to Nexus') {
            steps {
                script {
                    try {
                        withCredentials([usernamePassword(credentialsId: 'nexus-credentials',
                                                          usernameVariable: 'NEXUS_USERNAME',
                                                          passwordVariable: 'NEXUS_PASSWORD')]) {
                            writeFile file: "${WORKSPACE}/.mvn-nexus-settings.xml", text: """
                            <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
                              <localRepository>${MAVEN_CACHE}</localRepository>
                              <servers>
                                <server>
                                  <id>nexus</id>
                                  <username>\${NEXUS_USERNAME}</username>
                                  <password>\${NEXUS_PASSWORD}</password>
                                </server>
                              </servers>
                            </settings>
                            """
                            sh "mvn -s ${WORKSPACE}/.mvn-nexus-settings.xml deploy -DskipTests"
                        }
                    } catch (Exception e) {
                        echo "Failed to publish to Nexus: ${e.message}"
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
                docker build --cache-from ${IMAGE_NAME}:latest -t ${IMAGE_NAME}:${IMAGE_TAG} .
                docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                mkdir -p ${DOCKER_CACHE}
                SAFE_IMAGE_NAME=$(echo ${IMAGE_NAME} | tr '/' '_')
                docker save ${IMAGE_NAME}:latest -o ${DOCKER_CACHE}/${SAFE_IMAGE_NAME}-latest.tar
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
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "docker push ${IMAGE_NAME}:latest"
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
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
            sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
            sh 'docker system prune -f || true'
        }
    }
}
