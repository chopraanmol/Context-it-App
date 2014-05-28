<?php
 error_reporting(E_ERROR);
//ALSO CHANGE THE SUCCESS VALUES TO REFLECT WHAT WENT WRONG RATHER THAN A MESSAGE!!
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();

$input = json_decode(file_get_contents('php://input'), true);

// check for required fields
if (isset($input['user_id'])) {
 
    $id = $input['user_id'];
 
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
    // pgsql inserting a new row
    $result = pg_query_params($db->con,'INSERT INTO users(user_id) VALUES($1)', array($id));

    unset($db);
    
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response['success'] = 0;
    $response['message'] = 'Required field(s) is missing';
    $r = var_dump ($_GET);
	echo $r;
    // echoing JSON response
    echo json_encode($response);
}
?>
