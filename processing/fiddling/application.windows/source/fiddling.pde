/*
H. Cole Wiley cole@decode72.com
 LGPL (as far as I know everything I'm using is compitable with this license, so let's go with it.
 There's a lot of libraries involved here, as you can see from the imports.
 Particle cloud came from #openprocessing@http://openprocessing.org/sketch/6753
 #SimpleOpenNI@http://code.google.com/p/simple-openni/
 ill get the rest later
 */

import codeanticode.gsvideo.*;
//import processing.video.*;
import processing.serial.*;
import processing.opengl.*;
import javax.media.opengl.*;
//import saito.objloader.*;
import SimpleOpenNI.*;

import peasy.*; 
import toxi.geom.*; 
import controlP5.*; 

//Particle Cloud stuff

Vec3D globalOffset, avg, cameraCenter;
public float neighborhood, viscosity, speed, turbulence, cameraRate, rebirthRadius, spread, independence, dofRatio;
public int n, rebirth;
public boolean averageRebirth, paused;
Vector particles;
Plane focalPlane;
PeasyCam cam;

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
int threshHold = 100;
float scale = 1.0;
int NUM_EIMAGES = 4;

//MovieMaker
GSMovieMaker mm;
boolean recording = false;



void setup()
{
  //  mm = new MovieMaker(this, width, height, "riverless-walk.mov", MovieMaker.MEDIUM);
  size(1440, 1300, OPENGL);
  frameRate(26);
  mm = new GSMovieMaker(this, width, height, "drawing.ogg", GSMovieMaker.DIRAC, GSMovieMaker.MEDIUM, int(frameRate));
  mm.setQueueSize(0, 100);
  mm.start();
  gl = ((PGraphicsOpenGL)g).gl;
  println(gl);
  //  mm.setQueueSize(50, 100);
  cam = new PeasyCam(this, 2000);
  cam.setMinimumDistance(50);
  cam.setMaximumDistance(1500);

  setParameters();
  makeControls();

  cameraCenter = new Vec3D();
  avg = new Vec3D();
  globalOffset = new Vec3D(0, 1.f / 3, 2.f / 3);

  particles = new Vector();
  for(int i = 0; i < n; i++)
    particles.add(new Particle());

  noStroke();
  modelTran = new PVector(0, 0, 0);
  rot = new PVector(2.4699998, 6.4400015, 0.0);
  tran = new PVector();//width/4, height/4, 0);
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
  for(int i = 0; i < NUM_EIMAGES; i++) {
    eimages.add(new EImage("img"+i+".jpg"));
  }
}


void draw()
{
  //  kinect.update();
  //  PImage curImage = kinect.depthImage();
  background(29);
  lights();
  fill(113);
  noStroke();
  translate(tran.x, tran.y, tran.z);
  if(!recording) {
    stroke(255,0,0);
    line(-5000,0,0,5000,0,0);
    stroke(0,255,0);
    line(0,-5000,0,0,5000,0);
    stroke(0,0,255);
    line(0,0,-5000,0,0,5000);
  }
  //  sphere(1200);
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
  cloudDraw();
  if(recording) {
    loadPixels();
    // Add window's pixels to movie
    mm.addFrame(pixels);
    //    mm.addFrame();
  }
}

Particle randomParticle() {
  return ((Particle) particles.get((int) random(particles.size())));
}

void cloudDraw() {  
  avg = new Vec3D();
  for(int i = 0; i < particles.size(); i++) {
    Particle cur = ((Particle) particles.get(i));
    avg.addSelf(cur.position);
  }
  avg.scaleSelf(1.f / particles.size());

  cameraCenter.scaleSelf(1 - cameraRate);
  cameraCenter.addSelf(avg.scale(cameraRate));

  translate(-cameraCenter.x, -cameraCenter.y, -cameraCenter.z);

  float[] camPosition = cam.getPosition();
  focalPlane = new Plane(avg, new Vec3D(camPosition[0], camPosition[1], camPosition[2]));

  //  background(0);
  noFill();
  hint(DISABLE_DEPTH_TEST);
  for(int i = 0; i < particles.size(); i++) {
    Particle cur = ((Particle) particles.get(i));
    if(!paused)
      cur.update();
    cur.draw();
  }

  for(int i = 0; i < rebirth; i++)
    randomParticle().resetPosition();

  if(particles.size() > n)
    particles.setSize(n);
  while(particles.size() < n)
    particles.add(new Particle());
  //original
  globalOffset.addSelf(
  turbulence / neighborhood,
  turbulence / neighborhood,
  turbulence / neighborhood);
  //  int r = 1800;
  //  for(int theta=0; theta < 360; theta+=10) {
  //    globalOffset.addSelf(r*cos(theta), 400,r*sin(theta));
  //  }
  //    globalOffset.addSelf(1500,600, 1500);
}

//
//boolean bTexture = true;
//boolean bStroke = true;

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
  else if (key == 'l') scale(scale*.75);
  else if (key == 'b') scale(scale*1.25);
  else if (keyCode == LEFT) rot.x += .1;
  else if (keyCode == RIGHT) rot.x -= .1;
  else if (key == 'R') {
    recording = !recording;
    if(recording) {
      mm.start();
      println("now recording... better make it look pretty");
    }
    else {
      try{
        Thread.sleep(100);
      }catch(Exception e){
        println("whoops");
      }
      mm.finish();
      println("done recording");
    }
  }
}

void mouseDragged()
{
  if (keyPressed) {
    if (keyCode == ALT) {
      tran.x += (mouseX - pmouseX) * 2;
      tran.y += (mouseY - pmouseY) * 2;
      return;
    }
  }
}
void mouseWheel(int delta) {
  tran.z -= delta * 10;
}

