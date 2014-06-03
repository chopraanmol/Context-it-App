#!/usr/bin/php
<?php
set_include_path(get_include_path() . PATH_SEPARATOR . 'phpseclib');
include '../db/create/create_photo.php';
include 'util.php';

$input = $_POST;

if (isset($input['user_id'])){
	//upload image 
	if(!empty($_FILES)){
		$file_info = upload_image($user_id);
		$file_dir = 'http://www.doc.ic.ac.uk/project/2013/271/g1327111/process/'. $file_info[2];
		//add the photo to the database
			if(create_photo($user_id,$file_dir) != 1){
					//copy the file from the server to the vm. 	
				$dest_photo = $file_info[0] .'.'. $file_info[1];					
					//run tesseract on it and return dictionary words.
					if(transfer_file_to_vm($file_info[2],$dest_photo)){
						$text_array = execute_tesseract($dest_photo);
            if(!is_array($text_array)){
              //$text_array[0] // Without spellchecker
              //$text_array[1] // With spellchecker
                 
              // Split into 2-D array of words
              $actual_text = getArrayFromString($text_array[0]);

              $urlInfo = array();
              // make searchs using different algorithms.
            
              // search the actual text using bing using searchBing($text_array[0]);  
              // search the actual text using faroo using searchFaroo($text_array[0]); 
              // search the first line using searchFaroo($actual_text[0]); 
              // search the last line using searchFaroo($actual_text[count($actual_text) - 1]); 
              // search the dictionary line using searchFaroo($text_array[1]); 
              $urlInfo = array_merge($urlInfo, searchFaroo($text_array[0]), 
                                      searchFaroo($actual_text[0]), 
                                      searchFaroo($actual_text[count($actual_text) - 1]), 
                                      searchFaroo($text_array[1]));
              
              // Iterative deepening. search for the longest string (gives bigger context).
              $results = searchFaroo($array[0]); 
             
            
              // $results_bing = searchBing($array[0]); 

              $response = array();
              $response['status'] = 1;
              $response['url_set'] = $url_info; 
              echo json_encode($response);
                    
            }else{
              //TESSERACT GAVE AN ERROR.
              $response['status'] = 6;
              echo json_encode($response); 
            }
					  
					}else{
            //FILE NOT TRANSFERRED
              $response['status'] = 5;
              echo json_encode($response); 
          }
					
			}else{
				//Photo not added to the database.
        $response['status'] = 4;
        echo json_encode($response); 
			}

		
	}else{
		//File not uploaded successfully.
    $response['status'] = 7;
    echo json_encode($response); 
	}
		
	
}else{
	//user_id not provided by the user.
  $response['status'] = 3;
  echo json_encode($response);
}

?>
