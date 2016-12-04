package com.enio.eventLoop.event;

/**
 * Created by yuan on 11/30/16.
 */
public abstract class EventPriority implements Comparable<EventPriority>{

    /**
     * This means the priority the event, the event with higher priority will be handled faster than
     * other event with lower priority
     */
    public enum Priority{
        Top(3),Medium(2),Low(1);
        private int value;
        private Priority(int value){
            this.value=value;
        }
        public int value(){
            return value;
        }
    }
    protected Priority priority;

    public EventPriority(Priority priority){
        this.priority=priority;
    }
    public EventPriority(){
        this(Priority.Medium);
    }

    @Override
    public int compareTo(EventPriority o) {
        if(priority.value()>o.priority.value()){
            return -1;
        }else if(priority.value()<o.priority.value()){
            return 1;
        }else{
            return 0;
        }
    }

    public void setPriority(Priority priority){
        this.priority=priority;
    }
    public Priority getPriority(){
        return this.priority;
    }
}
