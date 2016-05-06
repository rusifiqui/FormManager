<?php
$post = json_decode(file_get_contents("php://input"), true);
$formName = $post['formName'];
$user = $post['user'];
$pass = $post['pass'];

mysql_connect("localhost",$user,$pass);
mysql_select_db("forms_db");

$q=mysql_query("SELECT COUNT(*) AS COUNT FROM FORM_DESIGN WHERE FORM_NAME = '" .$formName ."'");

while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
 
mysql_close();
?>