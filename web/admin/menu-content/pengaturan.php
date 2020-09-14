<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/23/13
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */

$settings = get_settings();

?>

<script type="text/javascript">
    $(function(){
        $('#simpan').click(function(){
            $('#notifikasi').html("<p>Sedang menyimpan...</p>").hide().fadeIn();
            $.ajax({
                type: "POST",
                url: "../ajax.php?save_settings",
                data: $("#pengaturan-form").serialize(),
                success: function(data){
                    var status = $.parseJSON(data);
                    if (status.status === 'benar') {
                        $('#notifikasi').html("<p class='benar'>Berhasil disimpan...</p>");
                    } else {
                        $('#notifikasi').html("<p class='salah'>Terjadi kesalahan saat menyimpan pengaturan!</p>");
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

<h2 class="judul-halaman">Pengaturan Sistem Mesin Pencari</h2>

<form id="pengaturan-form">

<table class="pengaturan">
    <tbody>
    <tr>
        <td class="option"><pre>username</pre></td>
        <td class="separator">:</td>
        <td class="value"><input type="text" name="username" value="<?php echo $settings['username']; ?>"></td>
        <td class="description">Nama pengguna untuk autentikasi Administrator</td>
    </tr><tr>
        <td><pre>password</pre></td>
        <td>:</td>
        <td><input type="password" name="password" value="<?php echo $settings['password']; ?>"></td>
        <td class="description">Kata sandi untuk autentikasi Administrator</td>
    </tr><tr>
        <td><pre>max_deep</pre></td>
        <td>:</td>
        <td><input type="text" name="max_deep" value="<?php echo $settings['max_deep']; ?>"></td>
        <td class="description">Batas kedalaman URL yang akan di akses oleh Agent</td>
    </tr><tr>
        <td><pre>links_per_page</pre></td>
        <td>:</td>
        <td><input type="text" name="links_per_page" value="<?php echo $settings['links_per_page']; ?>"></td>
        <td class="description">Links per halaman yang akan ditampilkan pada Index Hasil pencarian</td>
    </tr><tr>
        <td><pre>host_directory_per_process</pre></td>
        <td>:</td>
        <td><input type="text" name="host_directory_per_process" value="<?php echo $settings['host_directory_per_process']; ?>"></td>
        <td class="description">Host directory yang akan load dari database untuk tiap proses</td>
    </tr><tr>
        <td><pre>max_links_received</pre></td>
        <td>:</td>
        <td><input type="text" name="max_links_received" value="<?php echo $settings['max_links_received']; ?>"></td>
        <td class="description">Jumlah hasil (ditemukan) maksimal yang akan terima oleh Server Agent</td>
    </tr><tr>
        <td><pre>max_links_crawled</pre></td>
        <td>:</td>
        <td><input type="text" name="max_links_crawled" value="<?php echo $settings['max_links_crawled']; ?>"></td>
        <td class="description">Jumlah links maksimum yang akan ditelusuri seluruh Agent</td>
    </tr><tr>
        <td><pre>min_percentage_match</pre></td>
        <td>:</td>
        <td><input type="text" name="min_percentage_match" value="<?php echo $settings['min_percentage_match']; ?>"> %</td>
        <td class="description">Minimum persentasi kata kunci yang cocok</td>
    </tr><tr>
        <td><pre>timeout_searching</pre></td>
        <td>:</td>
        <td><input type="text" name="timeout_searching" value="<?php echo $settings['timeout_searching']; ?>"> sec</td>
        <td class="description">Batas waktu mesin pencari melakukan pencarian</td>
    </tr>
    </tbody>
</table>
<br/>
<button id="simpan">Simpan</button>

</form>
