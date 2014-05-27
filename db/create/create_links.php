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
    if(!$db->has_connected) {
	$response["success"] = 0;
        $response["message"] = "Database could not be opened";
	
	   // echoing JSON response
       echo json_encode($response);
	   exit;
    }

    $links_not_added = array();
    foreach ($urls as $value) {
        // pgsql inserting a new row
        $result = pg_query("INSERT INTO weburls VALUES('$id', '$value')");
        if(!$result){
            array_push($links_not_added,$value);
        }    
    }    
    unset($db);
    // check if row inserted or not
    if (count($links_not_added) == 0) {
        // successfully inserted into database
        $response["success"] = 1;
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        $response["not_included"] = $links_not_added;
 
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
