node {
    def mvnHome
    stage('Preparation') { // for display purposes
        // Get some code from a GitHub repository
        // git
        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool must be configured
        // **       in the global configuration.
        mvnHome = tool 'M3'
    }
    stage('Build') {
        configFileProvider([configFile(fileId: 'c926d31d-47bf-46ac-966e-49bd9d62b71e', variable: 'MAVEN_SETTINGS')]) {
            withMaven(maven: 'M3') {
                // Run the maven build

                if (isUnix()) {
                    sh "mvn -s $MAVEN_SETTINGS  -Dmaven.test.failure.ignore clean help:effective-pom help:effective-settings help:system package deploy"
                } else {
                    bat "mvn -s %MAVEN_SETTINGS%  -Dmaven.test.failure.ignore clean help:effective-pom help:effective-settings help:system package deploy"
                }
            }
        }
    }
    stage('Results') {
        archive 'target/*.jar'
    }
}
