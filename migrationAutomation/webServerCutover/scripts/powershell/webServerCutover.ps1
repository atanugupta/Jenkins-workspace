#This script will be used in Jenkins pipeline for stopping services.

#Variables
$hostname = $args[0]
$username = $args[1]
$password = $args[2]
$cred = New-Object System.Management.Automation.PSCredential ($username, $password)
$serviceName = $args[3]
$report = $args[4]
#Invoke-Command -Session $session -ScriptBlock {Get-Service was,w3svc}
Start-Transcript -Path "$report"

Write-Host '>> START SERVICES <<' -BackgroundColor White -ForegroundColor DarkGreen 
Write-Host ''
echo $hostname
echo $username
echo $password
echo $cred
echo $serviceName
echo $report

Write-Host ''
Stop-Transcript

