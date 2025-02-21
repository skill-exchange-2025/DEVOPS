pipeline {
    agent any

    tools {
        maven 'M3' // Nom de l'installation Maven configurée dans Jenkins
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' // Publication des rapports de test
                }
            }
        }


    }
}