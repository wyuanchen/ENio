package com.enio.eventLoop.event;

/**
 * Created by yuan on 11/30/16.
 */
public abstract class ScheduledEvent extends EventPriority implements Runnable{
    public ScheduledEvent(){}
    public ScheduledEvent(Priority priority){
        super(priority);
    }
}
