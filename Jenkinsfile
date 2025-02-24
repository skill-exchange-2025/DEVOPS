pipeline {
    agent any

  tools {
        maven "M2_HOME"
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
                    // Build le projet avec Maven
                    sh 'mvn clean install'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    // Exécute les tests unitaires
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