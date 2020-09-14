<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/23/13
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */

?>

<h2 class="judul-halaman">Daftar URL yang dikirimkan oleh client</h2>

<?php $site_submitted = get_site_submitted(); ?>

<table>
    <tbody>
    <tr>
        <th>Tanggal</th>
        <th>Situs</th>
        <th>Pengirim</th>
        <th>Aksi</th>
    </tr>
    <?php foreach($site_submitted as $site) : ?>
    <script>
        $(function() {
            $('#terima-<?php echo $site['id']; ?>').click(function() {
                $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                $.ajax({
                    type: "POST",
                    url: "../ajax.php?accept_site_submitted&id=<?php echo $site['id']; ?>",
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

            $('#tolak-<?php echo $site['id']; ?>').click(function() {
                $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                $.ajax({
                    type: "POST",
                    url: "../ajax.php?decline_site_submitted&id=<?php echo $site['id']; ?>",
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
        <td class="pengirim">
            <?php echo $site['ip_sender']; ?>
        </td>
        <td class="aksi"><button id="terima-<?php echo $site['id']; ?>">Terima</button> <button id="tolak-<?php echo $site['id']; ?>">Tolak</button></td>
    </tr>
    <?php endforeach; ?>
    </tbody>
</table>
