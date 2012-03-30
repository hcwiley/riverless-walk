/*
H. Cole Wiley cole@decode72.com
 LGPL (as far as I know everything I'm using is compitable with this license, so let's go with it.
 There's a lot of libraries involved here, as you can see from the imports.
 Particle cloud came from #openprocessing@http://openprocessing.org/sketch/6753
 #SimpleOpenNI@http://code.google.com/p/simple-openni/
 ill get the rest later
 */
//import processing.core.*;
import processing.core.*;
import processing.serial.*;
//import processing.opengl.*;
import SimpleOpenNI.*;
import java.util.Vector;
import javax.swing.*;
import peasy.*;
import toxi.geom.*;

public class Fiddling extends PApplet {

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
	// Controls controls;
	float thetaDelta = (float) 1.64;
	float theta = (float) 1.64;
	int blocksize = 10;
	int buildingRadius = 1200;
	float camD0;
	float camDMax;
	float camDMin;

	PVector rot, tran, modelTran;
	int y0;
	// Kinect
	SimpleOpenNI kinect;
	// Minim mic;
	Serial serial;
	Viewers viewers;
	IntVector userList;
	EImages eimages;
	int threshHold = 130;
	float scale = 1;
	int NUM_EIMAGES = 6;
	PVector center;

	boolean noLines = true;
	boolean isBlu = false;

	public void setup() {
		this.size(1024, 768, P3D);

		frameRate(25);

		if (NUM_EIMAGES == 6) {
			thetaDelta = (float) 3.45;
			theta = (float) 5.27;
		}
		// println(gl);
		camD0 = 1400;
		camDMax = 7000;
		camDMin = 0;
		cam = new PeasyCam(this, camD0);
		cam.setDistance(camD0);
		cam.setMinimumDistance(camDMin);
		cam.setMaximumDistance(camDMax);
		cameraCenter = new Vec3D();
		avg = new Vec3D();
		globalOffset = new Vec3D(0,0,0);//1.f / 5, 2.f / 3);

		rot = new PVector();
		y0 = 100;
		tran = new PVector(0, y0, 0);

		// kinect and viewer stuff, as they are related
		kinect = new SimpleOpenNI(this);
		kinect.enableDepth();
		kinect.setMirror(true);
		kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE);
		viewers = new Viewers();
		userList = new IntVector();

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
		dofRatio = 30;
		neighborhood = 300;
		speed = 24;
		viscosity = (float) .6;
		spread = 240;
		independence = (float) .35;
		rebirth = 100;
		rebirthRadius = 100;
		turbulence = (float) 2.4;
		cameraRate = (float) .4;
		averageRebirth = true;
		particles = new Vector();
		n = 5000;
		for (int i = 0; i < n; i++)
			particles.add(new Particle(this));

	}

	public void draw() {
		// rotateX(rot.y);
		cam.rotateY(rot.x);
		rot.x = 0;
		// rotateZ(rot.z);
		kinect.update();
		translate(0,tran.y,0);
		userList.clear();
		kinect.getUsers(userList);
		if (userList.size() > 0) {
			background(19);
			// lightFalloff(1, (float) 0.1, 0);
			// ambientLight(255, 255, 255, 0, 2*buildingRadius, 0);
			// lightSpecular(255, 255, 255);
			lights();
			// specular(falloff);
			fill(66);
			noStroke();
			shininess(255);
			// beginShape(QUADS);
			// vertex(-2*buildingRadius, y0, -2*buildingRadius);
			// vertex(-2*buildingRadius, y0, 2*buildingRadius);
			// vertex(2*buildingRadius, y0, 2*buildingRadius);
			// vertex(2*buildingRadius, y0, -2*buildingRadius);
			// endShape();
			// shininess(0);
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

			blocksize = (int) map(center.z, 1000, 4500, 200, 20);
			cam.setDistance((double) map(center.z + 300, 1000, 4300, camDMax,
					camDMin));
			buildingRadius = (int) map(userList.size(), 1, 6, 1100, 5000);
			// thetaDelta = (int) map(userList.size(), 1, 6, (float) 3.5, 10);
			tran.y = (int) map(center.y, 0, -180, y0 - 100, y0 + 100);
			eimages.render(threshHold, (int)(viewers.speed()*1.5));
		} else {
			// viewers = new Viewers();
			viewers.clear();
			sendSerial((byte) 0);
		}
//		float[] rotations = cam.getRotations();
//		rotateX(rotations[0]);
//		rotateY(rotations[1]);
//		rotateZ(rotations[2]);
		cloudDraw();
		sendSerial((byte) 0);
	}

	public void track(byte iout) {
		if (isBlu) {
			if (abs(iout) > 2) {
				// rot.x -= iout * .001;
			}
			return;
		}
		if (abs(iout) > 2) {
			rot.x += iout * .001;
		}
	}

	public void sendSerial(byte out) {
		if (serial != null && !isBlu) {
			if (abs(out) > 1) {
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
		globalOffset.addSelf(turbulence / neighborhood, turbulence
				/ neighborhood, turbulence / neighborhood);
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

        public PImage loadImage(String img){
          return super.loadImage(img);
        }
}
