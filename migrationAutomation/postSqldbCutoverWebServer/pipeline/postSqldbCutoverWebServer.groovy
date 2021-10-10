//This should be run on windows agent
//This script will start some services on prod server

pipeline {
  agent any
  environment {
      serviceName = ('iisadmin','XpressHR')
      report = 'postSqldbCutoverWebServer.txt'
  }
  parameters {
    string(name: 'hostname', defaultValue: '',  description: 'Enter hostname or ip.')
    string(name: 'username', defaultValue: '',  description: 'Enter username.')
    string(name: 'password', defaultValue: '',  description: 'Enter passwordword.')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '15'))
  }
  stages {
    stage('Start Service') {
      // Stopping Services on Windows Server
      steps {
        powershell script:'''
          try
          {
            dir("migrationAutomation/postSqldbCutoverWebServer/scripts/powershell/")
            {
             ./postSqldbCutoverWebServer.ps1 $hostname $username $password ${env:serviceName} ${env:report}
            }
          }
          catch (Exception e)
          {
            echo 'Exception occurred: ' + e.toString()
          }
        '''
      }
    }
  }
  post { 
    always { 
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: WORKSPACE, reportFiles: $report, reportName: 'postSqldbCutoverWebServerReport', reportTitles: ''])
    }
  }
}