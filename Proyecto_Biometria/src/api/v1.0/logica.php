<?php
include './bbdd.php';
function guardarMedida($Medicion,$TipoSensor,$Latitud, $Longitud){
  echo  bbddInsert($Medicion,$TipoSensor,$Latitud,$Longitud);
}

function obtenerMedida(){
    $medicionSelect=buscarBBDD();
    $resultado = array();
    $i = 0;
    //creamos lista JSON
    while ($fila = mysqli_fetch_array($medicionSelect)) {
        $respuesta = [];
        $respuesta["Medicion"] = $fila ["Medicion"];
        $respuesta["TipoSensor"] = $fila ["TipoSensor"];
        $respuesta["Latitud"] = $fila ["Latitud"];
        $respuesta["Longitud"] = $fila ["Longitud"];
        $resultado[$i] = $respuesta;
        $i++;
    }
    echo json_encode($resultado);
}

