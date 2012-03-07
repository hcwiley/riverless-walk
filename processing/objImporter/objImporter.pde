import SimpleOpenNI.*;
import processing.opengl.*;
import javax.media.opengl.*;
import saito.objloader.*;

OBJModel model ;

PVector rot, tran, modelTran;
GL gl;

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
