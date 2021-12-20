package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model>{
    private Model model;

    public PublishResultsEvent(Model model){
        this.model = model;
    }
    public Model getModel() {
        return model;
    }
}
