server=$1
serverUser=$2
serviceName=$3
timestamp=$(date "+%Y.%m.%d-%H:%M:%S")
logfile="/tmp/linuxStartServicesReport-$timestamp.txt"

#######################################
# Function to start multiple services on multiple servers.
# Gobals: server, serverUser, timestamp
# ReportFile: $logfile 
# Arguments: $server, $serviceName, $serverUser
# Outputs: Report File
# Writes location to ReportFilePath: $logfile
#######################################
function start_services(){
for serverName in $(echo $server | tr "," "\n")
do
  for srv in $(echo $serviceName | tr "," "\n")
  do
    status="$(ssh -o StrictHostKeyChecking=no $serverUser@$serverName "sudo service $srv start")"
    if [ $? != 0 ]
    then
    echo "Unable to connect to the server $serverName" |tee -a $logfile
    exit 1
    fi
    if [ ! -z "$status" ]
    then
      checks=$(echo -e "$status"|grep Active|awk '{print $3}')
      if [ $checks == "(dead)" ]
      then
        echo "Service $srv is in stopped state on $serverName." |tee -a $logfile
        echo "Starting the service $srv on $serverName." |tee -a $logfile
        sudo service $srv start
        if [ $? != 0 ]
        then
          echo "Failed to start service $srv on $serverName" |tee -a $logfile
          exit 1
        fi
      elif [ $checks == "(running)" ] ; then
        echo "Service $srv is in running state on $serverName." |tee -a $logfile
      fi
    else
      echo "Unable to connect to remote server $serverName.Connection Variable empty" |tee -a $logfile
      exit 1
    fi
  done
done
}

# Calling Function to start service
start_services $server $serverUser $serviceName
