<?php
set_include_path(get_include_path() . PATH_SEPARATOR . 'phpseclib');
include '../db/create/create_photo.php';
include 'util.php';

$input = json_decode(file_get_contents('php://input'), true);

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
		$text = execute_tesseract($dest_photo);
		echo $text;
			
		 
	//Split the words into a 2-d array based on line number. 
		$array = preg_split("/\r\n|\n|\r/", $text); // Split the result by newline. 
		$recognized_words = array();
		for($i = 0; $i < count($array); $i++){
			$line = preg_split("/[\s,]+/", $array[$i]); // split each line into words. 
			foreach($line as $word){
				array_push($recognized_words[$i],$word); 					
			}
		}
 
	// make searchs using different algorithms. 
	$results = searchFaroo($array[0]); // search the first line. 
	// $results_bing = searchBing($array[0]);
	
}else{
	//ERROR
}


?>