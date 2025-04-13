pipeline {
    agent any

    tools {
        maven 'Maven 3.9.6'
        jdk 'JAVA_HOME'
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        SONAR_HOST_URL = 'http://192.168.50.4:9000'  // Your actual SonarQube server IP
    }

    stages {
        stage('Debug Environment') {
            steps {
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

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'TOKEN')]) {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=tpfoyer \
                        -Dsonar.host.url=$SONAR_HOST_URL \
                        -Dsonar.login=$TOKEN
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Build completed'
        }
    }
}
