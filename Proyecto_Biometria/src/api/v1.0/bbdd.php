<?php

include './conexion.php';
function bbddInsert($Medicion,$TipoSensor,$Latitud,$Longitud ){//parte en la que mandamos una sentecia sql a la base de datos

    $sql = "INSERT INTO `datossensor` (`Medicion`, `TipoSensor`,`Latitud`,  `Longitud`) VALUES ('$Medicion', '$TipoSensor','$Latitud','$Longitud')";//meter estos valores
    if (mysqli_query(Conectar(), $sql)) {

        return true;

    } else {
        return false;
    }
}

function buscarBBDD(){
    $sql = "SELECT * FROM `datossensor`";
    return mysqli_query(Conectar(),$sql);
}