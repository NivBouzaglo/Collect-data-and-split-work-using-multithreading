package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
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
        });
        subscribeBroadcast(PublishConferenceBroadcast.class, t -> {
            for (Model model : t.getModels()) {
                if (student.getModels().contains(model))
                    student.addPublication();
                else
                    student.addPapersRead();
            }
        });
        subscribeBroadcast(finishBroadcast.class  , t->{
            if (this.getFuture().get(1,TimeUnit.MILLISECONDS)!=null){
                Model model = (Model) this.getFuture().get(1,TimeUnit.MILLISECONDS);
                if (model.getStatus() == Model.status.Trained) {
                    System.out.println(model.getName() + "Testing");
                    future = this.sendEvent(new TestModelEvent(model));
                }
                else if (model.getStatus() == Model.status.Tested && model.getResult() == Model.result.Good) {
                    System.out.println(model.getName() + "Publish");
                    future = this.sendEvent(new PublishResultsEvent(model));
                }
                else if (model.getResult()== Model.result.Bad && this.student.getModels().size()-1>modelCounter){
                    modelCounter++;
                    model = this.student.getModels().get(modelCounter);
                    System.out.println("New trainmodelEvent" + model.getName());
                    future = this.sendEvent(new TrainModelEvent(model));
                }
            }
        });
        if (!this.student.getModels().isEmpty()){
            System.out.println("start training" + student.getModels().get(0).getName());
            future = this.sendEvent(new TrainModelEvent(this.student.getModels().get(0)));
        }
    }
    public Future getFuture() {
        return future;
    }
}
