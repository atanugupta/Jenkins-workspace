//This should be run on windows agent
//This script will executesome database query on prod db server

def pathDir
def allowMissingValue
def alwaysLinkToLastBuildValue
def keepAllValue
def reportNameValue
def reportFileValue

pipeline {
  agent any
  environment {
    report = 'postSqldbCutoverSharepointServer_${BUILD_NUMBER}.txt'
  }
  parameters {
    string(name: 'servername', defaultValue: '',  description: 'Enter SQL servername.')
    string(name: 'username', defaultValue: '',  description: 'Enter database username.')
    string(name: 'password', defaultValue: '',  description: 'Enter database password.')
    string(name: 'database', defaultValue: '',  description: 'Enter database name.')    
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
            if (param.value.trim().isEmpty() || param.value.contains(' ')) {
            println param.key + " value is invalid. It is either empty or has space. Please provide correct value and rebuild the job. value=" + param.value
            sh 'exit 1'
            }
          }
        }
      }
    }

    stage('Load Configuration Properties') {
      steps {
        script {
          props = readProperties file : "$WORKSPACE/migrationAutomation/disableElasticSearch/pipeline/config.properties"
          pathDir = props['pathDir']
          allowMissingValue = props['allowMissingValue']
          alwaysLinkToLastBuildValue = props['alwaysLinkToLastBuildValue']
          keepAllValue = props['keepAllValue']
          reportNameValue = props['reportNameValue']
          reportFileValue = props['reportFileValue']

          println "setting properties values pathDir = " + pathDir 
          println "setting properties values allowMissingValue = " + allowMissingValue
          println "setting properties values alwaysLinkToLastBuildValue = " + alwaysLinkToLastBuildValue
          println "setting properties values keepAllValue = " + keepAllValue
          println "setting properties values reportNameValue = " + reportNameValue
          println "setting properties values reportFileValue = " + reportFileValue
        }
      }
    }

    stage('Start Service') {
      // Starting Services on Windows Server
      steps {
        powershell script:"""
          try
          {
            {
             $pathDir/postSqldbCutoverSharepointServer.ps1 $servername $username $password $database $urlOfEp $oldClusterName $newClusterName $report
            }
          }
          catch (Exception e)
          {
            echo 'Exception occurred: ' + e.toString()
          }
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