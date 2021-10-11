//This should be run on windows agent
//This script will stop some services on prod server

pipeline {
  agent any
  environment {
    report = 'webServerCutover.txt'
    filePath = 'migrationAutomation/webServerCutover/scripts/powershell'
    reportfilenamevalue = 'webServerCutover.txt'

  }
  parameters {
    string(name: 'hostname', defaultValue: '10.0.0.185',  description: 'Enter hostname or ip.')
    string(name: 'username', defaultValue: 'Administrator',  description: 'Enter username.')
    string(name: 'password', defaultValue: 'fPAgCohVU!',  description: 'Enter passwordword.')
    string(name: 'serviceName', defaultValue: 'was,w3svc',  description: 'Enter service name.')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '30'))
  }
  stages {
    stage('Start Service') {
      // Starting Services on Windows Server
      steps {
        powershell script:"""
           migrationAutomation/webServerCutover/scripts/powershell/webServerCutover.ps1 $hostname $username $password "$serviceName" $report
        """
      }
    }
  }

  post { 
    always { 
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: WORKSPACE, reportFiles: report, reportName: reportfilenamevalue, reportTitles: ''])
    }
  }
}