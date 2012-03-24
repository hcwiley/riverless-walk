=import processing.serial.*;
import processing.opengl.*;
import javax.media.opengl.*;
import saito.objloader.*;
import SimpleOpenNI.*;

OBJModel model ;

PVector rot, tran, modelTran;
GL gl;
//Kinect Tracker
UserTracker user;
SimpleOpenNI kinect;
Serial serial;
void setup()
{
    size(1280, 1080, OPENGL);
    frameRate(30);
    model = new OBJModel(this, "walking-out-01.obj","absolute", QUADS);
    model.enableDebug();

    model.scale(10);
    
    gl = ((PGraphicsOpenGL)g).gl;

    stroke(255);
    noStroke();
    modelTran = new PVector();
    rot = new PVector(4, 6.22, 0);
    tran = new PVector(-640, 740, -280);
    model.translate(modelTran);
    addMouseWheelListener(new java.awt.event.MouseWheelListener() { 
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) { 
      mouseWheel(evt.getWheelRotation());
    }});
    kinect = new SimpleOpenNI(this);
    user = new UserTracker(kinect);
    println(Serial.list());
//    serial = new Serial(this, Serial.list()[0], 9600);
//    while(serial.available() < 1){
//    }
//    println(serial.readString());
}


void draw()
{
    background(129);
    lights();
    pushMatrix();
    translate(tran.x, tran.y, tran.z);
    rotateX(rot.y);
    rotateY(rot.x);
    rotateZ(rot.z);

    model.draw();

    popMatrix();
    image(user.drawUser(),600, 600);
    // draw the skeleton if it's available
    if(user.context.isTrackingSkeleton(user.curUser)){
      user.drawSkeleton(user.curUser);
      PVector dir = user.trackUser();
//      print(dir.x / 100+", ");
      byte out = byte(dir.x / 100);
      if(abs(out) > 2){
        if(out < 0){
          out = byte(abs(out));
          out += 20;
        }
//        serial.write(out);
        rot.x += dir.x / 10000;
//        println(out);
      }
      else{
//        serial.write(byte(0));
        println("in the middle");
      }
      float deltaZ = dir.z - 2100;
      deltaZ /= -10;
      if(abs(deltaZ) > 20){
        tran.z += deltaZ;
        println(deltaZ);
      }
    }
}

boolean bTexture = true;
boolean bStroke = false;

void keyPressed()
{
    if(key == 't') {
        if(!bTexture) {
            model.enableTexture();
            bTexture = true;
        } 
        else {
            model.disableTexture();
            bTexture = false;
        }
    }

    if(key == 's') {
        if(!bStroke) {
            stroke(255);
            bStroke = true;
        } 
        else {
            noStroke();
            bStroke = false;
        }
    }

    else if(key=='1')
        model.shapeMode(POINTS);
    else if(key=='2')
        model.shapeMode(LINES);
    else if(key=='3')
        model.shapeMode(TRIANGLES);
    else if(key == 'p'){
      println("rx: "+rot.x+", ry: "+rot.y+", rz: "+rot.z+", tx: "+tran.x+", ty: "+tran.y+", tz: "+tran.z);
    }
}

void mouseDragged()
{
  if(keyPressed){
    if(keyCode == ALT){
      tran.x += (mouseX - pmouseX) * 10;
      tran.y += (mouseY - pmouseY) * 10;
    }
  }
  else{
    rot.x += (mouseX - pmouseX) * 0.01;
    rot.y -= (mouseY - pmouseY) * 0.01;
  }
}
void mouseWheel(int delta){
 tran.z -= delta * 10;
}

void onNewUser(int userId){
 user.onNewUser(userId); 
}

void onLostUser(int userId)
{
  user.onLostUser(userId);
}

void onStartCalibration(int userId)
{
  user.onStartCalibration(userId);
}

void onEndCalibration(int userId, boolean successfull)
{
  user.onEndCalibration(userId, successfull);
}

void onStartPose(String pose,int userId)
{
  user.onStartPose(pose, userId);
}

void onEndPose(String pose,int userId)
{
  user.onEndPose(pose, userId);
}