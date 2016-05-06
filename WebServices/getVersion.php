<?php
$post = json_decode(file_get_contents("php://input"), true);
$user = $post['user'];
$pass = $post['pass'];

mysql_connect("localhost",$user,$pass);
mysql_select_db("forms_db");
 
$q=mysql_query("SELECT VERSION_CODE AS VCODE, VERSION_NAME AS VNAME FROM APP_CONFIG WHERE VERSION_CODE = (SELECT MAX(VERSION_CODE) FROM APP_CONFIG)");
while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
 
mysql_close();
?>