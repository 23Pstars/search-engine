<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 12/17/12
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */

require_once ('inc/function.php');

?>

<html>
<head>
    <title>Mesin Pencari menggunakan Web Bot Agent berbasis JADE</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="css/style.css" />
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script>
        $(function() {
            $('#tambah-situs-baru').click(function() {
                $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                $.ajax({
                    type: "POST",
                    url: "ajax.php?tambah_situs",
                    data: $("#situs-baru-form").serialize(),
                    success: function(data){
                        console.log(this);
                        var status = $.parseJSON(data);
                        if (status.status === 'benar') {
                            $('#notifikasi').html("<p class='benar'>URL situs berhasil dikirim!</p>");
                            $('#situs-baru').val('');
                        } else {
                            $('#notifikasi').html("<p class='salah'>Terjadi kesalahan saat mengirim URL situs!</p>");
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
</head>
<body>
<div class="container">
    <?php include_once('inc/header.php'); ?>

        <div class="search-box">
            <form id="situs-baru-form" action="" method="get">
                <input class="add-new-site-field" id="situs-baru" name="situs-baru" type="text" placeholder="Contoh: http://ftunram.ac.id/" /> <input id="tambah-situs-baru" class="search-button" type="submit" value="Tambah" />
                <div class="info-add-new-site">
                    <p>Bagaimanapun kami akan memeriksa terlebih dahulu situs yang anda masukkan sebelum menampilkannya dalam pencarian. Kembali ke <a href="index.php">Pencarian</a>?</p>
                </div>
            </form>
        </div>

    <?php include_once('inc/footer.php'); ?>
</div>
</body>
</html>
