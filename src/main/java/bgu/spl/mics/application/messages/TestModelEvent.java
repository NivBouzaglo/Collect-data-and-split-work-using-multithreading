package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.Model;

//the t was added by bar
public class TestModelEvent implements Event<Model> {
    private Model model;
    private Future<Model> future;
    private MicroService service;

    public TestModelEvent(Model m) {
        model = m;
        future = new Future();
    }

    public void action() {
        System.out.println("Start testing");
        double rand = Math.random();
        switch (model.getStudent().getStatus()) {
            case PhD:
                if (rand >= 0.8) {
                    model.setResult("Good");
                } else
                    model.setResult("Bad");
                break;
            case MSc:
                if (rand >= 0.6) {
                    model.setResult("Good");
                } else
                    model.setResult("Bad");
                break;
        }
        this.model.Tested();
        this.future.resolve(model);
    }

    public MicroService getService() {
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

    public void setFuture(Future<Model> future) {
        this.future = future;
    }
}
