int Sen = 0; //Definimos la entrada en el pin A0
float Temp; //Aquí almacenaremos la temperatura

void setup()
{
  Serial.begin(9600);//Configuramos el puerto serial
}
void loop() 
{
  Temp = analogRead(Sen);//Leemos el valor desde el sensor
  Temp = ( 5 * Temp  * 100)/1023; //Convertimos el valor a temperatura
  
  Serial.print("Temperatura: ");
  Serial.println(Temp);//Enviamos el valor al puerto serial
  
  delay(5000);//Espera cinco segundos para repetir el proceso
}
