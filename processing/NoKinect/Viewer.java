import java.util.ArrayList;
import java.util.Random;

import processing.core.PVector;

class Viewer {
    PVector position;
    int id;
    int lastMillis;
    int curMillis;
    float speed;
    NoKinect parent;

    Viewer(PVector pos, int ID, NoKinect sce) {
        position = pos;
        id = ID;
        parent = sce;
        lastMillis = curMillis = parent.millis();
    }

    void updatePosition(PVector pos) {
        curMillis = parent.millis();
        speed = NoKinect.abs((pos.x - position.x) / (curMillis - lastMillis));
        if (speed > 0.01) {
        	speed = NoKinect.map(speed, 0, 1, 0, 30);
        }
        else
        	speed = 0;
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
