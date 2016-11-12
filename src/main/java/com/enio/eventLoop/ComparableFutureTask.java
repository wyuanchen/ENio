package com.enio.eventLoop;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by yuan on 11/12/16.
 */
public class ComparableFutureTask<V> extends FutureTask<V> implements Comparable<ComparableFutureTask>{
    private Object object;

    public ComparableFutureTask(Callable callable) {
        super(callable);
        object=callable;
    }
    public ComparableFutureTask(Runnable runnable,V result){
        super(runnable,result);
        object=runnable;
    }

    @Override
    public int compareTo(ComparableFutureTask o) {
        if(this==o)
            return 0;
        if(o==null)
            return 1;
        if(object!=null&&o.object!=null){
            if(object.getClass().equals(o.object.getClass())){
                if(object instanceof Comparable&&o.object instanceof Comparable){
                    return ((Comparable)object).compareTo(o.object);
                }
            }

        }
        return 1;

    }
}
