import java.util.ArrayList;
import java.util.Random;

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
        if (speed != 0) {
            speed = Fiddling.map(speed * 100, 0, (float) 10, 0, 20);
        }
        position = pos;
        lastMillis = curMillis;
    }
}

class Viewers extends ArrayList<Viewer> {
    Random rand = new Random();

    Viewer viewer(int ID) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).id == ID)
                return this.get(i);
        }
        return null;
    }

    public PVector center() {
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

    int speed() {
        int speed = 0;
        try{
        for (int i = 0; i < this.size(); i++) {
            speed += (int) this.get(i).speed;
        }
        return speed / this.size();
        } catch (Exception e){
            System.out.println(e);
            return -1;
        }
    }

    int getRandomY() {
        try{
        return (int) this.get(rand.nextInt(this.size())).position.y;
        }catch(Exception e){
            System.out.println(e);
            return -1;
        }
    }
}
