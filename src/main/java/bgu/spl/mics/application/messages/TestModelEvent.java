package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;
//the t was added by bar
public class TestModelEvent implements Event<String> {
    private Model model ;
    private Future<String> future;
    private MicroService service;

    public TestModelEvent(Model m){
        model=m;
        future = new Future();
    }
    public void action(String future) {
        this.future.resolve(future);
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

    public void result() {
        future.get();
    }

    public void setFuture(Future<String> future) {
        this.future = future;
    }
}
