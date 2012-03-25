import java.awt.Color;
import java.util.ArrayList;
import processing.core.*;

class EImage {
    PImage extrude;
    int[][] values;
    Fiddling parent;

    EImage(Fiddling sce, String file) {
        parent = sce;
        extrude = parent.loadImage(file);
        extrude.loadPixels();
        values = new int[extrude.width][extrude.height];
        for (int y = 0; y < extrude.height; y++) {
            for (int x = 0; x < extrude.width; x++) {
                values[x][y] = (int) (parent
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
        int blocksize = parent.blocksize;
        int r0 = parent.buildingRadius;
        for (int x = 0; x < extrude.width; x += 2) {
            for (int y = 0; y < extrude.height; y += 2) {
                int r = r0;
                if (values[x][y] < threshHold) {
                    int inverted = (int) (parent.map((int) values[x][y], 0,
                            threshHold, 255, 0));
                    parent.stroke(inverted);
                    // noStroke();
                    // fill(inverted);
                    r += values[x][y] * 2;
                    int ymult = (int) parent.map(parent.buildingRadius, 400,
                            3000, 3, 20);
                    float px = r * parent.cos(theta);
                    float pz = r * parent.sin(theta);
                    // parent.beginShape(parent.QUAD_STRIP);
                    Cube.drawCube(px, y * ymult - r0, pz, blocksize, inverted,
                            parent);
                    // parent.vertex(px, y*ymult-r0-blocksize, pz);
                    // parent.vertex(px-blocksize, y*ymult-r0, pz-blocksize);
                    // parent.vertex(px-blocksize, y*ymult-r0-blocksize,
                    // pz-blocksize);
                    // parent.vertex(px, y*4-r0, pz);
                    // parent.vertex(px, y*4-r0+blocksize, pz);
                    // parent.vertex(px+blocksize, y*4-r0, pz+blocksize);
                    // parent.vertex(px+blocksize, y*4-r0+blocksize,
                    // pz+blocksize);
                    // parent.endShape();
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
        parent.beginShape(parent.QUADS);
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
