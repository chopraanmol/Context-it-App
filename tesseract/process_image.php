<?php
set_include_path(get_include_path() . PATH_SEPARATOR . 'phpseclib');

include('Net/SFTP.php');

$username = 'guest';
$password = 'rackrack';
$host = 'cvm-g1327111';

//Upload the photo using the userid 
//TODO

//add the photo to the database
//TODO


//copy the file from the server to the vm. 
	
	$sftp = new Net_SFTP($host);
	if (!$sftp->login($username, $password)) {
	    exit('Login Failed');
	}

	// puts an x-byte file named photo on the SFTP server,
	// where x is the size of photo.jpg
	$dest_photo = 'photo.jpg';
	$src_photo = 'photo.jpg';
	$sftp->put($dest, $src , NET_SFTP_LOCAL_FILE);

//run tesseract on it.
	$ssh = new Net_SSH2($host);
	if (!$ssh->login($username, $password)) {
	    exit('Login Failed');
	}
	$src_text = 'out';
	$cmd = 'tesseract ' . $dest_photo . ' ' . $src_text . ' -psm 1';
	$ssh->exec($cmd);
	$cmd = 'cat ' . $src_text . '.txt';
	echo $ssh->exec($cmd);

//recognise words from the dictionary and search. 
//TODO

// make searchs using different algorithms. 
//TODO

unset($stfp);
unset($ssh);	

?>