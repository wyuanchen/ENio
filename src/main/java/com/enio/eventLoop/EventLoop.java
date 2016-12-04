package com.enio.eventLoop;

import com.enio.Channel.Channel;
import com.enio.eventLoop.event.Event;
import com.enio.eventLoop.event.ScheduledEvent;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * Created by yuan on 10/8/16.
 */
public class EventLoop {
//    private ExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
    private ScheduledExecutorService executorService=new PriorityScheduledThreadPoolExecutor(1);
    private Selector selector;
    private volatile boolean isEnd=false;
    public void terminate(){
        this.isEnd=true;
    }

    public Selector getSelector(){
        return selector;
    }

    public EventLoop(){
        Lock l;
        try {
            this.selector=Selector.open();
            startEventLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public Future<ClientChannel> bindClientChannel(final SelectableChannel channel, final int selectionKeyOP) throws IOException, IllegalAccessException, InstantiationException {
//        channel.configureBlocking(false);
//        Future<ClientChannel> future=executorService.submit(new Callable<ClientChannel>() {
//            public ClientChannel call() throws Exception {
//                SelectionKey key=channel.register(selector,selectionKeyOP);
//                ClientChannel clientChannel=new ClientChannel(key,EventLoop.this);
//                clientChannel.registerHandler();
//                Channel.channels.add(clientChannel);
//                return clientChannel;
//            }
//        });
//        return future;
//    }

//    public Future<ServerChannel> bindServerChannel(final SelectableChannel channel, final int selectionKeyOP, final EventLoopGroup loops, final List<Handler> handlers) throws IOException, IllegalAccessException, InstantiationException {
//        channel.configureBlocking(false);
//        Future<ServerChannel> future=executorService.submit(new Callable<ServerChannel>() {
//            public ServerChannel call() throws Exception {
//                SelectionKey key=channel.register(selector,selectionKeyOP);
//                ServerChannel serverChannel=new ServerChannel(key,EventLoop.this,loops);
//                serverChannel.registerHandler(handlers);
//                Channel.channels.add(serverChannel);
//                return serverChannel;
//            }
//        });
//        return future;
//    }


    /**
     * The eventLoop start loop
     */
    private void startEventLoop() {
        executorService.scheduleWithFixedDelay(new ScheduledEvent(Event.Priority.Low) {
            @Override
            public void run()  {
                try {
                    selector.select(1);
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    SelectionKey key = null;
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        Channel channel = (Channel) key.attachment();
                        channel.handle();
                        iterator.remove();
                    }
//                    if (!isEnd)
//                        executorService.submit(this);
                }catch (Throwable e){
                    e.printStackTrace();
                    return;
                }
            }
        },0,100,TimeUnit.MILLISECONDS);
    }

    public void shutdown(){
        executorService.submit(new Event<Object>(Event.Priority.Low) {
            @Override
            public Object call() throws Exception {
                selector.close();
                isEnd=true;
                executorService.shutdown();
                return null;
            }
        });
    }

    public <T> Future<T> submit(Event<T> event){
        return executorService.submit(event);
    }


}
