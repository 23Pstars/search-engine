<?php

$host = "127.0.0.1";
$user = "user";
$pass = "pass";
$db = "db";

$con = pg_connect("host=$host dbname=$db user=$user password=$pass")
    or die ("Could not connect to server\n");

function get_pg_version() {
    $query = "SELECT VERSION()";
    $rs = pg_query($query) or die("Cannot execute query: $query\n");
    $row = pg_fetch_row($rs);

    echo $row[0] . "\n";
}

function search_on_database($keyword) {

    switch(get_operator($keyword)) {
        case 1 :
            //echo 1;
            $keyword = stripslashes($keyword);
            $keyword = substr($keyword, 1, -1);
            $keyword = strtolower($keyword);
            $query = pg_query( "SELECT * FROM websites WHERE LOWER(title) LIKE '%".$keyword."%' OR LOWER(description) LIKE '%".$keyword."%'" );
            add_keyword($keyword);
            break;
        case 2 :
            //echo 2;
            $pos = strpos($keyword, 'site:');
            $site =  substr($keyword, $pos+5);
            $kw = substr($keyword, 0, $pos);
            $query = pg_query( "SELECT *, (ts_rank_cd(keyword, PLAINTO_TSQUERY('".$kw."')) * visited) AS rank FROM websites WHERE keyword @@ PLAINTO_TSQUERY('".$kw."') AND link LIKE '%".$site."%' ORDER BY rank DESC" );
            add_keyword($kw);
            break;
        default :
            $query = pg_query( "SELECT *, (ts_rank_cd(keyword, PLAINTO_TSQUERY('".$keyword."')) * visited) AS rank FROM websites WHERE keyword @@ PLAINTO_TSQUERY('".$keyword."') ORDER BY rank DESC" );
            add_keyword($keyword);
            break;
    }

    $count = pg_num_rows($query);
    if ($count) return pg_fetch_all($query);
    else return $count;
}

/**
 * untuk mendeteksi operator keyword
 *
 * 0 => default (sesuai pengaturan)
 * 1 => "" (mencari kata / kalimat yang persis sama)
 * 2 => site: (mencari didalam situs tertentu)
 *
 * @param $keyword
 */
function get_operator($keyword) {
    $keyword = stripslashes($keyword);
    $keyword = trim($keyword);
    if (substr($keyword, 0, 1) == '"' && substr($keyword, -1) == '"') {
        return 1;
    } else if (strpos($keyword, 'site:') !== false) {
        return 2;
    } else {
        return 0;
    }


}

function get_settings() {
    $result = array();
    $query = pg_query( "SELECT * FROM settings" );
    while ($s = pg_fetch_assoc($query)) {
        $result[$s['option']] = $s['value'];
    }
    return $result;
}

function save_settings($settings) {
    $result = true;
    foreach ($settings as $key => $setting) {
        if (!pg_query( "UPDATE settings SET value = '".$setting."' WHERE option = '".$key."'" )) $result = false;
    }
    return $result;
}

function add_sites($sites) {
    $ip = $_SERVER['REMOTE_ADDR'];
    return pg_query("INSERT INTO site_submitted (link, ip_sender) VALUES ('".$sites."', '".$ip."')");
}

function add_report($link, $reason) {
    $ip = $_SERVER['REMOTE_ADDR'];
    return pg_query("INSERT INTO site_reported (link, reason, ip_sender) VALUES ('".$link."', '".$reason."', '".$ip."')");
}

function accept_site_submitted($id) {
    $link = pg_fetch_assoc(pg_query("SELECT link FROM site_submitted WHERE id = $id"));
    return pg_query("UPDATE site_submitted SET status = 'accepted' WHERE id = ".$id) && pg_query("INSERT INTO host_directory (address) VALUES ('".$link['link']."')");
}

function decline_site_submitted($id) {
    return pg_query("DELETE FROM site_submitted WHERE id = ".$id);
}

function blok_site_reported($id) {
    $link = pg_fetch_assoc(pg_query("SELECT link FROM site_reported WHERE id = $id"));

    return pg_query("UPDATE site_reported SET status = 'accepted' WHERE id = ".$id)
        && pg_query("INSERT INTO site_blocked (link) VALUES ('".$link['link']."')")
        && pg_query("DELETE FROM websites WHERE link = '".$link['link']."'");
    /** /
    echo '<pre>';
    print_r($link);
    echo '</pre>';
    /**/
    return true;
}

