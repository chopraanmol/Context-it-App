<?php
error_reporting(E_ERROR);



function create_photo($user_id,$photo_path){
    // array for JSON response
    $response = array();
 
    $id = $user_id;
    $path = $photo_path;
 
    // include db connect class
    require_once '/vol/project/2013/271/g1327111/db/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    if(!$db->has_connected) {
        $response["status"] = 2;
        unset($db);
        return $response["status"];
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
    return $response["status"];
}

?>
