import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

class Viewer {
    PVector position;
    int id;
    int lastMillis;
    int curMillis;
    float speed;
    PApplet scene;

    Viewer(PVector pos, int ID, PApplet sce) {
        position = pos;
        id = ID;
        scene = sce;
        lastMillis = curMillis = scene.millis();
    }

    void updatePosition(PVector pos) {
        curMillis = scene.millis();
        speed = (pos.x - position.x) / (curMillis - lastMillis);
        position = pos;
        lastMillis = curMillis;
    }
}

class Viewers extends ArrayList<Viewer> {
    Viewer viewer(int ID) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).id == ID)
                return this.get(i);
        }
        return null;
    }
}
