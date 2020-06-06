<?php  
 $connect = mysqli_connect("localhost", "root", "", "google");
 if(isset($_POST["query"]))  
 {  
      $output = '';  
      $query = "SELECT texto FROM queries WHERE texto LIKE '%".$_POST["query"]."%'";
      $result = mysqli_query($connect, $query); 
      $output = '<ul>';  
      if(mysqli_num_rows($result) > 0)  
      {  
           while($row = mysqli_fetch_array($result))  
           {  
                $output .= '<li>'.$row["texto"].'</li>';  
           }  
      }  
      else  
      {  
           $output .= '<li>Not Found</li>';  
      }  
      $output .= '</ul>';   
 }  
 ?> 