package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<String>{
    private Model model;
    private Future<String> future=new Future();

    public PublishResultsEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
    public Future getFuture() {
        return future;
    }
    public void action(String future){
        this.future.resolve(future);
    }
    public boolean goodOrBad(){
        return model.good();
    }
}
