import java.util.ArrayList;
import processing.core.*;

class EImage implements Runnable {
	PImage extrude;
	byte[][] values;
	Fiddling parent;
	byte offset;

	EImage(Fiddling sce, String file, byte off) {
		parent = sce;
		offset = off;
		extrude = parent.loadImage(file);
		extrude.loadPixels();
		values = new byte[extrude.width][extrude.height];
		for (int y = 0; y < extrude.height; y++) {
			for (int x = 0; x < extrude.width; x++) {
				values[x][y] = (byte) (parent.brightness((int) extrude
						.get(x, y)));
			}
		}
	}

	void render() throws InterruptedException {
		float theta = parent.theta * offset;
		double delta = (double) parent.theta * 0.01;
		int r = parent.buildingRadius;
		for (int x = 0; x < extrude.width; x += 5) {
			for (int y = 0; y < extrude.height; y += 5) {
				System.out.println(parent.thetaDelta);
				if (values[x][y] < parent.threshHold) {
					int inverted = (int) (Fiddling.map((int) values[x][y], 0,
							parent.threshHold, 254, 150));
//					 parent.stroke(inverted);
					parent.noStroke();
					// parent.fill(inverted);
					r += values[x][y] * 2;
					int ymult = (int) Fiddling.map(parent.buildingRadius, 400,
							3000, 4, 18);
					try{
					drawCube(r * Fiddling.cos(theta), y * ymult
							- parent.buildingRadius, r * Fiddling.sin(theta),
							parent.blocksize, inverted-1);
					}
					catch(Exception e){
						System.out.println("shit");
					}
				}
				/*
				 * else{ stroke(values[x][y],values[x][y],values[x][y],.6);
				 * fill(values[x][y],values[x][y],values[x][y],.6); r +=
				 * values[x][y]; float px = r*cos(theta); float pz =
				 * r*sin(theta); vertex(px, y*4-1000, pz); }
				 */
			}
			theta += delta;
		}

	}
	
	void drawCube(float x, float y, float z, float r, int color) {
		parent.beginShape(parent.QUADS);
		System.out.println("parent: "+parent.frameRate+"\n color: "+color);
		// face 1
		parent.fill(color);
		parent.stroke(color);
		parent.vertex(x, y, z);
		parent.vertex(x, y - r, z);
		parent.vertex(x - r, y - r, z);
		parent.vertex(x - r, y, z);
		// face 2
		parent.fill(color);
		parent.vertex(x - r, y, z);
		parent.vertex(x - r, y, z - r);
		parent.vertex(x, y, z - r);
		parent.vertex(x, y, z);
		// face 3
		parent.fill(color);
		parent.vertex(x - r, y, z - r);
		parent.vertex(x - r, y - r, z - r);
		parent.vertex(x, y - r, z - r);
		parent.vertex(x, y, z - r);
		// face 4
		parent.fill(color);
		parent.vertex(x - r, y - r, z);
		parent.vertex(x - r, y - r, z - r);
		parent.vertex(x, y - r, z - r);
		parent.vertex(x, y - r, z);
		// face 5
		parent.fill(color);
		parent.vertex(x - r, y, z);
		parent.vertex(x - r, y, z - r);
		parent.vertex(x - r, y - r, z - r);
		parent.vertex(x - r, y - r, z);
		// face 6
		parent.fill(color);
		parent.vertex(x, y, z);
		parent.vertex(x, y, z - r);
		parent.vertex(x, y - r, z - r);
		parent.vertex(x, y - r, z);
		parent.endShape();
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			render();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class EImages extends ArrayList<EImage> {
	public ArrayList<Thread> threads;

	public EImages() {
		threads = new ArrayList<Thread>();
	}

	public void setThread(int i) {
		threads.add(new Thread(this.get(i)));
	}

	public void runThread(int i) {
		threads.get(i).start();
	}

	void render() throws InterruptedException {
		for (int i = 0; i < this.size(); i++) {
			this.get(i).run();
		}
	}
}

abstract class Cube {
	static void drawCube(float x, float y, float z, float r, int color,
			Fiddling parent) {
		parent.beginShape(parent.QUADS);
		System.out.println("parent: "+parent.frameRate+"\n color: "+color);
		// face 1
		parent.fill(color);
		parent.stroke(color);
		parent.vertex(x, y, z);
		parent.vertex(x, y - r, z);
		parent.vertex(x - r, y - r, z);
		parent.vertex(x - r, y, z);
		// face 2
		parent.fill(color);
		parent.vertex(x - r, y, z);
		parent.vertex(x - r, y, z - r);
		parent.vertex(x, y, z - r);
		parent.vertex(x, y, z);
		// face 3
		parent.fill(color);
		parent.vertex(x - r, y, z - r);
		parent.vertex(x - r, y - r, z - r);
		parent.vertex(x, y - r, z - r);
		parent.vertex(x, y, z - r);
		// face 4
		parent.fill(color);
		parent.vertex(x - r, y - r, z);
		parent.vertex(x - r, y - r, z - r);
		parent.vertex(x, y - r, z - r);
		parent.vertex(x, y - r, z);
		// face 5
		parent.fill(color);
		parent.vertex(x - r, y, z);
		parent.vertex(x - r, y, z - r);
		parent.vertex(x - r, y - r, z - r);
		parent.vertex(x - r, y - r, z);
		// face 6
		parent.fill(color);
		parent.vertex(x, y, z);
		parent.vertex(x, y, z - r);
		parent.vertex(x, y - r, z - r);
		parent.vertex(x, y - r, z);
		parent.endShape();
	}
}
