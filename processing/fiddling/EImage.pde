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

  void render(int offset, int total) {
    float theta = map(offset, 0, total, 0, 360);
    float delta = .006;//map(1, 0, 360, 0, extrude.width);
    int blocksize = 5;
    int r0 = 1200;
    
    for (int x = 0; x < extrude.width; x+=2) {
      for (int y = 0; y < extrude.height; y+=2) {
        int r = r0;
        if (values[x][y] < threshHold) {
          int inverted = int(map(values[x][y],0,threshHold,255,0));
          stroke(inverted);
//          noStroke();
//          fill(inverted);
          r += values[x][y];
          float px = r*cos(theta);
          float pz = r*sin(theta);
          beginShape(QUAD_STRIP);
          vertex(px, y*4-r0, pz);
          vertex(px, y*4-r0-blocksize, pz);
          vertex(px-blocksize, y*4-r0, pz-blocksize);
          vertex(px-blocksize, y*4-r0-blocksize, pz-blocksize);
          endShape();
//          vertex(px, y*4-r0, pz);
//          vertex(px, y*4-r0+blocksize, pz);
//          vertex(px+blocksize, y*4-r0, pz+blocksize);
//          vertex(px+blocksize, y*4-r0+blocksize, pz+blocksize);
        }
        /*
        else{
          stroke(values[x][y],values[x][y],values[x][y],.6);
          fill(values[x][y],values[x][y],values[x][y],.6);
          r += values[x][y];
          float px = r*cos(theta);
          float pz = r*sin(theta);
          vertex(px, y*4-1000, pz);
        }
        */
      }
      theta+= delta;
    }

  }
}

class EImages extends ArrayList<EImage> {
  void render() {
    for (int i = 0; i < this.size(); i++) {
      this.get(i).render(i, this.size());
    }
  }
}

