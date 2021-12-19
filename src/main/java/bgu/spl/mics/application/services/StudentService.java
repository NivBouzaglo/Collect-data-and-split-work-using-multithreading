package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.concurrent.TimeUnit;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private boolean terminate = false, inProgress = false;
    private int modelCounter = 0;
    private Future future =new Future();


    public StudentService(Student student) {
        super(student.getName());
        this.student = student;
        student.setService(this);
        // TODO Implement this
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
            setTerminate();
            for (Model model : student.getModels())
                System.out.println(model + "  " + model.getStatus());
        });
        subscribeBroadcast(PublishConferenceBroadcast.class, t -> {
            for (Model name : t.getModelsName()) {
                if (student.getModels().contains(name))
                    student.addPublication();
                else
                    student.addPapersRead();
            }
        });
        subscribeBroadcast(finishBroadcast.class  , t->{this.completeTrain(t.getEvent() , t.getModel());});
        progress(student.getModels().get(0));

    }

    public void publish(Model model) {
        if (model.getR().compareTo("Good") == 0) {
            PublishResultsEvent p = new PublishResultsEvent(model);
            future = sendEvent(p);
        }
    }

    public void setTerminate() {
        terminate = true;
    }

    public void progress(Model model) {
        if (!inProgress) {
            inProgress = true;
            TrainModelEvent train = new TrainModelEvent(model);
            train.setFuture(future);
            future = sendEvent(train);
        }
    }

    public Future getFuture() {
        return future;
    }
    public void completeTrain( Event event, Model model) {
        future.resolve(model);
        MessageBusImpl.getInstance().complete(event, model);
        if (model.getStudent().getService().getFuture().get(2, TimeUnit.MILLISECONDS) != null) {
            if (model.getStatus().compareTo("Trained") == 0) {
                TestModelEvent test = new TestModelEvent(model);
                this.future = sendEvent(test);
                test.setFuture(future);
                test.action();
                completeTest(event ,model);
            }}
    }
    public void completeTest(Event event, Model f) {
        MessageBusImpl.getInstance().complete(event, f);
        this.publish(f);
        inProgress=false;
        modelCounter++;
        if (modelCounter < student.getModels().size())
            progress(student.getModels().get(modelCounter));
    }
}
