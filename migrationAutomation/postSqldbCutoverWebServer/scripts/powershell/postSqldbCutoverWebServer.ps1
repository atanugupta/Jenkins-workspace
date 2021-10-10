#This script will be used in Jenkins pipeline for stopping services.

#VARIABLES
$serviceNames = $serviceName
$serverName = $hostname
$pass = ConvertTo-SecureString $password -AsPlainText -Force
$user = $username
$cred = New-Object System.Management.Automation.PSCredential ($user, $pass)
$report = $report


Start-Transcript -Path "C:/report/$report"

Write-Host '>> STOP SERVICES <<'
Write-Host ''

foreach ($serviceName in $serviceNames) {
	try{
    $serverName=$using:serverName
    $session = New-PSSession -ComputerName ${​​​​serverName}​​​​ -Credential $cred
    Invoke-Command -Session $session -ScriptBlock {​​​​ $serviceName=$using:serviceName 
    $service = Get-Service -Name $serviceName
		$service = Get-Service -Name $serviceName
		Write-Host $service.DisplayName
      
		Write-Host 'Status: ' -NoNewline
      
    Write-Host ''
			if ($service.Status -eq 'Stopped') {
			Write-Host $ServiceName already in $service.Status state
			}
      else {
        Write-Host ''
        $service_check=Set-Service -name $ServiceName -Status Stop -StartupType Disabled
			  if ($service_check -ne $null)
        {
			  Write-Host So, stopping service ${serviceName}.
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