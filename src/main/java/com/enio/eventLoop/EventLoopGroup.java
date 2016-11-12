package com.enio.eventLoop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yuan on 10/8/16.
 */
public class EventLoopGroup {
    private int defaultLoopsSize=1;
    private int loopsSize;
    private List<EventLoop> loops;
    private int curIndex;

    public EventLoopGroup(){
        this.loopsSize=defaultLoopsSize;
        initLoops();
    }

    public EventLoopGroup(int loopsSize){
        this.loopsSize=loopsSize;
        initLoops();
    }

    private void initLoops() {
        this.loops = Collections.synchronizedList(new ArrayList<EventLoop>(this.loopsSize));
        for(int i=0;i<loopsSize;i++)
            loops.add(new EventLoop());
    }

    public EventLoop selectOneEventLoop(){
        curIndex=(curIndex+1)%loopsSize;
        return loops.get(curIndex);
    }

    public void shutdown(){
        for(EventLoop eventLoop:loops){
            eventLoop.shutdown();
        }
    }

}
