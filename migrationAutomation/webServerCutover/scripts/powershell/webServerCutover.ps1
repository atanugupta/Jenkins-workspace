#This script will be used in Jenkins pipeline for stopping services.

#Variables
$hostname = $1
$username = $2
$password = $3
$cred = New-Object System.Management.Automation.PSCredential ($username, $password)
$serviceName = $4
$report = $5
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

