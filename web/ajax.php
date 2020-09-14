<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/23/13
 * Time: 1:30 AM
 * To change this template use File | Settings | File Templates.
 */

require_once ('inc/function.php');

if (isset($_GET['login'])) {
    $u = stripslashes($_POST['pengguna']);
    $p = stripslashes($_POST['sandi']);
    $status = login($u, $p);
    if ($status) setcookie("Zaf", md5($p), time()+(24*3600), '/');
    $result = array(
        'status' => $status ? 'benar' : 'salah'
    );
    echo json_encode($result);
} else if (isset($_GET['save_settings'])) {
    $settings = array(
        'username' => $_POST['username'],
        'password' => $_POST['password'],
        'max_deep' => $_POST['max_deep'],
        'links_per_page' => $_POST['links_per_page'],
        'host_directory_per_process' => $_POST['host_directory_per_process'],
        'max_links_received' => $_POST['max_links_received'],
        'max_links_crawled' => $_POST['max_links_crawled'],
        'min_percentage_match' => $_POST['min_percentage_match'],
        'timeout_searching' => $_POST['timeout_searching']
    );
    $status = save_settings($settings);
    $result = array(
        'status' => $status ? 'benar' : 'salah'
    );
    echo json_encode($result);
} else if (isset($_GET['tambah_situs'])) {
    $status = add_sites($_POST['situs-baru']);
    $result = array(
        'status' => $status ? 'benar' : 'salah'
    );
    echo json_encode($result);
} else if (isset($_GET['accept_site_submitted'])) {
    $status = accept_site_submitted($_GET['id']);
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'id' => $_GET['id']
    );
    echo json_encode($result);
} else if (isset($_GET['decline_site_submitted'])) {
    $status = decline_site_submitted($_GET['id']);
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'id' => $_GET['id']
    );
    echo json_encode($result);
} else if (isset($_GET['report'])) {
    $status = add_report($_GET['link'], $_GET['reason']);
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'link' => $_GET['link'],
        'reason' => $_GET['reason']
    );
    echo json_encode($result);
} else if (isset($_GET['blok_site_reported'])) {
    $status = blok_site_reported($_GET['id']);
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'id' => $_GET['id']
    );
    echo json_encode($result);
} else if (isset($_GET['ignore_site_reported'])) {
    $status = ignore_site_reported($_GET['id']);
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'id' => $_GET['id']
    );
    echo json_encode($result);
} else if (isset($_GET['delete_site_blocked'])) {
    $status = delete_site_blocked($_GET['id']);
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'id' => $_GET['id']
    );
    echo json_encode($result);
} else if (isset($_GET['clear_db'])) {
    $status = truncate_websites() && truncate_host_directory();
    pg_query("INSERT INTO host_directory (address) VALUES ('http://localhost/installed/joomla/')");
    pg_query("INSERT INTO host_directory (address) VALUES ('http://localhost/references/codex-wordpress/codex.wordpress.org/')");
    pg_query("INSERT INTO host_directory (address) VALUES ('http://localhost/references/php-manual/')");
    pg_query("INSERT INTO host_directory (address) VALUES ('http://localhost/references/w3schools/www.w3schools.com/')");
    pg_query("INSERT INTO host_directory (address) VALUES ('http://localhost/references/android-docs/')");
    $result = array(
        'status' => $status ? 'benar' : 'salah',
        'id' => $_GET['id']
    );
    echo json_encode($result);
}

?>
