//This should be run on windows agent
//This script will stop some services on prod server

pipeline {
  agent any
  environment {
    report = 'webServerCutover.txt'
    filePath = 'migrationAutomation/webServerCutover/scripts/powershell'

  }
  parameters {
    string(name: 'hostname', defaultValue: '10.0.0.185',  description: 'Enter hostname or ip.')
    string(name: 'username', defaultValue: 'Administrator',  description: 'Enter username.')
    string(name: 'password', defaultValue: 'fPAgCohVU!',  description: 'Enter passwordword.')
    string(name: 'serviceName', defaultValue: 'was,w3svc',  description: 'Enter service name.')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '15'))
  }
  stages {
    stage('Start Service') {
      // Starting Services on Windows Server
      steps {
        powershell script:"""
          ${env:filePath}/webServerCutover.ps1 "${env:hostname}" "${env:username}" "${env:password}" "${env:serviceName}" "${env:report}"
        """
      }
    }
  }

  post { 
    always { 
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: WORKSPACE, reportFiles: report, reportName: '*.txt', reportTitles: ''])
    }
  }
}