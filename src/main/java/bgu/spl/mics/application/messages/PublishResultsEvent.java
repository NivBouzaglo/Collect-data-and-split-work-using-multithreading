package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent<T> implements Event<T>{
    private Model model;
    private Future future;

    public PublishResultsEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Future getFuture() {
        return future;
    }
    public void action(Future future){
        this.future.resolve(future.get());
    }
}
