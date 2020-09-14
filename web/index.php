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
</head>
<body>
<div class="container">
    <?php include_once('inc/header.php'); ?>
    <?php

        if (isset($_GET['keyword'])) {

            $settings = get_settings();

            $timeout = false;

            $i = 1;
            $page = 1;

    ?>
        <div class="search-result">

            <?php $websites = search_on_database($_GET['keyword']); ?>

            <?php $total = count($websites); ?>

            <?php if ($websites) { ?>

            <form action="" method="get">
            <p class="notif">Terdapat <span><?php echo $total; ?></span> hasil dari kata kunci <input class="keyword-field" name="keyword" type="text" placeholder="Tulis kata kunci..." value='<?php echo stripslashes($_GET['keyword']); ?>' /> <input class="search-button" type="submit" value="Cari lagi" /></p>
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
                            <h1 class="title"><a href="go.php?url=<?php echo $result['link']; ?>&<?php echo md5('zaf'); ?>=<?php echo $result['id']; ?>" title="<?php echo $result['title']; ?>"><?php echo $result['title']; ?></a></h1>
                            <p class="description"><?php echo $result['description']; ?></p>
                            <p class="link"><?php echo $result['link']; ?></p>
                            <script>
                                $(function() {
                                    $('#report-1-<?php echo $result['id']; ?>').hide();
                                    $('#report-a-<?php echo $result['id']; ?>').click(function() {
                                        $('#report-2-<?php echo $result['id']; ?>').hide();
                                        $('#report-1-<?php echo $result['id']; ?>').fadeIn();
                                    });
                                    $('#report-button-<?php echo $result['id']; ?>').click(function() {
                                        $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                                        $.ajax({
                                            type: "GET",
                                            url: "ajax.php?report&link=<?php echo $result['link']; ?>&reason=" + $('#report-input-<?php echo $result['id']; ?>').val(),
                                            success: function(data){
                                                console.log('report input : ' + $('#report-input-<?php echo $result['id']; ?>').val());
                                                console.log(data);
                                                var status = $.parseJSON(data);
                                                if (status.status === 'benar') {
                                                    $('#notifikasi').html("<p class='benar'>Berhasil dikirim!</p>");
                                                    $('#report-<?php echo $result['id']; ?>').fadeOut();
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
                            <div class="report" id="report-<?php echo $result['id']; ?>">
                                <div id="report-1-<?php echo $result['id']; ?>">
                                    <input type="text" id="report-input-<?php echo $result['id']; ?>" />
                                    <button id="report-button-<?php echo $result['id']; ?>">Kirim</button>
                                </div>
                                <div id="report-2-<?php echo $result['id']; ?>">
                                    <a id="report-a-<?php echo $result['id']; ?>" href="#<?php echo md5('zaf'); ?>=<?php echo $result['id']; ?>">Laporkan</a>
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

        <?php } else { ?>

            <div id="ajax-retrieve">

                <p class="notif">
                    Kata kunci <span>"<?php echo stripslashes($_GET['keyword']); ?>"</span> belum ada dalam database,
                    mohon menunggu beberapa saat sementara mesin pencari melakukan pencarian langsung di Internet.
                </p>
                <div>
                    <img src="img/ajax-loader.gif" /> <p>mohon menunggu...</p>
                </div>

            </div>

            <script type="text/javascript">
                $(function(){
                    $.ajax({
                        url: "search.php?keyword=<?php echo $_GET['keyword']; ?>",
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        success: function(data){
                            /* kosongin html */
                            $('#ajax-retrieve').html('');
                            /* tambahkan response ajax */
                            $('#ajax-retrieve').append(data);
                        }
                    });
                });
            </script>


        <?php } ?>

        </div> <!-- search-result -->

    <?php

        } else {

    ?>

        <div class="search-box">
            <form action="" method="get">
                <img src="img/logo.png" title="Logo Mesin Pencari" alt="Logo Mesin Pencari" /><br/>
                <input class="keyword-field" name="keyword" type="text" placeholder="Tulis kata kunci..." /> <input class="search-button" type="submit" value="Cari" />
            </form>
            <br/>
            <p><a class="tambah-situs" href="tambah_situs.php">Tambahkan situs?</a></p>
        </div>

    <?php

        }

    ?>

    <?php include_once('inc/footer.php'); ?>
</div>
</body>
</html>
