<?php 
include('Net/SFTP.php');

$username = 'guest';
$password = 'rackrack';
$host = 'cvm-g1327111';

//Upload the photo using the userid 
function upload_photo($user_id,$_FILES){
	$Dir = 'photos/' . $user_id . '/';

	if(!is_dir($Dir)) {
	    mkdir($Dir, 0777,true);
	    chmod($Dir, 0777);
	}

	do {
	    $fileName = microtime();
		$fileName = preg_replace('/\s+/', '_', $fileName);
	} while (file_exists("$Dir". $fileName . '.jpg'));
	 
	$allowedExts = array("gif", "jpeg", "jpg", "png");
	$temp = explode(".", $_FILES["file"]["name"]);
	$extension = end($temp);

	if ((($_FILES["file"]["type"] == "image/gif")
	|| ($_FILES["file"]["type"] == "image/jpeg")
	|| ($_FILES["file"]["type"] == "image/jpg")
	|| ($_FILES["file"]["type"] == "image/pjpeg")
	|| ($_FILES["file"]["type"] == "image/x-png")
	|| ($_FILES["file"]["type"] == "image/png"))
	&& ($_FILES["file"]["size"] < 20000)
	&& in_array($extension, $allowedExts)) {
	  if ($_FILES["file"]["error"] > 0) {
	    echo "Return Code: " . $_FILES["file"]["error"] . "<br>";
	  } 
	  else {
	    move_uploaded_file($_FILES["file"]["tmp_name"],
	    "$Dir". $fileName . '.'. $extension);
	    }
	  }
	} else {
	  echo "Invalid file";
	}

	return array($fileName, $extension , "$Dir". $fileName . '.'. $extension);
}
	



function execute_tesseract($src_photo){
	$ssh = new Net_SSH2($host);
	if (!$ssh->login($username, $password)) {
	    exit('Login Failed');
	}
	$trans_text = 'out';
	$cmd = 'tesseract ' . $src_photo . ' ' . $trans_text . ' -psm 1';
	$ssh->exec($cmd);
	$cmd = 'cat ' . $trans_text . '.txt';
	$text =  $ssh->exec($cmd);
	unset($ssh);
	return $text;
}

function transfer_file_to_vm($src_photo,$dest_photo){
	$sftp = new Net_SFTP($host);
		if (!$sftp->login($username, $password)) {
		    exit('Login Failed');
		}

		// puts an x-byte file named photo on the SFTP server,
		// where x is the size of photo.jpg
		$sftp->put($dest_photo, $src_photo , NET_SFTP_LOCAL_FILE);
		unset($stfp);
}

function search($data){
	$ch = curl_init();
	$query = urlencode("'{$data}'");
    $fullUri = 'http://www.faroo.com/api?q='.$query.'&start=1&l=en&src=web&f=json&key=FsiN97uGtZCJZSg5iErp4T4bpbU_';
    curl_setopt($ch, CURLOPT_URL, $fullUri);
    echo $fullUri;
    curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
    $data=curl_exec($ch);
    $js = json_decode($data);
	$data = $js->results;
	foreach($data as $row){
		/*You can use the following fields: 
		* title, kwic (keyword in context) , content
		* url, iurl , news, votes, date, related. 
		*/
	}
    return $data;

    /*
    // TO USE BING API for better results.
	$acctKey = 'rt9rIpjp5a8649Zxw8rfcCJbvZa6PNKHF7HkOxMct/M';
    $rootUri =  'https://api.datamarket.azure.com/Bing/Search';  
    $query = urlencode("'wikipedia'");
    $requestUri = "$rootUri/Web?\$format=json&Query=$query";
    $auth = base64_encode("$acctKey:$acctKey");
    $data = array(
		'http' => array(
		'request_fulluri' => true,
		// ignore_errors can help debug – remove for production. This option added in PHP 5.2.10
		'ignore_errors' => true,
		'header' => "Authorization: Basic $auth")
		);
	
	$context = stream_context_create($data);
	// Get the response from Bing.
	$response = file_get_contents($requestUri, 0, $context);
   
	$jsonObj = json_decode($response);
	return $jsonObj->d->results;
    */
}

?>