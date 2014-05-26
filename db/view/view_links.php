<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["photo_id"])) {
    $photo_id = $_GET['photo_id'];
    // get a product from products table
    $result = pg_query("SELECT * FROM weburls WHERE photo_id = '$photo_id'");
 
    if (!empty($result)) {
        // check for empty result
        if (pg_num_rows($result) > 0) {
 
            $result = pg_fetch_all($result);
            $response["weburls"] = array();
 			foreach($result as $row){
	            array_push($response["weburls"], $row["url"]);
 			}   
 			$response["success"] = 1;
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No links found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No links found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Field missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>