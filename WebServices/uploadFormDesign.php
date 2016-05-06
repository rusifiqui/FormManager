<?php
$post = json_decode(file_get_contents("php://input"), true);
$form = $post['form'];
$user = $post['user'];
$pass = $post['pass'];

mysql_connect("localhost",$user,$pass);
mysql_select_db("forms_db");

$data = json_decode($form,true);
$name = $data['name'];
$formId;
$questionId;

// Query para insertar los datos del formulario
$sqlForm = "INSERT INTO FORM_DESIGN(CREATE_DATE, AUTHOR, FORM_NAME, FORM_DESCRIPTION, FORM_TRACKEABLE) VALUES ('" .$data['createDate'] 
	."', '" .$data['author'] ."', '" .$data['name'] ."', '" .$data['description'] ."', " .(int)$data['isTracked'] .")";

// Se insertan los datos del formulario	
$retval = mysql_query($sqlForm) or die(mysql_error());

// Se recupera el id del formulario que se acaba de insertar
$sqlAux = "SELECT ID_FORM_DESIGN FROM FORM_DESIGN WHERE FORM_NAME = '" .$data['name'] ."'";
$result = mysql_query($sqlAux);
if (mysql_num_rows($result)==1){
    $row = mysql_fetch_array($result);
	$formId = $row['ID_FORM_DESIGN'];
}else{
	$retval = false;
}


if($retval == true){
	// Se recuperan las preguntas
	$preguntas = $data['questions'];
	foreach($preguntas as $question){
		
		$sqlQuestion = "INSERT INTO QUESTION_DESIGN(CREATE_DATE, AUTHOR, ID_FORM_DESIGN, ID_QUESTION_TYPE, QUESTION, MANDATORY_ASNWER) VALUES ('" .$question['createDate'] 
			."', '" .$question['author'] ."', '" .$formId ."', '" .$question['questionType'] ."', '" .$question['question'] ."', " .(int)$question['mandatoryAnswer'] .")";
		// Se inserta la pregunta
		$retvalQuestion = mysql_query($sqlQuestion);
		$retval = $retval OR retvalQuestion;
		
		if($retval == true){
			// Se recupera el id de la pregunta que se acaba de insertar
			$sqlAux = "SELECT ID_QUESTION_DESIGN FROM QUESTION_DESIGN WHERE QUESTION = '" .$question['question'] ."' AND ID_FORM_DESIGN = " .$formId;
			$result = mysql_query($sqlAux);
			
			if (mysql_num_rows($result)==1){
				$row = mysql_fetch_array($result);
				$questionId = $row['ID_QUESTION_DESIGN'];
			}else{
				$retval = false;
			}
			
			//Se recuperan las respuestas, en el caso de haberlas
			$respuestas = $question['answers'];
			foreach($respuestas as $answer){
				$sqlAnswer = "INSERT INTO ANSWER_DESIGN(CREATE_DATE, AUTHOR, ID_QUESTION_DESIGN, ID_ANSWER_TYPE, ANSWER) VALUES ('" .$answer['createDate']
					."', '" .$answer['author'] ."', '" .$questionId ."', 1, '" .$answer['answer'] ."')";
				
				$retvalAnswer = mysql_query($sqlAnswer);
				$retval = $retval OR retvalQuestion;
				
			}
		}
	}
}
	
if($retval == FALSE){
	$arr = array(array('result'=>'KO', 'resultCode'=>1));
}else{
	$arr = array(array('result'=>'OK', 'resultCode'=>0));
}

print(json_encode($arr));

mysql_close();


?>