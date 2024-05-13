package main.physics.events;

import main.haspid.GameObject;
import main.physics.events.Event;

public interface Observer {
    void onNotify(GameObject gameObject, Event event);
}
