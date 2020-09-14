<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/23/13
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */

?>

<h2 class="judul-halaman">Daftar URL yang dilaporkan oleh client</h2>

<?php $site_reported = get_site_reported(); ?>

<table>
    <tbody>
    <tr>
        <th>Tanggal</th>
        <th>Situs</th>
        <th>Alasan</th>
        <th>Aksi</th>
    </tr>
    <?php foreach($site_reported as $site) : ?>
        <script>
            $(function() {
                $('#blok-<?php echo $site['id']; ?>').click(function() {
                    $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                    $.ajax({
                        type: "POST",
                        url: "../ajax.php?blok_site_reported&id=<?php echo $site['id']; ?>",
                        success: function(data){
                            console.log(data);
                            var status = $.parseJSON(data);
                            if (status.status === 'benar') {
                                $('#notifikasi').html("<p class='benar'>Berhasil disimpan!</p>");
                                $('#site-<?php echo $site['id']; ?>').fadeOut();
                            } else {
                                $('#notifikasi').html("<p class='salah'>Terjadi kesalahan!</p>");
                            }
                        },
                        error: function(jqXHR, textStatus, errorThrown){
                            $('#notifikasi').html("<p class='salah'>" + jqXHR.status+ " : " + jqXHR.statusText + "</p>");
                        }
                    });
                    setTimeout("$('#notifikasi').fadeOut()", 3000);
                    //alert('test');
                });

                $('#abaikan-<?php echo $site['id']; ?>').click(function() {
                    $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                    $.ajax({
                        type: "POST",
                        url: "../ajax.php?ignore_site_reported&id=<?php echo $site['id']; ?>",
                        success: function(data){
                            console.log(data);
                            var status = $.parseJSON(data);
                            if (status.status === 'benar') {
                                $('#notifikasi').html("<p class='benar'>Berhasil dihapus!</p>");
                                $('#site-<?php echo $site['id']; ?>').fadeOut();
                            } else {
                                $('#notifikasi').html("<p class='salah'>Terjadi kesalahan!</p>");
                            }
                        },
                        error: function(jqXHR, textStatus, errorThrown){
                            $('#notifikasi').html("<p class='salah'>" + jqXHR.status+ " : " + jqXHR.statusText + "</p>");
                        }
                    });
                    setTimeout("$('#notifikasi').fadeOut()", 3000);
                    //alert('test');
                });
            });
        </script>
        <tr id="site-<?php echo $site['id']; ?>">
            <td class="tanggal"><?php echo $site['date']; ?></td>
            <td class="situs">
                <a href="<?php echo $site['link']; ?>"><?php echo $site['link']; ?></a>
            </td>
            <td class="alasan">
                <p><?php echo $site['reason']; ?></p>
                IP : <?php echo $site['ip_sender']; ?>
            </td>
            <td class="aksi"><button id="blok-<?php echo $site['id']; ?>">Blok Situs</button> <button id="abaikan-<?php echo $site['id']; ?>">Abaikan</button></td>
        </tr>
    <?php endforeach; ?>
    </tbody>
</table>
