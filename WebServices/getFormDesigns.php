<?php
$post = json_decode(file_get_contents("php://input"), true);
$user = $post['user'];
$pass = $post['pass'];

mysql_connect("localhost",$user,$pass);
mysql_select_db("forms_db");

$q=mysql_query("SELECT ID_FORM_DESIGN ID, AUTHOR, FORM_NAME NAME, FORM_DESCRIPTION DESCRIPTION FROM FORM_DESIGN");
while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
 
mysql_close();
?>