pipeline {
    agent any

    environment {
        NEXUS_VERSION = "nexus3"
        NEXUS_PROTOCOL = "http"
        NEXUS_URL = "localhost:8081"
        NEXUS_REPOSITORY = "maven-releases"
        ARTIFACT_VERSION = "5.0.0"
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
/*
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
        }*/
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
