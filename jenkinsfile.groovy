#!/usr/bin/env groovy

pipeline {

   agent any

    parameters {
       string(name: 'Host_IP', defaultValue: '13.90.247.105',  description: 'Desired Host IP.')
    }

    options {
      buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '15'))
    }

    stages {
        stage("Create SH Script") {
            steps {
                dir ("${WORKSPACE}") {
                    script {
                        sh """
                        chmod 775 script.sh
                        sh script.sh ${Host_IP}
                        sed -i 's/Host_IP/${Host_IP}/g' script.sh
                        cat script.sh
                        """            
                        }
                    }
                }
            }
        stage("Execute SH Script") {
            steps {
                script {
            def remote = [:]
            remote.name = "node"
            remote.host = "${params.Host_IP}"
            remote.port = 22
            remote.allowAnyHosts = true
            withCredentials([usernamePassword(credentialsId: 'User_Auth', passwordVariable: 'password', usernameVariable: 'userName')]) {
            remote.user = userName
            remote.password = password
                sshPut remote: remote, from: 'script.sh', into: '.'
                sshCommand remote: remote, command: "cat script.sh"
                sshCommand remote: remote, command: "bash script.sh > report.html"
                sshCommand remote: remote, command: "cat report.html"
                sshGet remote: remote, from: 'report.html', into: 'report.html', override: true
                sshRemove remote: remote, path: 'script.sh'
                    }
                }
            }
        }
        post { 
         always { 
            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '/var/lib/jenkins/workspace/SSH_Script/', reportFiles: 'report.html', reportName: 'Report', reportTitles: ''])
            }
        }
    }   
}
