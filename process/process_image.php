<?php
set_include_path(get_include_path() . PATH_SEPARATOR . 'phpseclib');
include '../db/create/create_photo.php';
include 'util.php';

$input = $_POST;

if (isset($input['user_id'])){
	//upload image 
		$file_info = upload_image($user_id,$_FILES);
		$file_dir = 'http://www.doc.ic.ac.uk/project/2013/271/g1327111/process/'. $file_info[2];
	//add the photo to the database
		create_photo($user_id,$photo_path);

	//copy the file from the server to the vm. 	
		$dest_photo = $file_info[0] . $file_info[1];
		transfer_file_to_vm($file_info[2],$dest_photo);
		
	//run tesseract on it and return dictionary words.
		$text_array = execute_tesseract($dest_photo);
		echo $text_array[0]; // Without spellchecker
		echo $text_array[1] . '<br>'; // with spellchecker
			
 
    // Split both strings into 2-D array of words
    	$actual_text = getArrayFromString($text_array[0]);


	// make searchs using different algorithms.
	
	// search the actual text using bing 
	// $results = searchBing($text_array[0]);	
	// search the actual text using faroo 	
	$results = searchFaroo($text_array[0]); 
	// search the first line.
	$results = searchFaroo($actual_text[0]); 
	// search the last line.
	$results = searchFaroo($actual_text[count($actual_text) - 1]); 
	// search the dictionary line.	
	$results = searchFaroo($text_array[1]); 
	




	// Iterative deepening.	search for the longest string (gives bigger context).
	$results = searchFaroo($array[0]); 
 
	
	// $results_bing = searchBing($array[0]);
	
}else{
	//ERROR
}


?>
