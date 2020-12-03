pipeline {
    agent none
    stages {
        stage('git clone') {
            agent any
            steps {
                dir('repo'){
                    git credentialsId: 'git_user', url: 'http://git:80/jenkins/wareneingang.git'
                    sh 'ls'
                }
            }
        }
        stage('Build Backend') {
            agent {
                docker {
                    image 'maven:3-openjdk-11'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                sh '''
                    rm -rf app_backend/
                    ls
                    mkdir app_backend/
                    ls
                    cp -r repo/* app_backend/
                '''
                dir('app_backend'){
                    sh '''
                        ls
                        mvn -B -DskipTests clean package
                    '''
                }
            }
        }
        stage('Build Frontend') {
            agent {
                docker { image 'node:14' }
            }
            steps {
                sh '''
                    rm -rf app/
                    ls
                    mkdir app/
                    ls
                    cp repo/frontend/package.json app/package.json
                '''
                dir('app'){
                    sh '''
                        ls
                        npm install
                    '''
                }
                sh '''
                    cp -r repo/frontend/* app/
                '''
                dir('app'){
                    sh '''
                        ls
                        npm run build
                    '''
                }
            }
        }
        stage('Backend Unit Test') {
            agent {
                docker {
                    image 'maven:3-openjdk-11'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                sh '''
                    rm -rf app_backend/
                    ls
                    mkdir app_backend/
                    ls
                    cp -r repo/* app_backend/
                    '''
                dir('app_backend'){
                    sh '''
                        ls
                        mvn test
                    '''
                }
            }
            post {
                always {
                    dir('app_backend'){
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
        }
        stage('Backend Integration Test') {
            agent {
                docker {
                    image 'maven:3-openjdk-11'
                    args '-v /root/.m2:/root/.m2'
                }
            }
            steps {
                dir('app_backend'){
                    sh '''
                        ls
                        mvn verify
                    '''
                }
            }
        }
    }
}