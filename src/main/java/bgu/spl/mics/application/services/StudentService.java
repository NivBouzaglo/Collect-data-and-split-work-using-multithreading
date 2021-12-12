package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
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
        subscribeBroadcast();
        for (Model m : student.getModels()) {
            TrainModelEvent train = new TrainModelEvent(m);
            Future future = sendEvent(train);
            train.action(future);
            while (!train.getFuture().isDone()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            if (m.getStatus() == "Trained") {
                TestModelEvent test = new TestModelEvent(train.getModel());
                test.action(sendEvent(test));
                while (!test.getFuture().isDone()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                m.Tested();
                PublishResultsEvent p = new PublishResultsEvent(m);
                this.sendEvent(p);
            }
        }
    }
}
