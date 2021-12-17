package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

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

    public StudentService(Student student) {
        super(student.getName());
        this.student = student;
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class, t -> {
            terminate();
        });
        subscribeBroadcast(PublishConferenceBroadcast.class, t -> {
            for (Model name : t.getModelsName()) {
                if (student.getModels().contains(name))
                    student.addPublication();
                else
                    student.addPapersRead();
            }
        });
        for (Model m : student.getModels()) {
            TrainModelEvent train = new TrainModelEvent(m);
            Future future = sendEvent(train);
            while (train.getModel().getStatus() != "Trained"){
                train.setFuture(future);
            }
            TestModelEvent test = new TestModelEvent(m);
            Future f = sendEvent(test);
            while (test.getModel().getStatus() != "Tested"){
                test.setFuture(f);
            }
            if (test.getModel().getR() == "Good") {
                PublishResultsEvent p = new PublishResultsEvent(m);
                sendEvent(p);
            }
        }
    }
}

