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
                configFileProvider([configFile('e40a45f1-d202-4d5b-9398-bc63231b6812')]) {
                    script {
                        if (isUnix()) {
                            sh 'mvn -Dmaven.test.failure.ignore=true -Pupstream clean package'
                        } else {
                            bat 'mvn -Dmaven.test.failure.ignore=true -Pupstream clean package'
                        }
                    }
                }
            }
        }
        stage('Archive') {
            agent any
            steps {
                archiveArtifacts 'target/*.jar'
            }
        }
    }
    tools {
        maven 'Maven 3.5.2'
        jdk 'Local JDK 1.8.0_162'
    }
}
