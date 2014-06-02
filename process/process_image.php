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
		//$text_array[0]; // Without spellchecker
		//$text_array[1]; // With spellchecker
			
 
    // Split both strings into 2-D array of words
    	$actual_text = getArrayFromString($text_array[0]);

		$urlInfo = array();
	// make searchs using different algorithms.
	
	// search the actual text using bing 
	// $results = searchBing($text_array[0]);	
	// search the actual text using faroo 	
		//searchFaroo($text_array[0]); 
	// search the first line.
		//searchFaroo($actual_text[0]); 
	// search the last line.
		//searchFaroo($actual_text[count($actual_text) - 1]); 
	// search the dictionary line.	
		//searchFaroo($text_array[1]); 
	$urlInfo = array_merge($urlInfo, searchFaroo($text_array[0]), searchFaroo($actual_text[0]), 
			searchFaroo($actual_text[count($actual_text) - 1], searchFaroo($text_array[1]);

	$response = array();
	$response['status'] = 1;
	$response['url_set'] = $url_info; 
	echo json_encode($urlInfo);
	//TODO UIAGVKIAGBVKJASBVJASBVF
	// Iterative deepening.	search for the longest string (gives bigger context).
		//$results = searchFaroo($array[0]); 
 
	
	// $results_bing = searchBing($array[0]);
	
}else{
	//ERROR
}


?>