function ignore_site_reported($id) {
    return pg_query("DELETE FROM site_reported WHERE id = ".$id);
}

function delete_site_blocked($id) {
    return pg_query("DELETE FROM site_blocked WHERE id = ".$id);
}

function truncate_websites() {
    return pg_query("TRUNCATE websites");
}

function truncate_host_directory() {
    return pg_query("TRUNCATE host_directory");
}

function get_site_submitted() {
    $result = array();
    $query = pg_query( "SELECT * FROM site_submitted WHERE status = 'waiting' ORDER BY id DESC" );
    while ($q = pg_fetch_assoc($query)) {
        $result[] = $q;
    }
    return $result;
}

function get_site_reported() {
    $result = array();
    $query = pg_query( "SELECT * FROM site_reported WHERE status = 'waiting' ORDER BY id DESC" );
    while ($q = pg_fetch_assoc($query)) {
        $result[] = $q;
    }
    return $result;
}

function get_site_blocked() {
    $result = array();
    $query = pg_query( "SELECT * FROM site_blocked WHERE status = 'waiting' ORDER BY id DESC" );
    while ($q = pg_fetch_assoc($query)) {
        $result[] = $q;
    }
    return $result;
}

function login($u, $p) {
    $username = pg_query("SELECT value FROM settings WHERE option = 'username'");
    $password = pg_query("SELECT value FROM settings WHERE option = 'password'");
    $user =  pg_fetch_all($username);
    $pass = pg_fetch_all($password);
    return $u == $user[0]['value'] && $p == $pass[0]['value'];
}

function logout() {
    $password = pg_query("SELECT value FROM settings WHERE option = 'password'");
    $pass = pg_fetch_all($password);
    setcookie("Zaf", md5($pass[0]['value']), time()-(24*3600), '/');
}

function isLoggedIn() {
    $password = pg_query("SELECT value FROM settings WHERE option = 'password'");
    $pass = pg_fetch_all($password);
    return $_COOKIE['Zaf'] == md5($pass[0]['value']);
}

function update_visit_count_by_id($id) {
    return pg_query("UPDATE websites SET visited = visited + 1 WHERE id = '$id'");
}

function update_visit_count_by_url($url) {
    return pg_query("UPDATE websites SET visited = visited + 1 WHERE link = '$url'");
}

function add_keyword($keyword) {
    return pg_query("INSERT INTO searched (keyword) VALUES ('$keyword')");
}

function get_trend_searched($x) {
    $result = array();
    $query = pg_query( "SELECT keyword, COUNT(*) FROM searched GROUP BY keyword ORDER BY count DESC LIMIT $x" );
    while ($q = pg_fetch_assoc($query)) {
        $result[] = $q;
    }
    return $result;
}

function get_searched($date_start, $date_end, $keyword) {
    $result = array();
    $query = pg_query( "SELECT keyword, COUNT(*) FROM searched WHERE keyword = '$keyword' AND date >= '$date_start' and date <= '$date_end' GROUP BY keyword ORDER BY keyword DESC" );
    while ($q = pg_fetch_assoc($query)) {
        $result[] = $q;
    }
    return $result;
}

function filter($start, $end, $content) {
	$start_pos = strpos($content, $start);
	$end_pos = strpos($content, $end);
    $out = $start_pos && $end_pos ? substr($content, $start_pos, ($end_pos-$start_pos)) : $content;
	return $out;
}

