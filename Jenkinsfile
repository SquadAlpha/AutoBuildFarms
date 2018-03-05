pipeline {
    agent any
    stages {
        stage('Initialize') {
            agent any
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
        stage('Build') {
            agent any
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean package'
            }
        }
        stage('Archive') {
            agent any
            steps {
                archiveArtifacts 'target/*.jar,target/bin/bo*'
            }
        }
    }
    tools {
        maven 'Maven 3.5.2'
        jdk 'Local JDK 1.8.0_162'
    }
}