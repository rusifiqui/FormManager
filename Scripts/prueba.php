<?php
mysql_connect("localhost","root","rusifarules");
mysql_select_db("Forms_db");
 
$q=mysql_query("SELECT * FROM people");
while($e=mysql_fetch_assoc($q))
        $output[]=$e;
 
print(json_encode($output));
 
mysql_close();
?>