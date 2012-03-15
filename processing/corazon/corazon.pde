import processing.serial.*;
import processing.opengl.*;
import javax.media.opengl.*;
import saito.objloader.*;
import SimpleOpenNI.*;

OBJModel model ;

PVector rot, tran, modelTran;
GL gl;
//Kinect
SimpleOpenNI kinect;

Serial serial;
color INDICATOR;
void setup()
{
  size(1024, 768, OPENGL);
  frameRate(26);
  model = new OBJModel(this, "walking-out-01.obj", "absolute", QUADS);
  model.enableDebug();

  model.scale(10);

  gl = ((PGraphicsOpenGL)g).gl;

  INDICATOR = color(0, 0, 255);

  stroke(255);
  noStroke();
  modelTran = new PVector(0, 0,0);
  rot = new PVector(4, 6.22, 0);
  tran = new PVector();
  model.translate(modelTran);
  addMouseWheelListener(new java.awt.event.MouseWheelListener() { 
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) { 
      mouseWheel(evt.getWheelRotation());
    }
  }
  );
  kinect = new SimpleOpenNI(this);
  kinect.enableDepth();
  kinect.setMirror(true);
  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
  println(Serial.list());
  serial = new Serial(this, Serial.list()[0], 9600);
  while (serial.available () < 1) {
  }
  println(serial.readString());
}


void draw()
{
  kinect.update();
//  PImage curImage = kinect.depthImage();
  background(129);
  lights();
  pushMatrix();
  translate(tran.x, tran.y, tran.z);
  rotateX(rot.y);
  rotateY(rot.x);
  rotateZ(rot.z);

  model.draw();

  popMatrix();
  IntVector userList = new IntVector();
  kinect.getUsers(userList);
  PVector dir = new PVector();
  if (userList.size() > 0) {
    for (int i=0; i<userList.size(); i++) { 
      int userId = userList.get(i);
      PVector position = new PVector(); 
      kinect.getCoM(userId, position); 
      dir.x += position.x;
      dir.y += position.y;
      dir.z += position.z;
//      fill(255, 0, 0);
//      kinect.convertRealWorldToProjective(position, position);
//      ellipse(position.x, position.y, 25,25);
    }
    dir.x /= userList.size();
    dir.y /= userList.size();
    dir.z /= userList.size();
    float iout = map(dir.x, -700, 700, -15, 15);
    byte out = byte(iout);
    if (abs(out) > 1) {
      if (out < 0) {
        out = byte(abs(out));
        out += 20;
      }
      serial.write(out);
      rot.x -= dir.x / 20000;
//      println(out);
    }
    else {
      serial.write(byte(0));
//      println("in the middle");
    }
    float deltaZ = dir.z - 1500;
    deltaZ /= -10;
    if (abs(deltaZ) > 20) {
//      tran.z += deltaZ;
      //        println(deltaZ);
    }
  }
  else{
    serial.write(byte(0));
  }
//  image(kinect.depthImage, 0, 0);
}

boolean bTexture = true;
boolean bStroke = false;

void keyPressed()
{
  if (key == 't') {
    if (!bTexture) {
      model.enableTexture();
      bTexture = true;
    } 
    else {
      model.disableTexture();
      bTexture = false;
    }
  }

  if (key == 's') {
    if (!bStroke) {
      stroke(255);
      bStroke = true;
    } 
    else {
      noStroke();
      bStroke = false;
    }
  }

  else if (key=='1')
    model.shapeMode(POINTS);
  else if (key=='2')
    model.shapeMode(LINES);
  else if (key=='3')
    model.shapeMode(TRIANGLES);
  else if (key == 'p') {
    println("rx: "+rot.x+", ry: "+rot.y+", rz: "+rot.z+", tx: "+tran.x+", ty: "+tran.y+", tz: "+tran.z);
  }
}

void mouseDragged()
{
  if (keyPressed) {
    if (keyCode == ALT) {
      tran.x += (mouseX - pmouseX) * 10;
      tran.y += (mouseY - pmouseY) * 10;
    }
  }
  else {
    rot.x += (mouseX - pmouseX) * 0.01;
    rot.y -= (mouseY - pmouseY) * 0.01;
  }
}
void mouseWheel(int delta) {
  tran.z -= delta * 10;
}

