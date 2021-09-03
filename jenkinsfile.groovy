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
        stage ('Clean Workspace') {
           steps {
               cleanWs()
           }
       }
        stage("Create SH Script") {
            steps {
                dir ("${WORKSPACE}") {
                    script {
                        sh """
                        echo ${Host_IP}
                        pwd
                        ls -lrth
                        sudo ./script.sh ${Host_IP}
                        chmod 775 script.sh
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
                sshGet remote: remote, from: 'script.sh', into: 'test_new.sh', override: true
                sshRemove remote: remote, path: 'script.sh'
                    }
                }
            }
        }
    }   
}
