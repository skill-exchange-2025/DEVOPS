pipeline {
    agent any

    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "localhost:8081"
        NEXUS_REPOSITORY = "maven-releases"
        ARTIFACT_VERSION = "5.0.0"

        // Credentials binding
        DOCKER_CREDENTIALS = credentials('dockerhub-credentials')
        NEXUS_CREDENTIAL_ID = 'nexus-admin'
    }


    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def filesByGlob = findFiles(glob: "target/*.jar")
                    def artifactPath = filesByGlob[0].path
                    def artifactExists = fileExists artifactPath

                    if(artifactExists) {
                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: ARTIFACT_VERSION,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                [artifactId: '4TWIN7-devops',
                                 classifier: '',
                                 file: artifactPath,
                                 type: 'jar']
                            ]
                        )
                    }
                }
            }
        }
/*
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_USERNAME}/YourName_G1_devops:${BUILD_NUMBER} .'
                sh 'docker tag ${DOCKER_USERNAME}/YourName_G1_devops:${BUILD_NUMBER} ${DOCKER_USERNAME}/YourName_G1_devops:latest'
            }
        }

        stage('Push Docker Image') {
            steps {
                sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                sh 'docker push ${DOCKER_USERNAME}/YourName_G1_devops:${BUILD_NUMBER}'
                sh 'docker push ${DOCKER_USERNAME}/YourName_G1_devops:latest'
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }

        stage('Test API') {
            steps {
                script {
                    sleep(time: 30, unit: "SECONDS")
                    sh 'curl -X GET http://localhost:8089/tpfoyer/etudiant/retrieve-all-etudiants'
                }
            }
        }/*
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}