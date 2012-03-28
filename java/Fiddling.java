/*
H. Cole Wiley cole@decode72.com
 LGPL (as far as I know everything I'm using is compitable with this license, so let's go with it.
 There's a lot of libraries involved here, as you can see from the imports.
 Particle cloud came from #openprocessing@http://openprocessing.org/sketch/6753
 #SimpleOpenNI@http://code.google.com/p/simple-openni/
 ill get the rest later
 */
import processing.core.*;
import processing.serial.*;
//import processing.opengl.*;
import SimpleOpenNI.*;
import java.util.Vector;
import javax.swing.*;
//import processing.
import peasy.*;
import toxi.geom.*;

public class Fiddling extends PApplet {

    public static void main(String args[]) {
        Fiddling theApplet = new Fiddling();
        theApplet.init(); // Needed if overridden in applet
        theApplet.start(); // Needed if overridden in applet

        // ... Create a window (JFrame) and make applet the content pane.
        JFrame window = new JFrame("riverless walk");
        window.setContentPane(theApplet);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack(); // Arrange the components.
        System.out.println(theApplet.getSize());
        window.setVisible(true); // Make the window visible
    }

    public Fiddling() {

    }

    // Particle Cloud stuff

    Vec3D globalOffset, avg, cameraCenter;
    public float neighborhood, viscosity, speed, turbulence, cameraRate,
            rebirthRadius, spread, independence, dofRatio;
    public int n, rebirth;
    public boolean averageRebirth, paused;
    Vector particles;
    Plane focalPlane;
    PeasyCam cam;
    Controls controls;
    float thetaDelta = (float) 1.64;
    float theta = (float) 1.64;
    int blocksize = 5;
    int buildingRadius = 1500;
    float falloff;

    PVector rot, tran, modelTran;
    int y0;
    // GL gl;
    // Kinect
    SimpleOpenNI kinect;
    // Minim mic;
    Serial serial;
    Viewers viewers;
    IntVector userList;
    PFont font;
    EImages eimages;
    int threshHold = 130;
    float scale = 1;
    int NUM_EIMAGES = 6;
    PVector center;

    boolean noLines = true;
    boolean isBlu = true;

    public void setup() {
        this.size(1400, 1240, OPENGL);
        frameRate(25);
        // gl = ((PGraphicsOpenGL) g).gl;
        // println(gl);
        // mm.setQueueSize(50, 100);
        cam = new PeasyCam(this, 1700);
        cam.setMinimumDistance(300);
        cam.setMaximumDistance(6000);
        cam.rotateY(30);

        controls = new Controls(this);
        controls.setParameters();
        controls.makeControls();

        cameraCenter = new Vec3D();
        avg = new Vec3D();
        globalOffset = new Vec3D(0, 1.f / 5, 2.f / 3);

        particles = new Vector();
         n = 300;
         for (int i = 0; i < n; i++)
         particles.add(new Particle(this));

        noStroke();
        rot = new PVector();// new PVector((float) 2.4699998, (float) 6.4400015,
                            // (float) 0.0);
        y0 = -400;
        tran = new PVector(0, y0, 0);
        falloff = 0;

        // kinect and viewer stuff, as they are related
        kinect = new SimpleOpenNI(this);
        kinect.enableDepth();
        kinect.enableRGB();
        kinect.setMirror(true);
        kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
        viewers = new Viewers();
        userList = new IntVector();
        // Getting my font ready
        // font = createFont("Helvetica", 44);
        // textFont(font);
        // getting all my extrudeimages setup
        eimages = new EImages();
        for (int i = 0; i < NUM_EIMAGES; i++) {
            eimages.add(new EImage(this, "img" + i + ".jpg"));
        }
        if (!isBlu) {
            serial = new Serial(this, Serial.list()[0], 9600);
            while (serial.available() < 1) {
            }
            println(serial.readString());
        }

    }

    int cloudTimer = millis();

