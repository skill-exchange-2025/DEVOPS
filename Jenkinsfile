pipeline {
    agent any

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

        stage('Deploy') {
            steps {
                script {
                    // Ici tu peux ajouter la commande pour déployer ton application
                    echo 'Déploiement effectué'
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
