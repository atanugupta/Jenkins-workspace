#This script will be used in Jenkins pipeline for stopping services.

#VARIABLES
$servername = $1
$username = $2
$password = $3
$database = $4
#$urlOfEp = $5
#$oldClusterName = $6
#$newClusterName = $7
$report = $8


#function to check status 
function statusCheck {
  if ( $? -eq "True" ) {
    echo "Query executed succesfully."
  }
  else {
    echo "Query execution failed"
    exit 1
  }
}

cd "C:/Program Files/Microsoft SQL Server/Client SDK/ODBC/110/Tools/Binn"

Start-Transcript -Path $report

Write-Host "Query 1:"
SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "select @@VERSION"
statusCheck 

#Write-Host "Query 1:"
#SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "UPDATE [XPRESSHR_GLOBAL].[dbo].[GlobalConfig] SET Value='$urlOfEp' where [Key]='EmployeePortal.ExternalUrlBase'"
#statusCheck 
#
#Write-Host "Query 2:"
#SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "UPDATE [ONBPREM].[dbo].[Account]  SET [ConnectionString] = REPLACE([ConnectionString],'$oldClusterName','$newClusterName' ) WHERE [ConnectionString] like '%old_cluster_name%'"
#statusCheck
#
#Write-Host "Query 3:"
#SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "UPDATE [ONBPREM].[dbo].[MasterConfig] SET Value=(SELECT Value FROM [ONBPREM_old].[dbo].[MasterConfig] WHERE [Key]='JobManagementSvcAddressKey') Where [Key]='JobManagementSvcAddressKey'"
#statusCheck

Stop-Transcript