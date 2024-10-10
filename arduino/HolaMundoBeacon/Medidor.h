// -*- mode: c++ -*-

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO
int sensorData [11];

// ------------------------------------------------------
// ------------------------------------------------------
//_____________-pins______________
int Pgas = 5;
int Pref = 28;
int Ptemp = 29;

//-----------------------analog---------

int Vgas_value = 0;
int Vref_value = 0;
int Vtmp_value = 0;


float M = 499 * pow(10, -6);
float sensitivityCode = -41.26;// valor desconocido 
float resultado = sensitivityCode * M;

//------------------------------------------------------
double medida_temp(double Vtemp) {
  
  double temp=Vtemp*3.3/4096;
  return (87 * temp) - 18;
}


//--------------------------------
void Serial1InParser()
{
    int i = 0;
    for (int i =0; i<11; i++) {
       //while(!Serial1.available()) { }
       sensorData[i] = Serial.parseInt();
    }
}

class Medidor {

  // .....................................................
  // .....................................................
private:

public:

  // .....................................................
  // constructor
  // .....................................................
  Medidor(  ) {
  } // ()

  // .....................................................
  // .....................................................
  void iniciarMedidor() {
    //Serial1InParser();
	// las cosas que no se puedan hacer en el constructor, if any
  } // ()

  // .....................................................
  // .....................................................
  int medirCO2() {
	Vgas_value =analogRead(Pgas);// Analogggggggggggggggg
    Vref_value = analogRead(Pref);// Analogggggggggggggggg
    float concentracion = (1/resultado) * (Vgas_value - Vref_value) * (3.3/4096);
    Serial.print("concentracion");
    Serial.print(concentracion);
    return concentracion;
    //return medida_temp(analogRead(Ptemp));
  } // ()

  // .....................................................
  // .....................................................
  int medirTemperatura() {
    //return sensorData [2] ;
    //return medida_temp(analogRead(sensorData [2]));
	  return medida_temp(analogRead(Ptemp));
  } // ()
	
}; // class

// ------------------------------------------------------
// ------------------------------------------------------
// ------------------------------------------------------
// ------------------------------------------------------
#endif
