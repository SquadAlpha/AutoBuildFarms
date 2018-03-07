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
                configFileProvider([configFile('b2da8a78-1b48-48ed-9757-b601da22db66')]) {
                    script {
                        if (isUnix()) {
                            sh 'mvn -Dmaven.test.failure.ignore=true clean package install'
                        } else {
                            bat 'mvn -Dmaven.test.failure.ignore=true clean package install'
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
