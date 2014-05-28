#!/usr/bin/php
<?php
error_reporting(E_ERROR);
$response = array();
$response["photo_paths"] = array();
$response["status"] = -1;

$input = json_decode(file_get_contents('php://input'), true);

// connecting to db
require_once '../db_connect.php';
$db = new DB_CONNECT();
if(!$db->has_connected) {
	$response["status"] = 2;
	unset($db);
        echo json_encode($response);
	exit;
}

if (isset($input['user_id'])) {
    $uid = $input['user_id'];
    $result = pg_query_params($db->con,'SELECT * FROM photos WHERE user_id = $1', array($uid));
    unset($db);
    if ($result) {
            $result = pg_fetch_all($result);
 	    foreach($result as $row){
	            array_push($response["photo_paths"], $row["photo_path"]);
 	    }
 	    $response["status"] = 1;
    } else {
        $response["status"] = 3;
    }
} else {
    // required field is missing
    $response["success"] = 4;
}
echo json_encode($response);
?>
