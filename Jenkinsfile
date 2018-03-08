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
            configFileProvider([configFile(fileId: 'b2da8a78-1b48-48ed-9757-b601da22db66', variable: 'MAVEN_SETTINGS')]) {
                withMaven(maven: 'M3', mavenSettingsConfig: 'b2da8a78-1b48-48ed-9757-b601da22db66') {
                    // Run the maven build

                    if (isUnix()) {
                        sh "'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS  -Dmaven.test.failure.ignore clean package deploy"
                    } else {
                        bat(/"${mvnHome}\bin\mvn" -s $HOME\.m2\settings.xml  -Dmaven.test.failure.ignore clean package deploy/)
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
