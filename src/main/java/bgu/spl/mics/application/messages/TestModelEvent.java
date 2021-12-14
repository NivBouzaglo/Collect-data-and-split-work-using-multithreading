package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
//the t was added by bar
public class TestModelEvent implements Event<Model> {
    private Model model ;
    private Future<Model> future;

    public TestModelEvent(Model m){
        model=m;
        future = new Future();
    }
    public void action(Future future) {
        this.future.resolve((Model)future.get());
        notifyAll();
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
}
