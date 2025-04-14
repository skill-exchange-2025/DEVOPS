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
        // Define cache directories
        MAVEN_CACHE = "${WORKSPACE}/.m2"
        DOCKER_CACHE = "${WORKSPACE}/.docker-cache"
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
                // Create the Maven cache directory if it doesn't exist
                sh 'mkdir -p ${MAVEN_CACHE}'

                // Create a Maven settings file for local repository caching
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
                // Just compile the code
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                // Run unit tests only
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                // Run integration tests if they exist
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml verify -DskipUnitTests'
            }
            post {
                always {
                    // Publish integration test results if they exist
                    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Generate Code Coverage') {
            steps {
                // Generate code coverage report using Maven plugin
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml org.jacoco:jacoco-maven-plugin:report'
            }
        }

        stage('Package') {
            steps {
                // Build the package after tests
                sh 'mvn -s ${WORKSPACE}/.mvn-settings.xml package -DskipTests'
            }
        }

        stage('Setup Docker Cache') {
            steps {
                // Create the Docker cache directory if it doesn't exist
                sh 'mkdir -p ${DOCKER_CACHE}'

                // Restore Docker cache if it exists
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
                // Use Docker BuildKit for improved caching
                sh '''
                export DOCKER_BUILDKIT=1
                docker build --cache-from ${IMAGE_NAME}:latest -t ${IMAGE_NAME}:${IMAGE_TAG} .
                docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest
                '''

                // Save the image to cache for future builds - using safe filename
                sh '''
                mkdir -p ${DOCKER_CACHE}
                # Replace / with _ for safe filename
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
            sh 'docker logout || true'

            // Archive test reports as artifacts
            archiveArtifacts artifacts: '**/target/surefire-reports/*', allowEmptyArchive: true
            archiveArtifacts artifacts: '**/target/failsafe-reports/*', allowEmptyArchive: true
            archiveArtifacts artifacts: '**/target/site/jacoco/**/*', allowEmptyArchive: true

            // Archive the Maven cache for future builds
            sh '''
            echo "Archiving Maven cache..."
            tar -czf maven-cache.tar.gz -C ${WORKSPACE} .m2 || true
            '''
            archiveArtifacts artifacts: 'maven-cache.tar.gz', allowEmptyArchive: true
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

            // Keep latest tag in cache but remove it from Docker daemon
            sh "docker rmi ${IMAGE_NAME}:latest || true"

            // Clean up unused Docker resources while preserving cache
            sh 'docker system prune -f || true'
        }
    }
}