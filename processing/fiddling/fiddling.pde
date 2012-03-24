import processing.serial.*;
import processing.opengl.*;
import javax.media.opengl.*;
import saito.objloader.*;
import SimpleOpenNI.*;

OBJModel model;
OBJModel model2;

PVector rot, tran, modelTran;
GL gl;
//Kinect
SimpleOpenNI kinect;

Serial serial;
color INDICATOR;
Viewers viewers;
IntVector userList;
PFont font;
EImages eimages;
int threshHold = 200;
void setup()
{
  size(1440, 900, OPENGL);
  frameRate(26);
  gl = ((PGraphicsOpenGL)g).gl;

  INDICATOR = color(0, 0, 255);

  stroke(255);
  noStroke();
  modelTran = new PVector(0, 0, 0);
  rot = new PVector(2.4699998, 6.4400015, 0.0);
  tran = new PVector(width/2, height/2, 0);
  addMouseWheelListener(new java.awt.event.MouseWheelListener() { 
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) { 
      mouseWheel(evt.getWheelRotation());
    }
  }
  );
//  kinect = new SimpleOpenNI(this);
//  kinect.enableDepth();
//  kinect.setMirror(true);
//  kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
  viewers = new Viewers();
//  userList = new IntVector();

  font = createFont("Helvetica", 44);
  textFont(font);
  eimages = new EImages();
  for(int i = 0; i < 2; i++){
   eimages.add(new EImage("img"+i+".jpg")); 
  }
}


void draw()
{
//  kinect.update();
  //  PImage curImage = kinect.depthImage();
  background(129);
  lights();
  translate(tran.x, tran.y, tran.z);
  rotateX(rot.y);
  rotateY(rot.x);
  rotateZ(rot.z);
/*
  image(kinect.depthImage(), 0, 0);
  userList.clear();
  kinect.getUsers(userList);
  if (userList.size() > 0) {
    text("viewers: "+userList.size(), 600, 210, 600, 200); 
    for (int i=0; i<userList.size(); i++) {
      int userId = userList.get(i);
      PVector position = new PVector(); 
      kinect.getCoM(userId, position);
      if (viewers.viewer(userId) != null) {
        viewers.viewer(userId).updatePosition(position);
        text("speed: "+viewers.viewer(userId).speed, 700, 10, 600, 200);
      }
      else {
        println("adding new viewer");
        viewers.add(new Viewer(position, userId));
      }
    }
  }
  else {  
    //    viewers = new Viewers();
    viewers.clear();
  }
  */
  eimages.render(this);
  stroke(255,0,0);
  line(-5000,0,0,5000,0,0);
  stroke(0,255,0);
  line(0,-5000,0,0,5000,0);
  stroke(0,0,255);
  line(0,0,-5000,0,0,5000);
}

boolean bTexture = true;
boolean bStroke = false;

void keyPressed()
{
  if (key == 'p') {
    println("rot: "+rot.x+", "+rot.y+", "+rot.z);
    println("tran: "+tran.x+", "+tran.y+", "+tran.z);
  }
  else if ( key == 'r' ) tran.x+=15;
  else if ( key == 'f' ) tran.x-=15;
  else if ( key == 'e' ) tran.z+=15;
  else if ( key == 't' ) tran.z-=15;
  else if ( key == 'y' ) tran.y+=15;
  else if ( key == 'h' ) tran.y-=15;
  else if ( key == '+' ) threshHold += 10;
  else if ( key == '=' ) threshHold += 1;
  else if ( key == '_' ) threshHold -= 10;
  else if ( key == '-' ) threshHold -= 1;
}

void mouseDragged()
{
  if (keyPressed) {
    if (keyCode == ALT) {
      tran.x += (mouseX - pmouseX) * 2;
      tran.y += (mouseY - pmouseY) * 2;
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

