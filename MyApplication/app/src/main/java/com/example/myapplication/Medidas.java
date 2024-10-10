package com.example.myapplication;

public class Medidas {
    private int medicion;
    private int tipoSensor;
    private double latitud;

    private double longitud;


    public int getMedicion() {
        return medicion;
    }

    public void setMedicion(int medicion) {
        this.medicion = medicion;
    }

    public Medidas(int medicion, int tipoSensor, double latitud, double longitud) {
        this.medicion = medicion;
        this.tipoSensor = tipoSensor;
        this.latitud = latitud;
        this.longitud = longitud;
    }



    public int getTipoSensor() {
        return tipoSensor;
    }

    public void setTipoSensor(int tipoSensor) {
        this.tipoSensor = tipoSensor;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


}
