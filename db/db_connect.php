<?php
//use new to create an instance of this class which will also connect to the database.
//If connection was not successful, $this->has_connected will be = false. So please check 
//the value after creating an instance of this class.

//To close the connection to the database, please use unset() on this object.

//error_reporting(E_ERROR);
class DB_CONNECT {
	
    public $has_connected = true;
    public $con;
    function __construct() {
        $this->connect();
	if(!$this->con) {
	echo "did not connect!";
		$this->has_connected = false;
	}

    }
 
    function __destruct() {
        $this->close();
    }
 
    private function connect() {
        require_once __DIR__ . '/db_config.php';
        $this->con = pg_connect(PG_INFO);
    }
 
    private function close() {
	if($this->con) {
        	pg_close($this->con);
	}
    }
}
?>
