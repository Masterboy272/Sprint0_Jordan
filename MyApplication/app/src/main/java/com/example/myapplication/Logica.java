package com.example.myapplication;

import android.util.Log;

public class Logica {
    private Object Medicion;

    public void guardarMedicion(Medidas medicion) {

        Log.d("test", "entra a guardar medicion");
        PeticionarioREST elPeticionario = new PeticionarioREST();


        String textoJSON = "{\"Medicion\":\"" + medicion.getMedicion() + "\", \"TipoSensor\":\"" + medicion.getTipoSensor() + "\", \"Latitud\":\"" + medicion.getLatitud() + "\", \"Longitud\":\"" + medicion.getLongitud() + "\"}";
        Log.d("JSON", textoJSON);
        elPeticionario.hacerPeticionREST("POST", "http://172.20.10.2/src/api/v1.0/index.php", textoJSON,
                new PeticionarioREST.RespuestaREST() {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d("test", "Se ha insertado correctamente");
                    }
                }
        );


    }
}
