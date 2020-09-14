<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/23/13
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */

?>

<h2 class="judul-halaman">Daftar Situs yang di Blok</h2>

<!--
<input type="text" class="tambah-blok-situs" name="tambah-blok-situs" placeholder="http://..." /> <button>Tambah Situs</button> Tambahkan situs yang akan di blok dengan menekan fitur <i>Tambah Situs</i> disamping
<br/><br/>
-->

<?php $site_blocked = get_site_blocked(); ?>

<table>
    <tbody>
    <tr>
        <th>Tanggal</th>
        <th>Situs</th>
        <th>Aksi</th>
    </tr>
    <?php foreach($site_blocked as $site) : ?>
    <script>
        $(function() {
            $('#hapus-<?php echo $site['id']; ?>').click(function() {
                $('#notifikasi').html("<p>Sedang mengirim...</p>").hide().fadeIn();
                $.ajax({
                    type: "POST",
                    url: "../ajax.php?delete_site_blocked&id=<?php echo $site['id']; ?>",
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
        <td class="aksi"><button id="hapus-<?php echo $site['id']; ?>">Hapus</button></td>
    </tr>
    <?php endforeach; ?>
    </tbody>
</table>