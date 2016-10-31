package com.enio.message;

/**
 * The messsage is used to be processed by handler in pipeline,
 * And the data of message is the content which handler will process
 * Created by yuan on 10/24/16.
 */
public class Message {
    private Object data;
    public Message(Object data){
        this.data=data;
    }
    public Message(){
        this(null);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
