package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
//i added <T>
public class TrainModelEvent implements Event<Model> {
    private Model model ;
    private Future<Model> future;

    public TrainModelEvent(Model m){
        model = m ;
        future = new Future<>();
    }


    public void action(Model future) {
        this.future.resolve(future);
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
