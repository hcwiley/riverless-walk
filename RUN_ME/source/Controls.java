import controlP5.*;

public class Controls {
    public ControlP5 control;
    public ControlWindow w;
    public Fiddling parent;

    public Controls(Fiddling par) {
        parent = par;
    }

    void setParameters() {
        parent.n = 10000;
        parent.dofRatio = 50;
        parent.neighborhood = 700;
        parent.speed = 24;
        parent.viscosity = (float) .1;
        parent.spread = 100;
        parent.independence = (float) .15;
        parent.rebirth = 0;
        parent.rebirthRadius = 250;
        parent.turbulence = (float) 1.3;
        parent.cameraRate = (float) .1;
        parent.averageRebirth = false;
    }

    void makeControls() {
        control = new ControlP5(parent);

        w = control.addControlWindow("controlWindow", 10, 10, 350, 290);
        w.hideCoordinates();
        w.setTitle("Flocking Parameters");

        int y = 0;
        control.addSlider("n", 1, 20000, parent.n, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("dofRatio", 1, 200, parent.dofRatio, 10, y += 10,
                256, 9).setWindow(w);
        control.addSlider("neighborhood", 1, parent.width * 2,
                parent.neighborhood, 10, y += 10, 256, 9).setWindow(w);
        control.addSlider("speed", 0, 100, parent.speed, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("viscosity", 0, 1, parent.viscosity, 10, y += 10,
                256, 9).setWindow(w);
        control.addSlider("spread", 50, 200, parent.spread, 10, y += 10, 256, 9)
                .setWindow(w);
        control.addSlider("independence", 0, 1, parent.independence, 10,
                y += 10, 256, 9).setWindow(w);
        control.addSlider("rebirth", 0, 100, parent.rebirth, 10, y += 10, 256,
                9).setWindow(w);
        control.addSlider("rebirthRadius", 1, parent.width,
                parent.rebirthRadius, 10, y += 10, 256, 9).setWindow(w);
        control.addSlider("turbulence", 0, 4, parent.turbulence, 10, y += 10,
                256, 9).setWindow(w);
        control.addToggle("paused", false, 10, y += 35, 9, 9).setWindow(w);
//        control.addSlider("blocksize", (int)0, (int)700, (int)parent.blocksize, 10,
//                y += 10, 256, 9).setWindow(w);
        control.addSlider("theta", (float)0, (float)10, (float)parent.theta, 10,
                y += 15, 256, 9).setWindow(w);
        control.addSlider("thetaDelta", (float)0, (float)10, (float)parent.thetaDelta, 10,
                y += 15, 256, 9).setWindow(w);
        control.addSlider("falloff", (float)0, (float)1, (int)parent.falloff, 10,
                y += 15, 256, 9).setWindow(w);
        control.setAutoInitialization(true);
        
    }

}
