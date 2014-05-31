<?php
error_reporting(E_ERROR);
$uploads_dir = __DIR__ . '/uploads';
$status = -1;
    if ($_FILES["picture"]["error"] == UPLOAD_ERR_OK) {
        $tmp_name = $_FILES["picture"]["tmp_name"];
        $name = $_FILES["picture"]["name"];
        $status = move_uploaded_file($tmp_name, "$uploads_dir/$name");
    }
$response["status"] = $status;
$response["user_id"] = $_POST["user_id"];
$response["name"] =  $name;
$response["extension"] = end (explode(".", $name));	
/*
// array for JSON response
$response = array();

$input = $_POST;

// check for required fields
if (isset($input['photo_path']) && isset($input['user_id'])) {
 
    $path = $input['photo_path'];
    $id = $input['user_id'];
 
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
    $result = pg_query_params($db->con,'DELETE FROM photos WHERE user_id = $1 AND photo_path = $2', array($id, $path));
    unset($db);
    
    //if insertion was successful, return status code 1, else 0.
    if (pg_affected_rows($result) > 0) {
        $response["status"] = 1;
    } else {
        $response["status"] = 3;
    }
} else {
    // required field is missing
    $response['status'] = 4;
}*/
echo json_encode($response);
?>