function parse_output($output) {
	
	//error_reporting(E_ALL); ini_set('display_errors', 1);
	
    $settings = get_settings();
    
    $timeout = false;
    
    $output = preg_replace('#&(?=[a-z_0-9]+=)#', '&amp;', $output);
    
    if (strpos($output,"Timeout: aborting command ``java'' with signal 9\nKilled") !== false) $timeout = true;
    
	$xml = simplexml_load_string($output);
	
	$websites = $xml->website;
	
    $i = 1;
    $page = 1;
	?>

    <form action="" method="get">
        <p class="notif">
			<?php if ($timeout) : ?>
				Terjadi kesalahan jaringan dalam sistem, mesin pencari tidak dapat terhubung dengan internet.
			<?php else : ?>
				<?php $total = count($websites); ?>
				<?php if ($total == 0) : ?>
				Tidak ada
				<?php else : ?>
				Terdapat <span><?php echo $total; ?></span>
				<?php endif; ?>
                hasil dari kata kunci <input class="keyword-field" name="keyword" type="text" placeholder="Tulis kata kunci..." value='<?php echo stripslashes($_GET['keyword']); ?>' /> <input class="search-button" type="submit" value="Cari lagi" /></p>
			<?php endif; ?>
    </form>

    <ul>

        <?php echo '<span id="page-'.$page.'">'; ?>

        <?php foreach($websites as $result) { ?>
            <?php

            if ($i%$settings['links_per_page'] == 0) {
                $page++;
                echo '</span><span id="page-'.$page.'">';
            }
            $i++;

            ?>
            <li>
                <h1 class="title"><a href="go.php?url=<?php echo $result->url; ?>&<?php echo md5('zaf'); ?>=new" title="<?php echo $result->title; ?>"><?php echo $result->title; ?></a></h1>
                <p class="description"><?php echo $result->description; ?></p>
                <p class="link"><?php echo $result->url; ?></p>
                <script>
                    $(function() {
                        $('#report-1-<?php echo $i; ?>').hide();
                        $('#report-a-<?php echo $i; ?>').click(function() {
                            $('#report-2-<?php echo $i; ?>').hide();
                            $('#report-1-<?php echo $i; ?>').fadeIn();
                        });
                        $('#report-button-<?php echo $i; ?>').click(function() {
                            $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                            $.ajax({
                                type: "GET",
                                url: "ajax.php?report&link=<?php echo $result->url; ?>&reason=" + $('#report-input-<?php echo $i; ?>').val(),
                                success: function(data){
                                    console.log('report input : ' + $('#report-input-<?php echo $i; ?>').val());
                                    console.log(data);
                                    var status = $.parseJSON(data);
                                    if (status.status === 'benar') {
                                        $('#notifikasi').html("<p class='benar'>Berhasil dikirim!</p>");
                                        $('#report-<?php echo $i; ?>').fadeOut();
                                    } else {
                                        $('#notifikasi').html("<p class='salah'>Terjadi kesalahan!</p>");
                                    }
                                },
                                error: function(jqXHR, textStatus, errorThrown){
                                    $('#notifikasi').html("<p class='salah'>" + jqXHR.status+ " : " + jqXHR.statusText + "</p>");
                                }
                            });
                            setTimeout("$('#notifikasi').fadeOut()", 3000);
                        });
                    });
                </script>
                <div class="report" id="report-<?php echo $i; ?>">
                    <div id="report-1-<?php echo $i; ?>">
                        <input type="text" id="report-input-<?php echo $i; ?>" />
                        <button id="report-button-<?php echo $i; ?>">Kirim</button>
                    </div>
                    <div id="report-2-<?php echo $i; ?>">
                        <a id="report-a-<?php echo $i; ?>" href="#<?php echo md5('zaf'); ?>=new">Laporkan</a>
                    </div>
                </div>
            </li>
        <?php } ?>

        <?php echo '</span>'; ?>
    </ul>

    <?php if ($total != 0) : ?>

    <div class="pagination">
        <ul>
            <li>Halaman &rsaquo;&rsaquo; </li>
            <?php for ($i=1; $i<=$page; $i++) : ?>
            <li><a id="halaman-<?php echo $i; ?>" href="#halaman-<?php echo $i; ?>"><?php echo $i; ?></a></li>
            <?php endfor; ?>
        </ul>
    </div>

    <?php endif; ?>

    <script>
        function reset(){
            <?php for ($i=1; $i<=$page; $i++) : ?>
            $('#page-<?php echo $i; ?>').hide('slow');
            $('#halaman-<?php echo $i; ?>').removeClass('current');
            <?php endfor; ?>
        }
        $(function(){

            reset();
            $('#page-1').show('slow');
            $('#halaman-1').addClass('current');

            <?php for ($i=1; $i<=$page; $i++) : ?>
            $('#halaman-<?php echo $i; ?>').click(function(){
                reset();
                $('#page-<?php echo $i; ?>').show('slow');
                $('#halaman-<?php echo $i; ?>').addClass('current');
                return false;
            });
            <?php endfor; ?>
        });
    </script>
    
	<?php
}

?>