    public void draw() {
        PImage curImage = kinect.rgbImage();
        //
        // sphere((float) (buildingRadius * 1.7));
        rotateX(rot.y);
        cam.rotateY(rot.x);
        rot.x = 0;
        rotateZ(rot.z);
        // image(kinect.depthImage(), 0, 0);
        kinect.update();
        userList.clear();
        kinect.getUsers(userList);
        if (userList.size() > 0) {
            background(19);
             lightFalloff(1,falloff,0);
             ambientLight(255, 255, 255, 0, 2*buildingRadius, 0);
//            lightSpecular(255, 255, 255);
            // ambientLight(255, 255, 255, 0, -2*buildingRadius, 0);
//            specular(falloff);
            // shininess(falloff);
            fill(66);
            noStroke();
            translate(tran.x, tran.y, tran.z);
            // sphere(2 * buildingRadius);
//            fill(93);
//            beginShape(QUADS);
//            // texture(curImage);
//            vertex(-10 * buildingRadius, 1500, -10 * buildingRadius);
//            vertex(-10 * buildingRadius, 1500, 10 * buildingRadius);
//            vertex(10 * buildingRadius, 1500, 10 * buildingRadius);
//            vertex(10 * buildingRadius, 1500, -10 * buildingRadius);
//            endShape();
            if (!noLines) {
                stroke(255, 0, 0);
                line(-5000, 0, 0, 5000, 0, 0);
                stroke(0, 255, 0);
                line(0, -5000, 0, 0, 5000, 0);
                stroke(0, 0, 255);
                line(0, 0, -5000, 0, 0, 5000);
            }
            center = new PVector();
            for (int i = 0; i < userList.size(); i++) {
                int userId = userList.get(i);
                PVector position = new PVector();
                kinect.getCoM(userId, position);
                center.x += position.x;
                center.y += position.y;
                center.z += position.z;
                if (viewers.viewer(userId) != null) {
                    viewers.viewer(userId).updatePosition(position);
                } else {
                    System.out.println("adding new viewer");
                    viewers.add(new Viewer(position, userId, this));
                }
            }
            center.x /= userList.size();
            center.y /= userList.size();
            center.z /= userList.size();
            byte iout = (byte) map(center.x, -700, 700, -15, 15);
            track(iout);
            sendSerial(iout);
            // text("z: "+center.z, 700, 10,
            // 600, 200);
            blocksize = (int) map(center.z, 800, 2600, 100, 5);
            buildingRadius = (int) map(userList.size(), 1, 6, 1500, 6000);
            tran.y = (int) map(center.y, 0, -180, y0 - 200, y0 + 200);

            eimages.render(threshHold, viewers.speed());
        } else {
            // viewers = new Viewers();
            // viewers.clear();
            sendSerial((byte) 0);
            kinect.update();
            // buildingRadius = 1500;
            // blocksize = 10;
            // tran.y = y0;
            // println("cleared viewers");
        }
//        float[] rotations = cam.getRotations();
//        rotateX(rotations[0]);
//        rotateY(rotations[1]);
//        rotateZ(rotations[2]);
////        cloudDraw();
//        // if (millis() - cloudTimer > 0) {
//        // cloudTimer = millis();
//        fill(255);
//        stroke(255);
//        float[] pos = cam.getPosition();
//        text("cam looking here: " + pos[0]+", "+pos[1]+", "+pos[2], 100, 100, 300, 300);
        // }
        // image(curImage, 0, 0);
    }

    public void track(byte iout) {
        if (isBlu) {
            if (abs(iout) > 2) {
                // rot.x -= iout * .001;
            }
            return;
        }
        if (abs(iout) > 2) {
            rot.x -= iout * .05;
        }
    }

    public void sendSerial(byte out) {
        if (serial != null && !isBlu) {
            if (abs(out) > 2) {
                if (out < 0) {
                    out = (byte) abs(out);
                    out += 20;
                }
                serial.write(out);
                // println(out);
            } else {
                serial.write((byte) 0);
                // println("in the middle");
            }
        }
    }

    public Particle randomParticle() {
        return ((Particle) particles.get((int) random(particles.size())));
    }

    public void cloudDraw() {
        avg = new Vec3D();
        for (int i = 0; i < particles.size(); i++) {
            Particle cur = ((Particle) particles.get(i));
            avg.addSelf(cur.position);
        }
        avg.scaleSelf(1.f / particles.size());

        cameraCenter.scaleSelf(1 - cameraRate);
        cameraCenter.addSelf(avg.scale(cameraRate));

        // translate(-cameraCenter.x, -cameraCenter.y, -cameraCenter.z);

        float[] camPosition = cam.getPosition();
        focalPlane = new Plane(avg, new Vec3D(camPosition[0], camPosition[1],
                camPosition[2]));

        // background(0);
        noFill();
        hint(DISABLE_DEPTH_TEST);
        for (int i = 0; i < particles.size(); i++) {
            Particle cur = ((Particle) particles.get(i));
            if (!paused)
                cur.update();
            cur.draw();
        }

        for (int i = 0; i < rebirth; i++)
            randomParticle().resetPosition();

        if (particles.size() > n)
            particles.setSize(n);
        while (particles.size() < n)
            particles.add(new Particle(this));
        // original
//        globalOffset.addSelf(turbulence / neighborhood, turbulence
//                / neighborhood, turbulence / neighborhood);
        // int r = 1800;
        // for(int theta=0; theta < 360; theta+=10) {
        // globalOffset.addSelf(r*cos(theta), 400,r*sin(theta));
        // }
        float[] pos = cam.getPosition();
         globalOffset.addSelf(pos[0], pos[1],pos[2]);
    }

    public void keyPressed() {
        if (key == 'p') {
            println("rot: " + rot.x + ", " + rot.y + ", " + rot.z);
            println("tran: " + tran.x + ", " + tran.y + ", " + tran.z);
        } else if (key == 'r')
            tran.x += 15;
        else if (key == 'f')
            tran.x -= 15;
        else if (key == 'e')
            tran.z += 15;
        else if (key == 't')
            tran.z -= 15;
        else if (key == 'y')
            y0 += 15;
        else if (key == 'h')
            y0 -= 15;
        else if (key == '+')
            threshHold += 10;
        else if (key == '=')
            threshHold += 1;
        else if (key == '_')
            threshHold -= 10;
        else if (key == '-')
            threshHold -= 1;
        else if (key == 'l')
            this.scale((float) (scale * .75));
        else if (key == 'b')
            this.scale((float) (scale * 1.25));
        else if (keyCode == LEFT)
            rot.x += .1;
        else if (keyCode == RIGHT)
            rot.x -= .1;
        else if (keyCode == UP)
            tran.z += 10;
        else if (keyCode == DOWN)
            tran.z -= 10;
        else if (key == 'n')
            noLines = !noLines;
    }

    public void mouseDragged() {
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
}