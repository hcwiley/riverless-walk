int pwm_a = 3;  //PWM control for motor outputs 1 and 2 is on digital pin 3
int pwm_b = 11;  //PWM control for motor outputs 3 and 4 is on digital pin 11
int dir_a = 12;  //dir control for motor outputs 1 and 2 is on digital pin 12
int dir_b = 13;  //dir control for motor outputs 3 and 4 is on digital pin 13
byte dir = 0;
void setup()
{
  Serial.begin(19200);
  pinMode(pwm_a, OUTPUT);  //Set control pins to be outputs
  pinMode(pwm_b, OUTPUT);
  pinMode(dir_a, OUTPUT);
  pinMode(dir_b, OUTPUT);

  analogWrite(pwm_a, 100);        
  //set both motors to run at (100/255 = 39)% duty cycle (slow)  
  analogWrite(pwm_b, 100);
  Serial.println("hi");
}

void loop()
{
//  if(Serial.available() > 0){
    dir = Serial.read();
//  }
    if( dir == 53 ){
      digitalWrite(dir_a, HIGH);  //Set motor direction, 1 low, 2 high
      digitalWrite(dir_b, HIGH);  //Set motor direction, 3 high, 4 low
      analogWrite(pwm_a, 255);
      analogWrite(pwm_b, 255);
    }
    else if( dir == 48 ){
      digitalWrite(dir_a, LOW);  //Set motor direction, 1 low, 2 high
      digitalWrite(dir_b, LOW);  //Set motor direction, 3 high, 4 low
      analogWrite(pwm_a, 255);
      analogWrite(pwm_b, 255);
    }
    else{
      analogWrite(pwm_a, 0);      
      //set both motors to run at 0% duty cycle (off)
      analogWrite(pwm_b, 0);
    }
//  }
}
