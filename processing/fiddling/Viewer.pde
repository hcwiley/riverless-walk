class Viewer {
  PVector position;
  int id;
  int lastMillis;
  int curMillis;
  float speed;

  Viewer(PVector pos, int ID) {
    position = pos;
    id = ID;
    lastMillis = curMillis = millis();
  }
  
  void updatePosition(PVector pos){
    curMillis = millis();
    speed = (pos.x - position.x)/(curMillis - lastMillis);
    position = pos;
    lastMillis = curMillis;
  }
}

class Viewers extends ArrayList<Viewer> {
  Viewers() {
  }
  Viewer viewer(int ID) {
    for (int i=0; i<this.size(); i++) {
      if (this.get(i).id == ID)
        return this.get(i);
    }
    return null;
  }
}

