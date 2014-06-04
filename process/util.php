<?php 
include('Net/SFTP.php');
include 'resize_image.php';

$username = 'guest';
$password = 'rackrack';
$host = 'cvm-g1327111';

//Upload the photo using the userid 
function upload_image($user_id){
	$Dir = 'photos/' . $user_id . '/';

	if(!is_dir($Dir)) {
      mkdir($Dir, 0777);
	  chmod($Dir, 0777);
	}

	do {
    $fileName = microtime();
		$fileName = preg_replace('/\s+/', '_', $fileName);
	} while (file_exists("$Dir". $fileName . '.jpg'));
	 
	$allowedExts = array("jpeg", "jpg", "png");
	$temp = explode(".", $_FILES["file"]["name"]);
	$extension = end($temp);

	if (in_array($extension, $allowedExts)) {
	    move_uploaded_file($_FILES["file"]["tmp_name"],
	        "$Dir". $fileName . '.'. $extension);

	    $image = new ResizeImage("$Dir". $fileName . '.'. $extension);
		$image->resizeTo(100, 100, 'exact');
		$image->saveImage("$Dir". $fileName .'_thumbnail' .'.'. $extension);
	}	
	return array($fileName, $extension , "$Dir". $fileName . '.'. $extension);
}
	
function execute_tesseract($src_photo){
	$result = array();
	$ssh = new Net_SSH2($GLOBALS['host']);
	if (!$ssh->login($GLOBALS['username'], $GLOBALS['password'])) {
	    return null;
	}
	$array = explode(".", $src_photo);
	$trans_text = $array[0];
	$tesseract = 'tesseract ' . $src_photo . ' ' . $trans_text . ' -psm 1';
	$cleaning = './textcleaner -g -e none -f 10 -o 5'. $src_photo .$src_photo;
	$ssh->exec($cleaning);
	$ssh->exec($tesseract);
	$cmd = 'cat ' . $trans_text . '.txt';
	$text =  $ssh->exec($cmd);
	$text = preg_replace('/[\s\W]+/', "", $text);
	$result[0] = $text;
	$result[1] = $ssh->exec("php script.php '".$text."'");
	unset($ssh);
	return $result;
}

function transfer_file_to_vm($src_photo,$dest_photo){
	$sftp = new Net_SFTP($GLOBALS['host']);
	if (!$sftp->login($GLOBALS['username'], $GLOBALS['password'])) {
	    return null;
	}
	$sftp->put($dest_photo, $src_photo , NET_SFTP_LOCAL_FILE);
	unset($stfp);
	return true;
}

function searchFaroo($data){
	$ch = curl_init();
	$query = urlencode("'{$data}'");
  $fullUri = 'http://www.faroo.com/api?q='.$query.'&start=1&l=en&src=web&f=json&key=FsiN97uGtZCJZSg5iErp4T4bpbU_';
  curl_setopt($ch, CURLOPT_URL, $fullUri);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
  $data=curl_exec($ch);
  $js = json_decode($data);
	$data = $js->results;
	$urls = array();
	foreach($data as $row){
		array_push($urls,array("desc"=> $row->title, "url" => $row->url));
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
	'ignore_errors' => true,
	'header' => "Authorization: Basic $auth")
	);
	
  $context = stream_context_create($data);
  // Get the response from Bing.
  $response = file_get_contents($requestUri, 0, $context);  
  $jsonObj = json_decode($response);
  $urls = array();
  for($i = 0; $i < 10; $i++){	
	$result = $jsonObj->d->results[$i]; 		
	array_push($urls,array("desc"=> $result->Title,"url" => $result->Url));	
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
}	

?>
