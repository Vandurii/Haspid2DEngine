package main.observers;

import main.haspid.GameObject;
import main.observers.events.Event;

public interface Observer {
    void onNotify(GameObject gameObject, Event event);
}
