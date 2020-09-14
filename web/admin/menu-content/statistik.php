<?php
/**
 * Created by JetBrains PhpStorm.
 * User: zaf
 * Date: 3/23/13
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */


$x = 3;

$trend_searched = get_trend_searched($x);

$data = array();
for($i=0; $i<count($trend_searched); $i++) {
    $data[$i] = array();
    for ($j=0; $j<12; $j++) {
        $tmp = get_searched(date('Y').'-'.($j+1).'-1', date('Y').'-'.($j+1).'-30', $trend_searched[$i]['keyword']);
        $data[$i][] = $tmp[0]['count'];
        //echo 'keyword '.$trend_searched[$i]['keyword'].' pada bulan '.($j+1).' ada count : '.$tmp[0]['count'].'<br>';
    }
}

$warna = array('rgba(220,220,220,0.5)', 'rgba(151,187,205,0.5)', 'rgba(51,87,5,0.5)');

?>

<h2 class="judul-halaman">Informasi statistik kata kunci yang dicari setiap bulan</h2>

<script type="text/javascript" src="../js/Chart.min.js"></script>

<p class="ket-y" style="display: inline-block;color: #A70000;-webkit-transform: rotate(-90deg);position: absolute;left: 250px;top: 265px;font-size: 20px;">Jumlah Klik</p>
<canvas id="canvas" height="450" width="800"></canvas>
<p class="ket-x" style=" color: #A70000;padding-left: 400px;padding-top: 10px;font-size: 20px;">Bulan</p>

<script type="text/javascript">

    $(function(){
        $('#bersihin').click(function(){
            $('#notifikasi').html("<p>Sedang membersihkan</p>").hide().fadeIn();
            $.ajax({
                type: "POST",
                url: "../ajax.php?clear_db",
                success: function(data){
                    var status = $.parseJSON(data);
                    if (status.status === 'benar') {
                        $('#notifikasi').html("<p class='benar'>Berhasil dibersihkan!</p>");
                    } else {
                        $('#notifikasi').html("<p class='salah'>Terjadi kesalahan saat melakukan pembersihan!</p>");
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
	
	var lineChartData = {
			labels : ["Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"],
			datasets : [
                <?php for($i=0; $i<count($data); $i++) : ?>
				{
					fillColor : "<?php echo $warna[$i]; ?>",
					strokeColor : "<?php echo $warna[$i]; ?>",
					pointColor : "<?php echo $warna[$i]; ?>",
					pointStrokeColor : "#fff",
                    data : [<?php for($j=0; $j<count($data[$i]); $j++) echo empty($data[$i][$j]) ? '0, ' : $data[$i][$j].', '; ?>]
				},
                <?php endfor; ?>
			]
			
		}

	var myLine = new Chart(document.getElementById("canvas").getContext("2d")).Line(lineChartData);

</script>

<br/><br/>

<div class="statistik">
    <?php for($i=0; $i<count($trend_searched); $i++) : ?>
    <p><span style="background-color: <?php echo $warna[$i]; ?>;">&nbsp;&nbsp;&nbsp;&nbsp;</span> <?php echo $trend_searched[$i]['keyword']; ?></p>
    <?php endfor; ?>
</div>

<p>Klik untuk membersihkan isi table host_directory dan websites <button id="bersihin">bersihin</button></p>
