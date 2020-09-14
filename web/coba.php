<?php


require_once ('inc/function.php');

/** /
	echo '<strong>java -version</strong><hr>';
	echo $output = shell_exec("java -version 2<&1");
	echo '<br/><br/>';
	
	echo '<strong>HelloWorld.java</strong><hr>';
	echo $output = shell_exec("java HelloWorld 2<&1");
	echo '<br/><br/>';


	echo '<strong>ServerAgent.java</strong><hr>';
	$output = shell_exec("java -cp jade.jar:postgresql-jdbc.jar:. jade.Boot -host 127.0.0.1 'server:ServerAgent(1, 5, ".$_GET['keyword'].")' 2<&1");
	echo filter('<!--start-->', '<!--end-->', $output);
	echo '<br/><br/>';
/**/
?>

<?php

/** @var $string1  /
$string1 = 'Ahmad Zafrullah Mardiansyah tapon timur desa bilebante pringgarata lombok tengah';
$string2 = 'Ahmad tapon';

similar_text($string1, $string2, $p);
echo "Percent: $p%";
/**/

/** //
echo '<pre>';
print_r(get_site_submitted());
echo '</pre>';

accept_site_submitted(5);
/**/

/** /
echo '<pre>';

print_r(get_searched('2013-4-1', '2013-4-30', 'facebook'));

print_r(get_searched('2013-4-1', '2013-4-30', 'wordpress'));

echo '</pre>';

/**/

$keyword = 'zafrullah ahmad site:zaf.web.id';

$pos = strpos($keyword, 'site:');
$site =  substr($keyword, $pos+5);
echo $k = substr($keyword, 0, $pos);


?>
