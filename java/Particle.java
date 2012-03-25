import toxi.geom.*;
import java.util.Random;

import processing.core.PApplet;

class Particle {
    Vec3D centeringForce;
    Vec3D position, velocity, force;
    Vec3D localOffset;
    Random generator = new Random();
    Fiddling parent;

    public int random(float radius) {
        return generator.nextInt(Math.abs((int) radius));
    }

    Particle(Fiddling par) {
        centeringForce = new Vec3D();
        parent = par;
        resetPosition();
        velocity = new Vec3D();
        force = new Vec3D();
        localOffset = Vec3D.randomVector();
    }

    void resetPosition() {
        position = Vec3D.randomVector();
        position.scaleSelf(random(parent.rebirthRadius));
        if (parent.particles.size() == 0)
            position.addSelf(parent.avg);
        else
            position.addSelf(parent.randomParticle().position);
    }

    void draw() {
        float distanceToFocalPlane = parent.focalPlane
                .getDistanceToPoint(position);
        distanceToFocalPlane *= 1 / parent.dofRatio;
        distanceToFocalPlane = parent.constrain(distanceToFocalPlane, 1, 15);
        parent.strokeWeight(distanceToFocalPlane);
        parent.stroke(255, parent.constrain(
                255 / (distanceToFocalPlane * distanceToFocalPlane), 1, 255));
        parent.point(position.x, position.y, position.z);
    }

    void applyFlockingForce() {
        force.addSelf((float) (parent.noise(position.x / parent.neighborhood
                + parent.globalOffset.x + localOffset.x * parent.independence,
                position.y / parent.neighborhood, position.z
                        / parent.neighborhood) - .5), (float) (parent.noise(
                position.x / parent.neighborhood, position.y
                        / parent.neighborhood + parent.globalOffset.y
                        + localOffset.y * parent.independence, position.z
                        / parent.neighborhood) - .5), (float) (parent.noise(
                position.x / parent.neighborhood, position.y
                        / parent.neighborhood, position.z / parent.neighborhood
                        + parent.globalOffset.z + localOffset.z
                        * parent.independence) - .5));
    }

    void applyViscosityForce() {
        force.addSelf(velocity.scale(-parent.viscosity));
    }

    void applyCenteringForce() {
        centeringForce.set(position);
        centeringForce.subSelf(parent.avg);
        float distanceToCenter = centeringForce.magnitude();
        centeringForce.normalize();
        centeringForce.scaleSelf(-distanceToCenter
                / (parent.spread * parent.spread));
        force.addSelf(centeringForce);
    }

    void update() {
        force.clear();
        applyFlockingForce();
        applyViscosityForce();
        applyCenteringForce();
        velocity.addSelf(force); // mass = 1
        position.addSelf(velocity.scale(parent.speed));
    }
}
