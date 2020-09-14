<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/22/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */

require_once ('../inc/function.php');

if (isset($_GET['keluar'])) {
    logout();
    header('Location: index.php');
}

?>

<html>
<head>
    <title>Administrator | Mesin Pencari menggunakan Web Bot Agent berbasis JADE</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="../css/style.css" />
    <script type="text/javascript" src="../js/jquery.min.js"></script>
</head>
<body <?php echo (isLoggedIn()) ? 'class="admin"' : ''; ?> >
<div class="container">
    <div id="notifikasi"></div>

        <?php if (!isLoggedIn()) : ?>

        <script type="text/javascript">
            $(function(){
                $('#submit').click(function(){
                    $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                    $.ajax({
                        type: "POST",
                        url: "../ajax.php?login",
                        data: $("#login-form").serialize(),
                        success: function(data){
                            var status = $.parseJSON(data);
                            if (status.status === 'benar') {
                                $('#notifikasi').html("<p class='benar'>Berhasil masuk...</p>");
                                window.location.replace("index.php");
                            } else {
                                $('#notifikasi').html("<p class='salah'>Nama Pengguna atau Kata Sandi salah!</p>");
                            }
                        },
                        error: function(jqXHR, textStatus, errorThrown){
                            $('#notifikasi').html("<p class='salah'>" + jqXHR.status+ " : " + jqXHR.statusText + "</p>");
                        }
                    });
                    setTimeout("$('#notifikasi').fadeOut()", 3000);
                    return false;
                });
            });
        </script>

    <div class="login">
        <form id="login-form">
        <label for="pengguna">Nama Pengguna</label><br/>
        <input id="pengguna" name="pengguna" type="text" /><br/>
        <label for="sandi">Kata Sandi</label><br/>
        <input id="sandi" name="sandi" type="password" /><br/>
        <input id="submit" type="submit" value="Masuk" /><input type="reset" value="Bersihkan" />
        </form>
    </div>
    <p class="back"><a href="../" title="Halaman depan">&lsaquo;&lsaquo; Halaman depan</a></p>
    <?php else: ?>
    <div class="admin-content">
        <?php $page = isset($_GET['page']) ? $_GET['page'] : 'statistik'; ?>
        <div class="menu-nav">
            <div class="logo">
                <a href="../"><img src="../img/logo.png" /></a>
            </div>
            <ul>
                <li><a href="?page=statistik" <?php echo $page == 'statistik' ? 'class="current"' : ''; ?> >Statistik</a></li>
                <li><a href="?page=laporan_url" <?php echo $page == 'laporan_url' ? 'class="current"' : ''; ?> >Laporan URL</a></li>
                <li><a href="?page=url_kiriman" <?php echo $page == 'url_kiriman' ? 'class="current"' : ''; ?> >URL Kiriman</a></li>
                <li><a href="?page=blok_situs" <?php echo $page == 'blok_situs' ? 'class="current"' : ''; ?> >Blok Situs</a></li>
                <li><a href="?page=pengaturan" <?php echo $page == 'pengaturan' ? 'class="current"' : ''; ?> >Pengaturan</a></li>
                <li><a href="?keluar">Keluar</a></li>
            </ul>
        </div>
        <div class="content-menu">
            <?php

                switch ($page) {
                    case 'statistik':
                        include_once ('menu-content/statistik.php');
                        break;
                    case 'laporan_url':
                        include_once ('menu-content/laporan_url.php');
                        break;
                    case 'url_kiriman':
                        include_once ('menu-content/url_kiriman.php');
                        break;
                    case 'blok_situs':
                        include_once ('menu-content/blok_situs.php');
                        break;
                    case 'pengaturan':
                        include_once ('menu-content/pengaturan.php');
                        break;
                    default:
                        include_once ('menu-content/404.php');
                        break;
                }

            ?>
        </div>
    </div>
    <?php endif ?>
</div>
</body>
</html>