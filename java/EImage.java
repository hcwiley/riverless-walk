import java.util.ArrayList;
import processing.core.*;

class EImage {
	PImage extrude;
	byte[][] values;
	Fiddling parent;

	EImage(Fiddling sce, String file) {
		parent = sce;
		extrude = parent.loadImage(file);
		extrude.loadPixels();
		values = new byte[extrude.width][extrude.height];
		for (int y = 0; y < extrude.height; y++) {
			for (int x = 0; x < extrude.width; x++) {
				values[x][y] = (byte) (parent
						.brightness((int) extrude.get(x, y)));
			}
		}
	}

	void render(int offset, int total, int threshHold) {
		float theta = parent.theta * offset;// parent.map(offset, 0, total, 0,
											// 180);
		// System.out.println(parent.thetaDelta);
		double delta = (double) parent.theta * 0.01;// parent.map(1, 0, 360, 0,
													// extrude.width);
		for (int x = 0; x < extrude.width; x += 5) {
			for (int y = 0; y < extrude.height; y += 5) {
				int r = parent.buildingRadius;
				if (values[x][y] < threshHold) {
					int inverted = (int) (Fiddling.map((int) values[x][y], 0,
							threshHold, 255, 150));
					// parent.stroke(inverted);
					parent.noStroke();
					// parent.fill(inverted);
					r += values[x][y] * 2;
					int ymult = (int) Fiddling.map(parent.buildingRadius, 400,
							3000, 4, 18);
					Cube.drawCube(r * Fiddling.cos(theta), y * ymult
							- parent.buildingRadius, r * Fiddling.sin(theta),
							parent.blocksize, inverted, parent);
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
}

class EImages extends ArrayList<EImage> {
	void render(int threshHold) {
		for (int i = 0; i < this.size(); i++) {
			this.get(i).render(i, this.size(), threshHold);
		}
	}
}

abstract class Cube {
	static void drawCube(float x, float y, float z, float r, int color,
			Fiddling parent) {
		parent.beginShape(Fiddling.QUADS);
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
