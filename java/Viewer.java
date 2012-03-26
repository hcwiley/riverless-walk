import java.util.ArrayList;
import processing.core.PVector;

class Viewer {
    PVector position;
    int id;
    int lastMillis;
    int curMillis;
    float speed;
    Fiddling parent;

    Viewer(PVector pos, int ID, Fiddling sce) {
        position = pos;
        id = ID;
        parent = sce;
        lastMillis = curMillis = parent.millis();
    }

    void updatePosition(PVector pos) {
        curMillis = parent.millis();
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
    public PVector center(){
    	PVector center = new PVector();
    	for (int i = 0; i < this.size(); i++) {
            center.x += this.get(i).position.x;
            center.y += this.get(i).position.y;
            center.z += this.get(i).position.z;
        }
    	center.x /= this.size();
    	center.y /= this.size();
    	center.z /= this.size();
    	return center;
    }
}
