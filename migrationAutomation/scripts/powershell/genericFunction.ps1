# Generic script to use across ONB migration scripts. 
#function to check status 

$var1=$args[0]
$var2=$args[1]

function statusCheck {
    if ( $? -eq "True" ) {

        echo "$var1 $var2 was success."
    }
    else {
      echo "$args[2] failed"
      exit 1
    }
  }

statusCheck