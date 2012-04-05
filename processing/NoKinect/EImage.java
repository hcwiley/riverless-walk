import java.util.ArrayList;
import processing.core.PImage;

class EImage {
  PImage extrude;
  int[] values;
  NoKinect parent;
  int[] position;
  int forDelta;

  EImage(NoKinect sce, String file) {
    parent = sce;
    position = new int[3];
    extrude = parent.loadImage(file);
    extrude.loadPixels();
    forDelta = 10;
    values = new int[extrude.width * extrude.height];
    for (int x = forDelta; x < extrude.width; x += forDelta) {
      for (int y = forDelta; y < extrude.height; y += forDelta) {
        values[(x * extrude.width) + y] = (int) (parent
          .brightness((int) extrude.get(x, y)));
        for (int d = 0; d < forDelta; d++) {
          values[(x * extrude.width) + y] += (int) (parent
            .brightness((int) extrude.get(x - d, y - d)));
        }
        values[(x * extrude.width) + y] /= forDelta;
      }
    }
  }

  void render(int offset, int total, int threshHold, int blockSpacing) {
    float theta = parent.theta * offset;// parent.map(offset, 0, total, 0,
    // 180);
    //         System.out.println(blockSpacing);
    float delta = (float) (parent.thetaDelta * 0.01);// parent.map(1, 0, 360, 0,
    // extrude.width);
    for (int x = forDelta; x < extrude.width; x += forDelta) {
      for (int y = forDelta; y < extrude.height; y += forDelta) {
        int r = parent.buildingRadius;
        if (values[(x * extrude.width) + y] < threshHold) {
          int inverted = (int) (NoKinect.map(
          (int) values[(x * extrude.width) + y], 0, 
          255, 255, 0));
          r += values[(x * extrude.width) + y] * forDelta;
          int ymult = (int) NoKinect.map(parent.buildingRadius, 400, 
          3000, 4, 18);
          position[0] = (int) (r * NoKinect.cos(theta));
          position[1] = y * ymult - parent.buildingRadius
            - extrude.height;
          position[2] = (int) (r * NoKinect.sin(theta));
          //                    parent.strokeWeight(NoKinect.map(blockSpacing, 0, 40, 0,6));
          //                    NoKinect.println(blockSpacing);
          parent.fill(inverted);
          parent.noStroke();
          //                  parent.stroke(inverted);
          Cube.drawCube(position[0], position[1], position[2], 
          parent.blocksize, inverted, parent, 0);
        }
        else {
          parent.noStroke();
        }
      }
      theta += delta;
    }
  }
}

class EImages extends ArrayList<EImage> {
  public void render(int threshHold, int blockSpacing) {
    byte index = -1;
    for (int i = 0; i < this.size(); i++) {
      this.get(i).render(i, this.size(), threshHold, blockSpacing);
    }
  }
}

abstract class Cube {
  static void drawCube(float x, float y, float z, float r, int color, 
  NoKinect parent, int blockSpacing) {
    //        parent.fill(color);
    parent.noStroke();
    parent.beginShape(NoKinect.QUADS);
    // face 1
    parent.vertex(x, y, z);
    parent.vertex(x, y - r, z);
    parent.vertex(x - r, y - r, z);
    parent.vertex(x - r, y, z);
    // face 2
    //        parent.fill(color);
    parent.vertex(x - r, y + blockSpacing, z);
    parent.vertex(x - r, y + blockSpacing, z - r);
    parent.vertex(x, y + blockSpacing, z - r);
    parent.vertex(x, y + blockSpacing, z);
    // face 3
    //        parent.fill(color);
    parent.vertex(x - r, y, z - r - blockSpacing);
    parent.vertex(x - r, y - r, z - r - blockSpacing);
    parent.vertex(x, y - r, z - r - blockSpacing);
    parent.vertex(x, y, z - r - blockSpacing);
    // face 4
    //        parent.fill(color);
    parent.vertex(x - r, y - r - blockSpacing, z);
    parent.vertex(x - r, y - r - blockSpacing, z - r);
    parent.vertex(x, y - r - blockSpacing, z - r);
    parent.vertex(x, y - r - blockSpacing, z);
    // face 5
    //        parent.fill(color);
    parent.vertex(x - r - blockSpacing, y, z);
    parent.vertex(x - r - blockSpacing, y, z - r);
    parent.vertex(x - r - blockSpacing, y - r, z - r);
    parent.vertex(x - r - blockSpacing, y - r, z);
    // face 6
    //        parent.fill(color);
    parent.vertex(x + blockSpacing, y, z);
    parent.vertex(x + blockSpacing, y, z - r);
    parent.vertex(x + blockSpacing, y - r, z - r);
    parent.vertex(x + blockSpacing, y - r, z);
    parent.endShape();
  }
}

