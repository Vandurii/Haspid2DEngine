package main.physics.events;

public class Event {
    private EventType eventType;

    public Event(EventType eventType){
        this.eventType = eventType;
    }

    public EventType getEventType(){
        return eventType;
    }
}
