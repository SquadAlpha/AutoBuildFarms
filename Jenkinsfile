node {
    def mvnHome
    stage('Preparation') { // for display purposes
        // Get some code from a GitHub repository
        git 'https://github.com/SquadAlpha/AutoBuildFarms.git'
        // Get the Maven tool.
        // ** NOTE: This 'M3' Maven tool must be configured
        // **       in the global configuration.
        mvnHome = tool 'M3'
    }
    stage('Build') {
        configFileProvider([configFile(fileId: 'b2da8a78-1b48-48ed-9757-b601da22db66', variable: 'MAVEN_SETTINGS')]) {
            withMaven(maven: 'M3', mavenSettingsConfig: 'b2da8a78-1b48-48ed-9757-b601da22db66') {
                // Run the maven build

                if (isUnix()) {
                    sh "'${mvnHome}/bin/mvn' -s $MAVEN_SETTINGS  -Dmaven.test.failure.ignore clean package deploy"
                } else {
                    bat(/"${mvnHome}\bin\mvn" -s %MAVEN_SETTINGS%  -Dmaven.test.failure.ignore clean package deploy/)
                }
            }
        }
    }
    stage('Results') {
        junit '**/target/surefire-reports/TEST-*.xml'
        archive 'target/*.jar'
    }
}
