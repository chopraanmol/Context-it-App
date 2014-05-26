<?php
 
/*
 * Following code will create a new links row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['photo_id'] && isset($_POST['urls'])) {
 
    $id = $_POST['photo_id'];
    $urls = $_POST['urls'];     

    // include db connect class
    require_once '../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

    $link = array();
    foreach ($urls as $value) {
        // pgsql inserting a new row
        $result = pg_query("INSERT INTO weburls VALUES('$id', '$value'");
        if(!$result){
            array_push($link,$value);
        }    
    }    
 
    // check if row inserted or not
    if (count($link) == 0) {
        // successfully inserted into database
        $response["success"] = 1;
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        $response["not_included"] = $link;
 
        // echoing JSON response
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