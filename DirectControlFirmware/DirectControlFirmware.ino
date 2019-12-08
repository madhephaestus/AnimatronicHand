#include <WiiChuck.h>
#include <Wire.h>
#include <Servo.h>
// This works with the Guitar Hero World Tour (Wii) Drums

Accessory nunchuck;

int xCurrent = 98;
int yCurrent = 128;
#define xCenter  98
#define yCenter 128
int maxPerLoop = 5;
Servo pan;  // create servo object to control a servo
Servo tilt;  // create servo object to control a servo
Servo elbow;  // create servo object to control a servo



void setup() {

  Serial.begin(115200);
  Serial.println("Starting WiiChuck Generic Controller Demo");

  nunchuck.begin();
  pan.attach(0);  // attaches the servo on pin 9 to the servo object
  tilt.attach(1);  // attaches the servo on pin 9 to the servo object
  elbow.attach(2);  // attaches the servo on pin 9 to the servo object
  //nunchuck.addMap(new Nunchuck::joyX(0,255,98,0)); // Servo pin, max servo value, zero center value, min servo value 
 // nunchuck.addMap(new Nunchuck::joyY(1,255,128,0)); // Servo pin, max servo value, zero center value, min servo value   
  //nunchuck.addMap(new Nunchuck::joyY(2,255,128,0)); // Servo pin, max servo value, zero center value, min servo value                                                          
                                                       
  //nunchuck.addMap(new Nunchuck::buttonZ(2,200,128,10)); // Servo pin, max servo value, zero center value, min servo value 
  //nunchuck.addMap(new Nunchuck::buttonC(8,200,128,10)); // Servo pin, max servo value, zero center value, min servo value 

  //nunchuck.printMaps(Serial);  
}


void loop() {
   nunchuck.readData();   // Read inputs and update maps
  //nunchuck.printInputs(Serial); // Print all inputs
  int value;
  if(nunchuck.getJoyX()>=128)
    value= map(nunchuck.getJoyX(),255,128,180,xCenter);
   else
    value= map(nunchuck.getJoyX(),128,0,xCenter,0);
  int diff = (value-xCurrent);
  if(abs(diff)>maxPerLoop){
    if(diff>0)
      xCurrent+=maxPerLoop;
     else
      xCurrent-=maxPerLoop;

  }else{
    xCurrent=value;
  }

  int valueY;
   if(nunchuck.getJoyY()>=128)
    valueY=map(nunchuck.getJoyY(),255,128,180,yCenter);// nunchuck.getJoyY();
   else
    valueY=map(nunchuck.getJoyY(),128,0,yCenter,0);// nunchuck.getJoyY();
  
  int diffY = (valueY-yCurrent);
  if(abs(diffY)>maxPerLoop){
     if(diffY>0)
      yCurrent+=maxPerLoop;
     else
      yCurrent-=maxPerLoop;

  }else{
    yCurrent=valueY;
  }
  pan.write(xCurrent); 
  tilt.write(yCurrent); 
  elbow.write(yCurrent); 

  //nunchuck.printMaps(Serial);  
  delay(50);
  
  Serial.print(" Raw value x: ");
  Serial.print(xCurrent);
  Serial.print(" current: ");
  Serial.print(yCurrent);
  Serial.print("\r\n ");

}
