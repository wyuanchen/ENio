package com.enio.eventLoop;

import com.enio.eventLoop.event.EventPriority;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import static java.util.concurrent.TimeUnit.NANOSECONDS;


public class PriorityScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    private static final AtomicLong sequencer = new AtomicLong();


    private class MyScheduledTask<V>
            extends FutureTask<V> implements RunnableScheduledFuture<V>,Callable<V>{

        /** Sequence number to break ties FIFO */
        private final long sequenceNumber;

        /** The time the task is enabled to execute in nanoTime units */
        private long time;

        /**
         * Period in nanoseconds for repeating tasks.  A positive
         * value indicates fixed-rate execution.  A negative value
         * indicates fixed-delay execution.  A value of 0 indicates a
         * non-repeating task.
         */
        private final long period;

        EventPriority eventPriority;


        /**
         * Creates a one-shot action with given nanoTime-based trigger time.
         */
        MyScheduledTask(Runnable r, V result, long ns) {
            super(r, result);
            this.time = ns;
            this.period = 0;
            this.eventPriority= (EventPriority) r;
            sequenceNumber=sequencer.getAndIncrement();
        }

        /**
         * Creates a periodic action with given nano time and period.
         */
        MyScheduledTask(Runnable r, V result, long ns, long period) {
            super(r, result);
            this.time = ns;
            this.period = period;
            this.eventPriority= (EventPriority) r;
            sequenceNumber=sequencer.getAndIncrement();
        }

        /**
         * Creates a one-shot action with given nanoTime-based trigger time.
         */
        MyScheduledTask(Callable<V> callable, long ns) {
            super(callable);
            this.time = ns;
            this.period = 0;
            this.eventPriority= (EventPriority) callable;
            sequenceNumber=sequencer.getAndIncrement();
        }




        public long getDelay(TimeUnit unit) {
            return unit.convert(time - now(), NANOSECONDS);
        }

        public int compareTo(Delayed other) {
            if (other == this) // compare zero if same object
                return 0;
            if (other instanceof MyScheduledTask) {
                MyScheduledTask<?> x = (MyScheduledTask<?>)other;
                long currentTime=now();
                if(time>currentTime&&x.time>currentTime){
                    long diff =time-x.time;
                    if(diff<0)
                        return -1;
                    else if(diff>0)
                        return 1;
                    else if(sequenceNumber<x.sequenceNumber)
                        return -1;
                    else
                        return 1;
                }else if(time<=currentTime&&x.time<=currentTime){
                    int priorityCompareResult=eventPriority.compareTo(x.eventPriority);
                    if(priorityCompareResult!=0)
                        return priorityCompareResult;
                    else if(sequenceNumber<x.sequenceNumber)
                        return -1;
                    else
                        return 1;
                }else if(time<=currentTime)
                    return -1;
                else
                    return 1;

            }
            long diff = getDelay(NANOSECONDS) - other.getDelay(NANOSECONDS);
            return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
        }

        /**
         * Returns {@code true} if this is a periodic (not a one-shot) action.
         *
         * @return {@code true} if periodic
         */
        public boolean isPeriodic() {
            return period != 0;
        }

        /**
         * Sets the next time to run for a periodic task.
         */
        private void setNextRunTime() {
            long p = period;
            if (p > 0)
                time += p;
            else
                time = triggerTime(-p);
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancelled = super.cancel(mayInterruptIfRunning);
            if (cancelled && getRemoveOnCancelPolicy() )
                remove(this);
            return cancelled;
        }

        /**
         * Overrides FutureTask version so as to reset/requeue if periodic.
         */
        public void run() {
            boolean periodic = isPeriodic();
            if (isShutdown())
                cancel(false);
            else if (!periodic)
                super.run();
            else if (super.runAndReset()) {
                setNextRunTime();
                getQueue().add(this);
            }
        }

        @Override
        public V call() throws Exception {
            return null;
        }
    }


    /**
     * Returns current nanosecond time.
     */
    final long now() {
        return System.nanoTime();
    }



    protected <V> RunnableScheduledFuture<V> decorateTask(
            Runnable runnable, RunnableScheduledFuture<V> task) {
        return (RunnableScheduledFuture<V>) runnable;
    }


    protected <V> RunnableScheduledFuture<V> decorateTask(
            Callable<V> callable, RunnableScheduledFuture<V> task) {
        return (RunnableScheduledFuture<V>) callable;
    }


    public PriorityScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }


    public PriorityScheduledThreadPoolExecutor(int corePoolSize,
                                       ThreadFactory threadFactory) {
        super(corePoolSize,threadFactory);
    }


    public PriorityScheduledThreadPoolExecutor(int corePoolSize,
                                       RejectedExecutionHandler handler) {
        super(corePoolSize,handler);
    }


    public PriorityScheduledThreadPoolExecutor(int corePoolSize,
                                       ThreadFactory threadFactory,
                                       RejectedExecutionHandler handler) {
        super(corePoolSize,threadFactory);
    }

    /**
     * Returns the trigger time of a delayed action.
     */
    private long triggerTime(long delay, TimeUnit unit) {
        return triggerTime(unit.toNanos((delay < 0) ? 0 : delay));
    }

    /**
     * Constrains the values of all delays in the queue to be within
     * Long.MAX_VALUE of each other, to avoid overflow in compareTo.
     * This may occur if a task is eligible to be dequeued, but has
     * not yet been, while some other task is added with a delay of
     * Long.MAX_VALUE.
     */
    private long overflowFree(long delay) {
        Delayed head = (Delayed) super.getQueue().peek();
        if (head != null) {
            long headDelay = head.getDelay(NANOSECONDS);
            if (headDelay < 0 && (delay - headDelay < 0))
                delay = Long.MAX_VALUE + headDelay;
        }
        return delay;
    }

    /**
     * Returns the trigger time of a delayed action.
     */
    long triggerTime(long delay) {
        return now() +
                ((delay < (Long.MAX_VALUE >> 1)) ? delay : overflowFree(delay));
    }



    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public ScheduledFuture<?> schedule(Runnable command,
                                       long delay,
                                       TimeUnit unit) {
        if (command == null || unit == null)
            throw new NullPointerException();
        if(!(command instanceof EventPriority))
            throw new EventPriorityCastException();
        command=new MyScheduledTask<Void>(command,null,triggerTime(delay,unit));
        return super.schedule(command,delay,unit);
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                           long delay,
                                           TimeUnit unit) {
        if (callable == null || unit == null)
            throw new NullPointerException();
        if(!(callable instanceof EventPriority))
            throw new EventPriorityCastException();
        callable=new MyScheduledTask<V>(callable,triggerTime(delay,unit));
        return super.schedule(callable,delay,unit);
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     * @throws IllegalArgumentException   {@inheritDoc}
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit) {
        if (command == null || unit == null)
            throw new NullPointerException();
        if (period <= 0)
            throw new IllegalArgumentException();
        if(!(command instanceof EventPriority))
            throw new EventPriorityCastException();
        command=new MyScheduledTask<Void>(command,null,triggerTime(initialDelay,unit),unit.toNanos(period));
        return super.scheduleAtFixedRate(command,initialDelay,period,unit);
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     * @throws IllegalArgumentException   {@inheritDoc}
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     long initialDelay,
                                                     long delay,
                                                     TimeUnit unit) {
        if (command == null || unit == null)
            throw new NullPointerException();
        if (delay <= 0)
            throw new IllegalArgumentException();
        if(!(command instanceof EventPriority))
            throw new EventPriorityCastException();
        command=new MyScheduledTask<Void>(command,null,triggerTime(initialDelay,unit),unit.toNanos(-delay));
        return super.scheduleWithFixedDelay(command,initialDelay,delay,unit);
    }

}

class EventPriorityCastException extends RuntimeException {
    public EventPriorityCastException(){
        super("EventPriorityCastException");
    }
}