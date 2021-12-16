package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
//the t was added by bar
public class TestModelEvent implements Event<Model.result> {
    private Model model ;
    private Future<Model.result> future;

    public TestModelEvent(Model m){
        model=m;
        future = new Future();
    }
    public void action(Model.result future) {
        this.future.resolve(future);
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

    public void setFuture(Future<Model.result> future) {
        this.future = future;
    }
}
