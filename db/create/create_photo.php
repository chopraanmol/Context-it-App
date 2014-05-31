<?php
error_reporting(E_ERROR);

// array for JSON response
$response = array();

$input = json_decode(file_get_contents('php://input'), true);

// check for required fields
if (isset($input['user_id']) && isset($input['photo_path'])) {
 
    $id = $input['user_id'];
    $path = $input['photo_path'];
 
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
    $result = pg_query_params($db->con,'INSERT INTO photos(user_id, photo_path) VALUES($1, $2)', array($id, $path));
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
