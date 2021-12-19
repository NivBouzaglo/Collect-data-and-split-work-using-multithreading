package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<String>{
    private Model model;
    private Future<String> future=new Future();
    private MicroService service;

    public PublishResultsEvent(Model model){
        this.model = model;
    }
    public MicroService getService(){
        return service;
    }

    public void setService(MicroService service) {
        this.service = service;
    }
    public Model getModel() {
        return model;
    }
    public Future getFuture() {
        return future;
    }
    public void action(){}
    public boolean goodOrBad(){
        return model.good();
    }
    public Class getType(){
        return PublishResultsEvent.class;
    }
}
