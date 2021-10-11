#This script will be used in Jenkins pipeline for stopping services.

#Variables
$hostname = $1
$user = $2
$pass = $3
$cred = New-Object System.Management.Automation.PSCredential ($user, $pass)
$serviceNames = $4
$report = $5
#Invoke-Command -Session $session -ScriptBlock {Get-Service was,w3svc}
Start-Transcript -Path "$report"

Write-Host '>> START SERVICES <<' -BackgroundColor White -ForegroundColor DarkGreen 
Write-Host ''

echo $cred

Write-Host ''
Stop-Transcript

