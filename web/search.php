<?php

/**
 * 
 * Command Example :
 
Port=1025
while netstat -atwn | grep "^.*:${Port}.*:\*\s*LISTEN\s*$"
do
Port=$(( ${Port} + 1 ))
done
java -cp jade.jar:postgresql-jdbc.jar:jsoup.jar:. jade.Boot -host 127.0.0.1 -local-port ${Port} 'server:ServerAgent(joomla)'

 * 
 */

require_once ('inc/function.php');

//echo stripslashes($_GET['keyword']);

/**/
$port = 1025;
while(shell_exec('lsof -iTCP:'.$port.' 2<&1') != null){
	$port++;
}

$settings = get_settings();

$output = shell_exec("timeout ".$settings['timeout_searching']." java -cp jade.jar:postgresql-jdbc.jar:jsoup.jar:. jade.Boot -host 127.0.0.1 -local-port ".$port." 'server:ServerAgent(".stripslashes($_GET['keyword']).")' 2<&1");
$output = filter('<!-- start -->', '<!-- end -->', $output);
/**/

parse_output($output);

?>
