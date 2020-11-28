pipeline {
    agent {
        docker {
            image 'maven:3-openjdk-11'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('git clone') {
             steps {
                git credentialsId: 'git_user', url: 'http://git:80/jenkins/wareneingang.git'
            }
        }
        stage('Build') {
             steps {
                 sh 'mvn -B -DskipTests clean package'
            }
        }
    }
}