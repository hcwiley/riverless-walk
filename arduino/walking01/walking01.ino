int pwm_a = 3;  //PWM control for motor outputs 1 and 2 is on digital pin 3
int pwm_b = 11;  //PWM control for motor outputs 3 and 4 is on digital pin 11
int dir_a = 12;  //dir control for motor outputs 1 and 2 is on digital pin 12
int dir_b = 13;  //dir control for motor outputs 3 and 4 is on digital pin 13
int dir;
int powIn;
int total;
void setup()
{
  Serial.begin(9600);
  pinMode(pwm_a, OUTPUT);  //Set control pins to be outputs
  pinMode(pwm_b, OUTPUT);
  pinMode(dir_a, OUTPUT);
  pinMode(dir_b, OUTPUT);
  
  dir = 1;
  powIn = 1;
  total = 0;

  analogWrite(pwm_a, 100);        
  //set both motors to run at (100/255 = 39)% duty cycle (slow)  
  analogWrite(pwm_b, 100);
  Serial.print("hi");
}

void loop()
{
  if(Serial.available() > 0){
      byte data = Serial.read();
      if (data > 20){
        dir = 5;
        data -= 20;
      }
      else if (data < 10 && data > 0){
        dir = 0;
      }
      else{
        dir = 1;
        powIn = 0;
        data = 0;
      }
      powIn = map(abs(data),2, 7, 30, 140);
  }
  if( dir == 5 ){
    digitalWrite(dir_a, HIGH);  //Set motor direction, 1 low, 2 high
    digitalWrite(dir_b, HIGH);  //Set motor direction, 3 high, 4 low
    analogWrite(pwm_a, powIn);
    analogWrite(pwm_b, powIn);
  }
  else if( dir == 0 ){
    digitalWrite(dir_a, LOW);  //Set motor direction, 1 low, 2 high
    digitalWrite(dir_b, LOW);  //Set motor direction, 3 high, 4 low
    analogWrite(pwm_a, powIn);
    analogWrite(pwm_b, powIn);
  }
  else{
    analogWrite(pwm_a, 0);      
    //set both motors to run at 0% duty cycle (off)
    analogWrite(pwm_b, 0);
  }
//  }
}
