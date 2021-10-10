#This script will be used in Jenkins pipeline for stopping services.

#Variables
$serviceNames = $serviceName
$serverName = $hostname
$pass = ConvertTo-SecureString $password -AsPlainText -Force
$user = $username
$cred = New-Object System.Management.Automation.PSCredential ($user, $pass)
$report = $report
#Invoke-Command -Session $session -ScriptBlock {Get-Service was,w3svc}
Start-Transcript -Path "C:/report/$report"

Write-Host '>> START SERVICES <<' -BackgroundColor White -ForegroundColor DarkGreen 
Write-Host ''

foreach ($serviceName in $serviceNames) {
	try{
		$serverName=$using:serverName
        $session = New-PSSession -ComputerName ${​​​​serverName}​​​​ -Credential $cred
        Invoke-Command -Session $session -ScriptBlock {​​​​ $serviceName=$using:serviceName 
       	$service = Get-Service -Name $serviceName
		Write-Host $service.DisplayName -BackgroundColor DarkGray -ForegroundColor White

		Write-Host 'Status: ' -NoNewline
    	Write-Host ''
			if ($service.Status -eq 'Running') {
			Write-Host $ServiceName already in $service.Status state -ForegroundColor Green
			}
			else {
        Write-Host ''
        $service_check=Set-Service -name $ServiceName -Status Running -StartupType Automatic
					if ($service_check -ne $null)
            {
	          Write-Host So, starting service ${serviceName}.
            }
          else
            {
            Write-Host Service ${ServiceName} Not Found 
          }
   			}
    Write-Host ''
	  $service.Refresh() 
	}
	catch{
    Write-host "Service could not be found" -ForegroundColor Red
  }
}

Stop-Transcript