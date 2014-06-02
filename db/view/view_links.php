#!/usr/bin/php
<?php
error_reporting(E_ERROR);
$response = array();
$response["weburls"] = array();
$response["status"] = -1;

$input = $POST;

// connecting to db
require_once '../db_connect.php';
$db = new DB_CONNECT();
if(!$db->has_connected) {
	$response["status"] = 2;
	unset($db);
        echo json_encode($response);
	exit;
}

if (isset($input['photo_id'])) {
    $id = $input['photo_id'];
    $result = pg_query_params($db->con,'SELECT * FROM weburls WHERE photo_id = $1', array($id));
    unset($db);
    if ($result) {
            $result = pg_fetch_all($result);
 	    foreach($result as $row){
	            array_push($response["weburls"], $row["url"]);
 	    }
 	    $response["status"] = 1;
    } else {
        $response["status"] = 3;
    }
} else {
    $response["success"] = 4;
}
echo json_encode($response);
?>
