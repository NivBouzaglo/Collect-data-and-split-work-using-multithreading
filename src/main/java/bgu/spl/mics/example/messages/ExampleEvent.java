package bgu.spl.mics.example.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

public class ExampleEvent implements Event<String>{

    private String senderName;
    private MicroService service;

    public void setService(MicroService service) {
        this.service = service;
    }

    @Override
    public MicroService getService() {
        return service;
    }

    public ExampleEvent(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
    public void action(String future){}

    @Override
    public Future getFuture() {
        return null;
    }
}