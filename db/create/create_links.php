#!/usr/bin/php
<?php
error_reporting(E_ERROR);


// array for JSON response
$response = array();

$input = $POST;

// check for required fields
if (isset($input['url']) && isset($input['photo_id'])) {
 
    $url = $input['url'];
    $id = $input['photo_id'];
 
    // include db connect class
    require_once '../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    if(!$db->has_connected) {
	$response["status"] = 2;
	unset($db);
        echo json_encode($response);
        exit;
    }

    // pgsql inserting a new row
    $result = pg_query_params($db->con,'INSERT INTO weburls VALUES($1, $2)', array($id, $url));
    unset($db);
    
    //if insertion was successful, return status code 1, else 0.
    if ($result) {
        $response["status"] = 1;
    } else {
        $response["status"] = 3;
    }
} else {
    // required field is missing
    $response['status'] = 4;
}
echo json_encode($response);
?>
