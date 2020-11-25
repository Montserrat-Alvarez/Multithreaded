const int Trigger = 2;   
const int Echo = 3;   

void setup()
  { 
    Serial.begin(9600);//Configuración de puerto serial
    pinMode(Trigger, OUTPUT); //Pin de salida
    pinMode(Echo, INPUT);  //Pin de entrada
  }
void loop()
  { 
  long t; //Tiempo para obtener distancia
  long d; //Distancia 

  digitalWrite(Trigger, HIGH);
  delayMicroseconds(10);   //Envio de pulso 
  digitalWrite(Trigger, LOW);
  
  t = pulseIn(Echo, HIGH); //Se obtiene el ancho del pulso
  d = t/59;             //Calculo de distancia
  
  Serial.print("Distancia: ");
  Serial.print(d);      //Envio de distancia a puerto serial
  Serial.print("cm");
  Serial.println();
  delay(5000);         //Espera cinco segundos para repetir el proceso
  }
  


