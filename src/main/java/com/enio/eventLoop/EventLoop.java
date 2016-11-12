package com.enio.eventLoop;

import com.enio.Channel.Channel;
import com.enio.eventLoop.event.Event;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by yuan on 10/8/16.
 */
public class EventLoop {
//    private ExecutorService executorService= Executors.newSingleThreadScheduledExecutor();
    private ExecutorService executorService=new PriorityThreadPoolExecutor(1,1,1,TimeUnit.SECONDS,new PriorityBlockingQueue<Runnable>());
    private Selector selector;
    private volatile boolean isEnd=false;

    public void terminate(){
        this.isEnd=true;
    }

    public Selector getSelector(){
        return selector;
    }

    public EventLoop(){
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
        executorService.submit(new Event<Object>(Event.Priority.Low) {
            @Override
            public Object call() throws Exception {
                selector.select(100);
                Set<SelectionKey> keys=selector.selectedKeys();
                Iterator<SelectionKey> iterator=keys.iterator();
                SelectionKey key=null;
                while(iterator.hasNext()){
                    key=iterator.next();
                    Channel channel= (Channel) key.attachment();
                    channel.handle();
                    iterator.remove();
                }
                if(!isEnd)
                    executorService.submit(this);
                return null;
            }
        });
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
