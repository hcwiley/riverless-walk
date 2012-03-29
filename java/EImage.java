import java.util.ArrayList;
import processing.core.*;

class EImage {
    PImage extrude;
    int[] values;
    Fiddling parent;
    int[] position;
    int forDelta;

    EImage(Fiddling sce, String file) {
        parent = sce;
        position = new int[3];
        extrude = parent.loadImage(file);
        extrude.loadPixels();
        forDelta = 10;
        values = new int[extrude.width * extrude.height];
        for (int x = forDelta; x < extrude.width; x += forDelta) {
            for (int y = forDelta; y < extrude.height; y += forDelta) {
                values[(x * extrude.width) + y] = (byte) (parent
                        .brightness((int) extrude.get(x, y)));
                for (int d = 0; d < forDelta; d++) {
                    values[(x * extrude.width) + y] += (byte) (parent
                            .brightness((int) extrude.get(x - d, y - d)));
                }
                values[(x * extrude.width) + y] /= forDelta;

            }
        }
    }

    boolean render(int offset, int total, int threshHold, int blockSpacing) {
        float theta = parent.theta * offset;// parent.map(offset, 0, total, 0,
                                            // 180);
//         System.out.println(blockSpacing);
        float delta = (float) (parent.thetaDelta * 0.01);// parent.map(1, 0, 360, 0,
                                                    // extrude.width);
        for (int x = forDelta; x < extrude.width - forDelta * 2; x += forDelta) {
            for (int y = forDelta; y < extrude.height - forDelta * 2; y += forDelta) {
                int r = parent.buildingRadius;
                if (values[(x * extrude.width) + y] < threshHold) {
                    int inverted = (int) (Fiddling.map(
                            (int) values[(x * extrude.width) + y], 0,
                            255, 255, 0));
                    // parent.stroke(inverted);
                    parent.noStroke();
                    // parent.fill(inverted);
                    r += values[(x * extrude.width) + y] * forDelta;
                    int ymult = (int) Fiddling.map(parent.buildingRadius, 400,
                            3000, 4, 18);
//                    if (x > extrude.width / 2 - forDelta - 2
//                            || x < extrude.width / 2 + forDelta + 2) {
////                        System.out.println("image at: " + position[0] + ", "
////                                + position[1] + ", " + position[2]);
//                    }
                    position[0] = (int) (r * Fiddling.cos(theta));
                    position[1] = y * ymult - parent.buildingRadius
                            - (300 - extrude.height);
                    position[2] = (int) (r * Fiddling.sin(theta));
//                    parent.strokeWeight(Fiddling.map(blockSpacing, 0, 20, 0,3));
//                    Fiddling.println(blockSpacing);
                    parent.stroke(Fiddling.map(blockSpacing, -15, 15, 0, 255));
                  parent.fill(inverted);
                    Cube.drawCube(position[0], -position[1], position[2],
                            parent.blocksize, inverted, parent, blockSpacing);
                }
                else{
                    parent.noStroke();
                }
            }
            theta += delta;
        }
        if (parent.cam.getLookAt()[0] == position[0]
                || parent.cam.getLookAt()[1] == position[1]
                || parent.cam.getLookAt()[2] == position[2])
            return true;
        else
            return false;

    }
}

class EImages extends ArrayList<EImage> {
    public byte render(int threshHold, int blockSpacing) {
        byte index = -1;
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).render(i, this.size(), threshHold, blockSpacing))
                index = (byte) i;
        }
        return index;
    }
}

abstract class Cube {
    static void drawCube(float x, float y, float z, float r, int color,
            Fiddling parent, int blockSpacing) {
        parent.fill(color);
        parent.noStroke();
        parent.smooth();
//        parent.stroke(color);
        parent.beginShape(Fiddling.QUADS);
        // face 1
        parent.vertex(x, y, z);
        parent.vertex(x, y - r, z);
        parent.vertex(x - r, y - r, z);
        parent.vertex(x - r, y, z);
        // face 2
        parent.fill(color);
        parent.vertex(x - r, y + blockSpacing, z);
        parent.vertex(x - r, y + blockSpacing, z - r);
        parent.vertex(x, y + blockSpacing, z - r);
        parent.vertex(x, y + blockSpacing, z);
        // face 3
        parent.fill(color);
        parent.vertex(x - r, y, z - r - blockSpacing);
        parent.vertex(x - r, y - r, z - r - blockSpacing);
        parent.vertex(x, y - r, z - r - blockSpacing);
        parent.vertex(x, y, z - r - blockSpacing);
        // face 4
        parent.fill(color);
        parent.vertex(x - r, y - r - blockSpacing, z);
        parent.vertex(x - r, y - r - blockSpacing, z - r);
        parent.vertex(x, y - r - blockSpacing, z - r);
        parent.vertex(x, y - r - blockSpacing, z);
        // face 5
        parent.fill(color);
        parent.vertex(x - r - blockSpacing, y, z);
        parent.vertex(x - r - blockSpacing, y, z - r);
        parent.vertex(x - r - blockSpacing, y - r, z - r);
        parent.vertex(x - r - blockSpacing, y - r, z);
        // face 6
        parent.fill(color);
        parent.vertex(x + blockSpacing, y, z);
        parent.vertex(x + blockSpacing, y, z - r);
        parent.vertex(x + blockSpacing, y - r, z - r);
        parent.vertex(x + blockSpacing, y - r, z);
        parent.endShape();
    }
}
