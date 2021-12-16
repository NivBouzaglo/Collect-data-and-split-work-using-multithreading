package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

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
        System.out.println("student service");
        subscribeBroadcast(PublishConferenceBroadcast.class, t -> {
            for (Model name : t.getModelsName()) {
                if (student.getModels().contains(name))
                    student.addPublication();
                else
                    student.addPapersRead();
            }
        });
        for (Model m : student.getModels()) {
            System.out.println("train");
            TrainModelEvent train = new TrainModelEvent(m);
            Future future = sendEvent(train);
            train.action(train);
            if (m.getStatus() == "Trained") {
                System.out.println("test");
                TestModelEvent test = new TestModelEvent((Model) future.get());
                test.action(sendEvent(test));
                if (test.getModel().getStatus() == "Tested") {
                    PublishResultsEvent p = new PublishResultsEvent(m);
                    sendEvent(p);
                }
            }
        }
        subscribeBroadcast(TerminateBroadcast.class, t -> { terminate(); });
    }

}
