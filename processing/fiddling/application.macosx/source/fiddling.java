import processing.core.*; 
import processing.xml.*; 

import codeanticode.gsvideo.*; 
import processing.serial.*; 
import processing.opengl.*; 
import javax.media.opengl.*; 
import SimpleOpenNI.*; 
import peasy.*; 
import toxi.geom.*; 
import controlP5.*; 
import controlP5.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class fiddling extends PApplet {

/*
H. Cole Wiley cole@decode72.com
 LGPL (as far as I know everything I'm using is compitable with this license, so let's go with it.
 There's a lot of libraries involved here, as you can see from the imports.
 Particle cloud came from #openprocessing@http://openprocessing.org/sketch/6753
 #SimpleOpenNI@http://code.google.com/p/simple-openni/
 ill get the rest later
 */


//import processing.video.*;



//import saito.objloader.*;


 
 
 

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
int INDICATOR;
Viewers viewers;
IntVector userList;
PFont font;
EImages eimages;
int threshHold = 100;
float scale = 1.0f;
int NUM_EIMAGES = 4;

//MovieMaker
GSMovieMaker mm;
boolean recording = false;



public void setup()
{
  //  mm = new MovieMaker(this, width, height, "riverless-walk.mov", MovieMaker.MEDIUM);
  size(2000, 1600, OPENGL);
  frameRate(24);
  mm = new GSMovieMaker(this, width, height, "drawing.ogg");//, GSMovieMaker.X264, GSMovieMaker.LOW, int(frameRate));
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
  rot = new PVector(2.4699998f, 6.4400015f, 0.0f);
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


public void draw()
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

public Particle randomParticle() {
  return ((Particle) particles.get((int) random(particles.size())));
}

public void cloudDraw() {  
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

public void keyPressed()
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
  else if (key == 'l') scale(scale*.75f);
  else if (key == 'b') scale(scale*1.25f);
  else if (keyCode == LEFT) rot.x += .1f;
  else if (keyCode == RIGHT) rot.x -= .1f;
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

public void mouseDragged()
{
  if (keyPressed) {
    if (keyCode == ALT) {
      tran.x += (mouseX - pmouseX) * 2;
      tran.y += (mouseY - pmouseY) * 2;
      return;
    }
  }
}
public void mouseWheel(int delta) {
  tran.z -= delta * 10;
}


public ControlP5 control;
public ControlWindow w;
public void setParameters() {
  n = 10000;
  dofRatio = 50;
  neighborhood = 700;
  speed = 24;
  viscosity = .1f;
  spread = 100;
  independence = .15f;
  rebirth = 0;
  rebirthRadius = 250;
  turbulence = 1.3f;
  cameraRate = .1f;
  averageRebirth = false;
}

public void makeControls() {
  control = new ControlP5(this);
  
  w = control.addControlWindow("controlWindow", 10, 10, 350, 140);
  w.hideCoordinates();
  w.setTitle("Flocking Parameters");
  
  int y = 0;
  control.addSlider("n", 1, 20000, n, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("dofRatio", 1, 200, dofRatio, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("neighborhood", 1, width * 2, neighborhood, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("speed", 0, 100, speed, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("viscosity", 0, 1, viscosity, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("spread", 50, 200, spread, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("independence", 0, 1, independence, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("rebirth", 0, 100, rebirth, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("rebirthRadius", 1, width, rebirthRadius, 10, y += 10, 256, 9).setWindow(w);
  control.addSlider("turbulence", 0, 4, turbulence, 10, y += 10, 256, 9).setWindow(w);
  control.addToggle("paused", false, 10, y += 11, 9, 9).setWindow(w);
  control.setAutoInitialization(true);
}

class EImage {
  PImage extrude;
  int[][] values;
  EImage(String file) {
    extrude = loadImage(file);
    extrude.loadPixels();
    values = new int[extrude.width][extrude.height];
    for (int y = 0; y < extrude.height; y++) {
      for (int x = 0; x < extrude.width; x++) {
        int pixel = extrude.get(x, y);
        values[x][y] = PApplet.parseInt(brightness(pixel));
      }
    }
  }

  public void render(PApplet scene, int offset, int total) {
    float theta = map(offset, 0, total-1, 0, 360);
    float delta = .006f;//map(1, 0, 360, 0, extrude.width);
    int blocksize = 100;
    int r0 = 1100;
    beginShape(QUAD_STRIP);
    for (int x = 0; x < extrude.width; x+=2) {
      for (int y = 0; y < extrude.height; y+=2) {
        int r = r0;
        if (values[x][y] < threshHold) {
          int inverted = PApplet.parseInt(map(values[x][y],0,threshHold,255,0));
//          stroke(inverted);
          noStroke();
          fill(inverted);
          r += values[x][y];
          float px = r*cos(theta);
          float pz = r*sin(theta);
          vertex(px, y*4-r0, pz);
          vertex(px, y*4-r0-blocksize, pz);
          vertex(px-blocksize, y*4-r0-blocksize, pz-blocksize);
          vertex(px-blocksize, y*4-r0, pz-blocksize);
        }
        /*
        else{
          stroke(values[x][y],values[x][y],values[x][y],.6);
          fill(values[x][y],values[x][y],values[x][y],.6);
          r += values[x][y];
          float px = r*cos(theta);
          float pz = r*sin(theta);
          vertex(px, y*4-1000, pz);
        }
        */
      }
      theta+= delta;
    }
    endShape();
  }
}

class EImages extends ArrayList<EImage> {
  public void render(PApplet scene) {
    for (int i = 0; i < this.size(); i++) {
      this.get(i).render(scene,i, this.size());
    }
  }
}

Vec3D centeringForce = new Vec3D();

class Particle {
  Vec3D position, velocity, force;
  Vec3D localOffset;
  Particle() {
    resetPosition();
    velocity = new Vec3D();
    force = new Vec3D();
    localOffset = Vec3D.randomVector();
  }
  public void resetPosition() {
    position = Vec3D.randomVector();
    position.scaleSelf(random(rebirthRadius));
    if(particles.size() == 0)
      position.addSelf(avg);
    else
      position.addSelf(randomParticle().position);
  }

  public void draw() {
    float distanceToFocalPlane = focalPlane.getDistanceToPoint(position);
    distanceToFocalPlane *= 1 / dofRatio;
    distanceToFocalPlane = constrain(distanceToFocalPlane, 1, 15);
    strokeWeight(distanceToFocalPlane);
    stroke(255, constrain(255 / (distanceToFocalPlane * distanceToFocalPlane), 1, 255));
    point(position.x, position.y, position.z);
  }
  public void applyFlockingForce() {
    force.addSelf(
    noise(
    position.x / neighborhood + globalOffset.x + localOffset.x * independence,
    position.y / neighborhood,
    position.z / neighborhood)
      - .5f,
    noise(
    position.x / neighborhood,
    position.y / neighborhood + globalOffset.y  + localOffset.y * independence,
    position.z / neighborhood)
      - .5f,
    noise(
    position.x / neighborhood,
    position.y / neighborhood,
    position.z / neighborhood + globalOffset.z + localOffset.z * independence)
      - .5f);
  }
  public void applyViscosityForce() {
    force.addSelf(velocity.scale(-viscosity));
  }
  public void applyCenteringForce() {
    centeringForce.set(position);
    centeringForce.subSelf(avg);
    float distanceToCenter = centeringForce.magnitude();
    centeringForce.normalize();
    centeringForce.scaleSelf(-distanceToCenter / (spread * spread));
    force.addSelf(centeringForce);
  }
  public void update() {
    force.clear();
    applyFlockingForce();
    applyViscosityForce();
    applyCenteringForce();
    velocity.addSelf(force); // mass = 1
    position.addSelf(velocity.scale(speed));
  }
}

class Viewer {
  PVector position;
  int id;
  int lastMillis;
  int curMillis;
  float speed;

  Viewer(PVector pos, int ID) {
    position = pos;
    id = ID;
    lastMillis = curMillis = millis();
  }
  
  public void updatePosition(PVector pos){
    curMillis = millis();
    speed = (pos.x - position.x)/(curMillis - lastMillis);
    position = pos;
    lastMillis = curMillis;
  }
}

class Viewers extends ArrayList<Viewer> {
  Viewers() {
  }
  public Viewer viewer(int ID) {
    for (int i=0; i<this.size(); i++) {
      if (this.get(i).id == ID)
        return this.get(i);
    }
    return null;
  }
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "fiddling" });
  }
}
