pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'jdk-21'
    }

    environment {
        JAVA_HOME = tool name: 'jdk-21', type: 'hudson.model.JDK'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'jihed', url: 'https://github.com/skill-exchange-2025/DEVOPS.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Tests Unitaires') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonar-scanner') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t springboot-app .'
            }
        }

        stage('Docker Compose Up') {
            steps {
                sh 'docker-compose up -d'
            }
        }
    }
}
