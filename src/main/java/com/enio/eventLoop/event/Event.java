package com.enio.eventLoop.event;

import java.util.concurrent.Callable;

/**
 * Created by yuan on 11/7/16.
 */
public abstract class Event<T> implements Callable<T>,Comparable<Event>{

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
    private Priority priority;

    public Event(){
        this(Priority.Medium);
    }

    public Event(Priority priority){
        this.priority=priority;
    }

    @Override
    public int compareTo(Event o) {
        if(priority.value()>o.priority.value()){
            return 1;
        }else if(priority.value()<o.priority.value()){
            return -1;
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
