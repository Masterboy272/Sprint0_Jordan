<?php
function Conectar(){

    //conexion al servidor local

    $server = "localhost";
    $user = "root";
    $password = "";
    $dbName = "biometria";
    $mysql=new mysqli("localhost","root","","biometria");

    $conn = mysqli_connect($server, $user, $password, $dbName);
    //vemos si la conexion falla

    if (!$conn) {
        http_response_code(500);
        die("Error: " . mysqli_connect_error());
    }
    else{
        //"conectado";
    }
    
    return $conn;
}

