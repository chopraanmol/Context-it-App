<?php
 
/*
 * Following code will delete a product from table
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['user_id']) && isset($_POST['photo_path'])) {
    $uid = $_POST['user_id'];
    $photo_path = $_POST['photo_path'];

    // include db connect class
    require_once '../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    if(!$db->has_connected) {
	$response["success"] = 0;
        $response["message"] = "Database could not be opened";
	
	   // echoing JSON response
       echo json_encode($response);
	   exit;
    }
    // mysql update row with matched pid
    $result = pg_query("DELETE FROM photos WHERE user_id = '$uid' AND photo_path = '$photo_path'");
 
    unset($db);
    
    // check if row deleted or not
    if (pg_affected_rows($result) > 0) {
        // successfully updated
        $response["success"] = 1;
        
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No photo found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
