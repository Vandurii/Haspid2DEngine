package main.physics;

import main.components.Component;
import main.haspid.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2d;
import org.joml.Vector2f;

public class HContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2d aNormal = new Vector2d(worldManifold.normal.x, worldManifold.normal.y);
        Vector2d bNormal = new Vector2d(aNormal).negate();

        for(Component c: objectA.getAllComponent()){
             c.beginCollision(objectB, contact, aNormal);
        }

        for(Component c: objectB.getAllComponent()){
             c.beginCollision(objectA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2d aNormal = new Vector2d(worldManifold.normal.x, worldManifold.normal.y);
        Vector2d bNormal = new Vector2d(aNormal).negate();

        for(Component c: objectA.getAllComponent()){
            c.endCollision(objectB, contact, aNormal);
        }

        for(Component c: objectB.getAllComponent()){
            c.endCollision(objectA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2d aNormal = new Vector2d(worldManifold.normal.x, worldManifold.normal.y);
        Vector2d bNormal = new Vector2d(aNormal).negate();

        for(Component c: objectA.getAllComponent()){
            c.preSolve(objectB, contact, aNormal);
        }

        for(Component c: objectB.getAllComponent()){
            c.preSolve(objectA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        GameObject objectA = (GameObject) contact.getFixtureA().getUserData();
        GameObject objectB = (GameObject) contact.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2d aNormal = new Vector2d(worldManifold.normal.x, worldManifold.normal.y);
        Vector2d bNormal = new Vector2d(aNormal).negate();

        for(Component c: objectA.getAllComponent()){
            c.postSolve(objectB, contact, aNormal);
        }

        for(Component c: objectB.getAllComponent()){
            c.postSolve(objectA, contact, bNormal);
        }
    }
}
