#This script will be used in Jenkins pipeline for stopping services.

#VARIABLES
$servername = $args[0]
$username = $args[1]
$password = $args[2]
$database = $args[3]
$report = $args[4]
$sqlCmdPath = $args[5]
$urlOfEp = $args[6]
$oldClusterName = $args[7]
$newClusterName = $args[8]
$workspace = $args[9]

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

Start-Transcript -Path $workspace/$report

Write-Host '>> Print Variables <<' 
Write-Host '******************'
$servername 
$username
$database
$report
$sqlCmdPath
$urlOfEp
$oldClusterName
$newClusterName
$workspace
Write-Host '******************'

cd $sqlCmdPath

Write-Host '>> DB Query Result <<'  

Write-Host "Query 1:"
./SQLCMD.EXE -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "select @@VERSION"
statusCheck 

Write-Host "Query 2:"
SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "UPDATE [XPRESSHR_GLOBAL].[dbo].[GlobalConfig] SET Value='$urlOfEp' where [Key]='EmployeePortal.ExternalUrlBase'"
statusCheck 

Write-Host "Query 3:"
SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "UPDATE [ONBPREM].[dbo].[Account]  SET [ConnectionString] = REPLACE([ConnectionString],'$oldClusterName','$newClusterName' ) WHERE [ConnectionString] like '%old_cluster_name%'"
statusCheck

Write-Host "Query 4:"
SQLCMD.exe -S "$servername" -d "$database" -U "$username" -P "$password" -W -Q "UPDATE [ONBPREM].[dbo].[MasterConfig] SET Value=(SELECT Value FROM [ONBPREM_old].[dbo].[MasterConfig] WHERE [Key]='JobManagementSvcAddressKey') Where [Key]='JobManagementSvcAddressKey'"
statusCheck

Stop-Transcript