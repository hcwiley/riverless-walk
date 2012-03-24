class EImage {
  PImage extrude;
  int[][] values;
  EImage(String file) {
    extrude = loadImage(file);
    extrude.loadPixels();
    values = new int[extrude.width][extrude.height];
    for (int y = 0; y < extrude.height; y++) {
      for (int x = 0; x < extrude.width; x++) {
        color pixel = extrude.get(x, y);
        values[x][y] = int(brightness(pixel));
      }
    }
  }

  void render(PApplet scene, int offset, int total) {
    float theta = map(offset, 0, 2, 0, 360);
    float delta = .007;//map(1, 0, 360, 0, extrude.width);
    beginShape(TRIANGLES);
    for (int x = 0; x < extrude.width; x++) {
      for (int y = 0; y < extrude.height; y++) {
        int r = 500;
        if (values[x][y] < threshHold) {
          stroke(values[x][y]);
          fill(values[x][y]);
          r += values[x][y];
          float px = r*cos(theta);
          float pz = r*sin(theta);
          vertex(px, y*3-1000, pz);
        }
      }
      theta+= delta;
    }
    endShape();
  }
}

class EImages extends ArrayList<EImage> {
  void render(PApplet scene) {
    for (int i = 0; i < this.size(); i++) {
      this.get(i).render(scene,i, this.size());
    }
  }
}

