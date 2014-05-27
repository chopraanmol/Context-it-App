#!\usr\bin\php
<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';

// connecting to db
$db = new DB_CONNECT();
if(!$db->has_connected) {
	$response["success"] = 0;
        $response["message"] = "Database could not be opened";
	
	   // echoing JSON response
       echo json_encode($response);
	   exit;
    }
// check for post data
if (isset($_GET["user_id"])) {
    $uid = $_GET['user_id'];
 	
    // get a product from products table
    $result = pg_query("SELECT * FROM photos WHERE user_id = '$uid'");
 
    if (!empty($result)) {
        // check for empty result
        if (pg_num_rows($result) > 0) {
 
            $result = pg_fetch_all($result);
            $response["photos_paths"] = array();
 			foreach($result as $row){
	            array_push($response["photo_paths"], $row["photo_path"]);
 			}   
 			$response["success"] = 1;
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No product found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No product found";
 
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
