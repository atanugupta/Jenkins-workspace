//This should be run on windows agent
//This script will executesome database query on prod db server

pipeline {
  agent any
  environment {
    report = "postSqldbCutoverSharepointServer_${BUILD_NUMBER}.txt"
    sqlCmdPath = 'C:/Program Files/Microsoft SQL Server/Client SDK/ODBC/110/Tools/Binn'
    pathDir = 'migrationAutomation/postSqldbCutoverSharepointServer/scripts/powershell'
  }
  parameters {
    string(name: 'servername', defaultValue: '10.0.0.185',  description: 'Enter SQL servername.')
    string(name: 'username', defaultValue: 'sa',  description: 'Enter database username.')
    password(name: 'password', defaultValue: 'fPAgCohVU!',  description: 'Enter database password.')
    string(name: 'database', defaultValue: 'testdb',  description: 'Enter database name.')    
    string(name: 'urlOfEp', defaultValue: '',  description: 'Enter live URL of EP.') 
    string(name: 'oldClusterName', defaultValue: '',  description: 'Enter old cluster name.') 
    string(name: 'newClusterName', defaultValue: '',  description: 'Enter new cluster name.') 
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '15'))
  }
  stages {
    stage('Verify Input Parameters') {
      steps {
        script {
          params.each { param ->
            if (param.value instanceof String) {
              if (param.value.trim().isEmpty() || param.value.contains(' ')) {
              println param.key + " value is invalid. It is either empty or has space. Please provide correct value and rebuild the job. value=" + param.value
              sh 'exit 1'
              }
            }
          }
        }
      }
    }

    stage('Load Configuration Properties') {
      steps {
        script {
          props = readProperties file : "migrationAutomation/postSqldbCutoverSharepointServer/pipeline/config.properties"
          reportFileValue = props['reportFileValue']
          allowMissingValue = props['allowMissingValue']
          alwaysLinkToLastBuildValue = props['alwaysLinkToLastBuildValue']
          keepAllValue = props['keepAllValue']
          reportNameValue = props['reportNameValue']

          println "setting properties values allowMissingValue = " + allowMissingValue
          println "setting properties values alwaysLinkToLastBuildValue = " + alwaysLinkToLastBuildValue
          println "setting properties values keepAllValue = " + keepAllValue
          println "setting properties values reportNameValue = " + reportNameValue
          println "setting properties values reportFileValue = " + reportFileValue
        }
      }
    }

    stage('SQL DB Query Execution') {
      // Starting Services on Windows Server
      steps {
        powershell script:"""
        cd $pathDir
        ./postSqldbCutoverSharepointServer.ps1 $servername $username $password $database $report "$sqlCmdPath" $urlOfEp $oldClusterName $newClusterName $workspace
        """
      }
    }
  }
  post {
    always {
      publishHTML([allowMissing: allowMissingValue, alwaysLinkToLastBuild: alwaysLinkToLastBuildValue, keepAll: keepAllValue, reportDir: WORKSPACE, reportFiles: reportFileValue, reportName: reportNameValue, reportTitles: ''])
    }
  }
}