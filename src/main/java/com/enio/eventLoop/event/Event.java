package com.enio.eventLoop.event;

import java.util.concurrent.Callable;

/**
 * Created by yuan on 11/7/16.
 */
public abstract class Event<V> extends EventPriority implements Callable<V>{

    public Event(Priority priority){
        super(priority);
    }
    public Event(){}



}
