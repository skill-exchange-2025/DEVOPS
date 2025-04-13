pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'jdk-21'
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Debug Environment') {
            steps {
                // Print environment variables to diagnose issues
                sh 'echo "JAVA_HOME: $JAVA_HOME"'
                sh 'echo "PATH: $PATH"'
                sh 'java -version || true'
                sh 'mvn -version || true'
                sh 'ls -la $JAVA_HOME/bin || true'
            }
        }

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
    }

    post {
        always {
            echo 'Build completed'
        }
    }
}