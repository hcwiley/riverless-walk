import superCAD.*;

import processing.opengl.*;

import processing.dxf.*;

PImage extrude;
int[][] values;
float angle = 0;
int mx =0 , my =0 , mz =0 , dx, dy, dz, l, tx = 300, ty = 100;
float s;
float x1 = 50, x2 = 550, y1 = 300, y2 = 600;
float lc, rc;
boolean record = false;
int threshHold = 240;
void setup() {
  size(1400, 1440, OPENGL);
  
  // Load the image into a new array
  s = .25;
  frameRate(26);
  // load a file, give the AudioPlayer buffers that are 2048 samples long
  PrintWriter file;
  file = createWriter("output.dat");
  extrude = loadImage("img3.jpg");
  extrude.loadPixels();
  values = new int[extrude.width][extrude.height];
  for (int y = 0; y < extrude.height; y++) {
    for (int x = 0; x < extrude.width; x++) {
      color pixel = extrude.get(x, y);
      values[x][y] = int(brightness(pixel));
      file.println(x+", "+x+","+values[x][y]);
    }
  }
  scale(.5);
  file.close();
  print("done with setup\n");
  
}

void draw(){
  if (record) {
    println("writing obj");
    beginRaw("superCAD.ObjFile", "output.obj");
  }
  background(0);
  scale(s);
  translate(tx, ty, 0);
  rotateX(map(mx, 0, width, 0, PI));
  rotateY(map(my, 0, width, 0, PI));
  rotateZ(map(mz, 0, height, 0, -PI));
  for (int x = 0; x < extrude.width -3; x++) {
    if(x%30==0){
      for (int y = 0; y < extrude.height - 3; y++) {
        if(y%10==0){
          if(values[x][y] > threshHold){
            beginShape(QUAD_STRIP);
            stroke(values[x][y]);
            fill(values[x][y]);
            vertex(x+ 100, y+ 100, values[x][++y]+ 100);
            vertex(x, y, values[++x][++y]+ 100);
            vertex(x+ 100, y+ 100, values[++x][++y]+ 100);
            vertex(x, y, values[++x][++y]+ 100);
            endShape();
          }
        }
      }
    }
  }
  if (record) {
    endRaw();
    record = false;
  }
}

void keyPressed()
{
  if(key == 'd'){
    println("pressed d, bout to write");
    record = true;
  }
  if(keyCode == CONTROL){
        mx = mouseX;//(x - mouseX)+ x;
        my = mouseY;//(y - mouseY)+ y;
  }
  else if(keyCode == ALT){
    tx = mouseX;
    ty = mouseY;
  }
  else if(keyCode == UP){
    print(key+"\n");
    ty -= 15;
  }
  else if(keyCode == DOWN){
    print(key+"\n");
    ty += 15;
  }
  else if(keyCode == LEFT){
    print(key+"\n");
    tx -= 15;
  }
  else if(keyCode == RIGHT){
    print(key+"\n");
    tx += 15;
  }
  else if (key == 'b' || key == 'B'){
    s = s*1.25;
  }
  else if(key=='l' || key == 'L'){
    s = s*.50;
  }
  else if ( key == 's' ) super.stop();
  else if ( key == '1' ){
    mx =0 ; my =0 ; mz =0 ; tx = 50; ty = 50; s = 1.75;
    println("Preset 1\nmx= "+mx +"; my= "+ my +"; mz= "+mz+"; dx= "+dx +"; dy= "+dy+"; dz= "+dz+"; tx= "+tx +"; ty= "+ty);
  }
  else if ( key == '2' ){
    mx = 450; my =0 ;  mz =490 ; tx = 404; ty = 607;
    println("Preset 1\nmx= "+mx +"; my= "+ my +"; mz= "+mz+"; dx= "+dx +"; dy= "+dy+"; dz= "+dz+"; tx= "+tx +"; ty= "+ty);
  }
  else if ( key == '3' ){
    mx= 787; my= 752; mz= 91; tx= 884; ty= 292;
    println("Preset 2\nmx= "+mx +"; my= "+ my +"; mz= "+mz+"; dx= "+dx +"; dy= "+dy+"; dz= "+dz+"; tx= "+tx +"; ty= "+ty);
  }
  else if ( key == '4' ){
    mx= 193; my= 172; mz= 899; dx= 0; dy= 0; dz= 0; tx= 908; ty= 659;
    println("Preset 2\nmx= "+mx +"; my= "+ my +"; mz= "+mz+"; dx= "+dx +"; dy= "+dy+"; dz= "+dz+"; tx= "+tx +"; ty= "+ty);
  }

  else if ( key == 'q' ) println("mx= "+mx +"; my= "+ my +"; mz= "+mz+"; dx= "+dx +"; dy= "+dy+"; dz= "+dz+"; tx= "+tx +"; ty= "+ty);
  else if ( key == 'r' ) mx+=15;
  else if ( key == 'f' ) mx-=15;
  else if ( key == 'e' ) mz+=15;
  else if ( key == 't' ) mz-=15;
  else if ( key == 'y' ) my+=15;
  else if ( key == 'h' ) my-=15;
  else if ( key == '+' ) threshHold += 10;
  else if ( key == '=' ) threshHold += 1;
  else if ( key == '_' ) threshHold -= 10;
  else if ( key == '-' ) threshHold -= 1;
}
void mouseDragged()
{
  if(keyPressed){
    if(keyCode == ALT){
      tx += (mouseX - pmouseX) * 10;
      ty += (mouseY - pmouseY) * 10;
    }
  }
  else{
    my -= (mouseX - pmouseX);
    mx += (mouseY - pmouseY);
  }
}

