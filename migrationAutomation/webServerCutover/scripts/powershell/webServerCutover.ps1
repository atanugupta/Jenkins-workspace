#This script will be used in Jenkins pipeline for stopping services.

#Variables
$hostname = "$args[0]"
$username = "$args[1]"
$password = ConvertTo-SecureString "$args[2]" -AsPlainText -Force
$cred = New-Object System.Management.Automation.PSCredential ($username, $password)
$serviceName = "$args[3]"
$report = "$args[4]"

## Create Session
$session = New-PSSession -ComputerName 10.0.0.185 -Credential $cred

#Invoke-Command -Session $session -ScriptBlock {Get-Service was,w3svc}
Start-Transcript -Path $report

Write-Host '>>>> Print Variables <<<<' -BackgroundColor White -ForegroundColor DarkGreen 
Write-Host ''
echo $hostname
echo $username
echo $password
echo $cred
echo $serviceName
echo $report

Write-Host ''

Write-Host '>>>> START SERVICES <<<<' -BackgroundColor White -ForegroundColor DarkGreen 
Write-Host ''
Invoke-Command -Session  $session -ScriptBlock {hostname}
Write-Host ''
Stop-Transcript

