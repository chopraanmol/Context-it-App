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
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("DELETE FROM photos WHERE user_id = $uid AND photo_path = $photo_path");
 
    // check if row deleted or not
    if (pg_affected_rows() > 0) {
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