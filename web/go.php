<?php

require_once ( 'inc/function.php' );

/**
 * antisipasi jika parameter url link sama
 * dengan parameter yang kita gunakan untuk update kolom visited
 */
if (isset($_GET['url']) && isset($_GET[md5('zaf')])) {
    $id = $_GET[md5('zaf')];
    if ('new' == $id) {
        /**
         * update berdasarkan url
         */
        if (update_visit_count_by_url($_GET['url'])) {
            header( 'Location: ' . $_GET['url'] );
        } else {
            die ( 'Invalid parameter! code : url' );
        }
    } else if (update_visit_count_by_id($id)) {
        header( 'Location: ' . $_GET['url'] );
    } else {
        die ( 'Invalid parameter! code : id' );
    }
}

?>
