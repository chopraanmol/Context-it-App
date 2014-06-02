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
	 
	$allowedExts = array("jpeg", "jpg", "png");
	$temp = explode(".", $_FILES["file"]["name"]);
	$extension = end($temp);

	if ((($_FILES["file"]["type"] == "image/jpeg")
	|| ($_FILES["file"]["type"] == "image/jpg")
	|| ($_FILES["file"]["type"] == "image/pjpeg")
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

	return array($fileName, $extension , "$Dir". $fileName . '.'. $extension);
}
	



function execute_tesseract($src_photo){
	$result = array();
	$ssh = new Net_SSH2($GLOBALS['host']);
	if (!$ssh->login($GLOBALS['username'], $GLOBALS['password'])) {
	    exit('Login Failed');
	}
	$trans_text = 'out';
	$cmd = 'tesseract ' . $src_photo . ' ' . $trans_text . ' -psm 1';
	$ssh->exec($cmd);
	$cmd = 'cat ' . $trans_text . '.txt';
	$text =  $ssh->exec($cmd);
	$text = preg_replace("/[0-9:'|=,.]/", "", $text);
	$result[0] = $text;
	$result[1] = $ssh->exec("php script.php '".$text."'");
	unset($ssh);
	return $result;
}

function transfer_file_to_vm($src_photo,$dest_photo){
	$sftp = new Net_SFTP($GLOBALS['host']);
		if (!$sftp->login($GLOBALS['username'], $GLOBALS['password'])) {
		    exit('Login Failed');
		}

		// puts an x-byte file named photo on the SFTP server,
		// where x is the size of photo.jpg
		$sftp->put($dest_photo, $src_photo , NET_SFTP_LOCAL_FILE);
		unset($stfp);
}

function searchFaroo($data){
	$ch = curl_init();
	$query = urlencode("'{$data}'");
    $fullUri = 'http://www.faroo.com/api?q='.$query.'&start=1&l=en&src=web&f=json&key=FsiN97uGtZCJZSg5iErp4T4bpbU_';
    curl_setopt($ch, CURLOPT_URL, $fullUri);
    echo $fullUri;
    curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
    $data=curl_exec($ch);
    $js = json_decode($data);
	$data = $js->results;
	$urls = array();
	foreach($data as $row){
		array_push($urls,$row->url);
		/*You can use the following fields: 
		* title, kwic (keyword in context) , content
		* url, iurl , news, votes, date, related. 
		*/
	}
    return $urls;
}


   // TO USE BING API for better results.
function searchBing($data){
	$acctKey = 'rt9rIpjp5a8649Zxw8rfcCJbvZa6PNKHF7HkOxMct/M';
    $rootUri =  'https://api.datamarket.azure.com/Bing/Search';  
    $query = urlencode("'$data'");
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
	$urls = array();
	foreach($jsonObj->d->results as $result){
		array_push($urls,$result->Url);
	}
	return $urls;

}


//Split the words into a 2-d array based on line number. 
function getArrayFromString($text){
	$array = preg_split("/\r\n|\n|\r/", $text); // Split the result by newline. 
	$recognized_words = array();
	for($i = 0; $i < count($array); $i++){
		$line = preg_split("/[\s,]+/", $array[$i]); // split each line into words. 		 		
		foreach($line as $word){			
			if($word != ''){
				if(!is_array($recognized_words[$i])){ 
					$recognized_words[$i] = array();
				}
				array_push($recognized_words[$i],$word); 
			}									
		}
	}
	return $recognized_words;
		

?>