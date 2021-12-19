package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;
//i added <T>
public class TrainModelEvent implements Event<Model> {
    private Model model ;
    private Future<Model> future;
    private MicroService service;

    public TrainModelEvent(Model m){
        model = m ;
        future = null ;
    }
    public MicroService getService(){
        return service;
    }

    public void setService(MicroService service) {
        this.service = service;
    }

    public void action() {
        this.future.resolve(model);
    }

    public Model getModel() {
        return model;
    }

    public Future getFuture() {
        return future;
    }

    public boolean process() {
        return false;
    }

    public void setFuture(Future<Model> future) {
        this.future = future;
    }
}
