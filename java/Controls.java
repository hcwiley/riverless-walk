import processing.core.PApplet;
import controlP5.*;

public class Controls {
    public ControlP5 control;
    public ControlWindow w;
    public float neighborhood, viscosity, speed, turbulence, cameraRate,
            rebirthRadius, spread, independence, dofRatio;
    public int n, rebirth;
    public boolean averageRebirth, paused;

    void setParameters() {
        n = 10000;
        dofRatio = 50;
        neighborhood = 700;
        speed = 24;
        viscosity = (float).1;
        spread = 100;
        independence = (float).15;
        rebirth = 0;
        rebirthRadius = 250;
        turbulence = (float)1.3;
        cameraRate = (float).1;
        averageRebirth = false;
    }

    void makeControls(PApplet scene) {
        control = new ControlP5(scene);

        w = control.addControlWindow("controlWindow", 10, 10, 350, 140);
        w.hideCoordinates();
        w.setTitle("Flocking Parameters");

        int y = 0;
        control.addSlider("n", 1, 20000, n, 10, y += 10, 256, 9).setWindow(w);
        control.addSlider("dofRatio", 1, 200, dofRatio, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("neighborhood", 1, scene.width * 2, neighborhood, 10,
                y += 10, 256, 9).setWindow(w);
        control.addSlider("speed", 0, 100, speed, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("viscosity", 0, 1, viscosity, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("spread", 50, 200, spread, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("independence", 0, 1, independence, 10, y += 10, 256,
                9).setWindow(w);
        control.addSlider("rebirth", 0, 100, rebirth, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("rebirthRadius", 1, scene.width, rebirthRadius, 10,
                y += 10, 256, 9).setWindow(w);
        control.addSlider("turbulence", 0, 4, turbulence, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addToggle("paused", false, 10, y += 11, 9, 9).setWindow(w);
        control.setAutoInitialization(true);
    }

}