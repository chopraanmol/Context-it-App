<?php
set_include_path(get_include_path() . PATH_SEPARATOR . 'phpseclib');

include('Net/SFTP.php');

$username = 'guest';
$password = 'rackrack';
$host = 'cvm-g1327111';

	//Upload the photo using the userid 

	$Dir = 'photos/' . $user_id . '/';

	if(!is_dir($Dir)) {
	    mkdir($Dir, 0777,true);
	    chmod($Dir, 0777);
	}

	do {
	    $fileName = microtime();
		$fileName = preg_replace('/\s+/', '_', $fileName);
	} while (file_exists("$Dir". $fileName . '.jpg'));
	 
	$file_path = $Dir.basename($_FILES['uploaded_file']['name']);

	//add the photo to the database
	require_once '../db/create/create_photo.php';

	$photo_path = 'http://www.doc.ic.ac.uk/project/2013/271/g1327111/process/'. $file_path;
	create_photo($user_id,$photo_path);

//copy the file from the server to the vm. 	
	$sftp = new Net_SFTP($host);
	if (!$sftp->login($username, $password)) {
	    exit('Login Failed');
	}

	// puts an x-byte file named photo on the SFTP server,
	// where x is the size of photo.jpg
	$dest_photo = $fileName;
	$src_photo = "$Dir". $fileName . '.jpg';
	$sftp->put($dest_photo, $src_photo , NET_SFTP_LOCAL_FILE);
	unset($stfp);
	
	//run tesseract on it.
	$ssh = new Net_SSH2($host);
	if (!$ssh->login($username, $password)) {
	    exit('Login Failed');
	}
	$trans_text = 'out4';
	$cmd = 'tesseract ' . $dest_photo . ' ' . $trans_text . ' -psm 1';
	$ssh->exec($cmd);
	$cmd = 'cat ' . $trans_text . '.txt';
	$text =  $ssh->exec($cmd);
	echo $text;

	//recognise words from the dictionary and search. 
	//TODO

	// make searchs using different algorithms. 
	//TODO

unset($ssh);	

?>