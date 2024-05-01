package main.observers;

import main.haspid.GameObject;
import main.observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    private static List<Observer> observerList = new ArrayList<>();

    public static void addObserver(Observer observer){
        observerList.add(observer);
    }

    public static void notify(GameObject gameObject, Event event){
        for(Observer observer: observerList){
            observer.onNotify(gameObject, event);
        }
    }
}
