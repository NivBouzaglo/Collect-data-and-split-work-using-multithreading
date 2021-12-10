package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TrainModelEvent;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {MSc, PhD}

    private int name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;

    public Student(int name , String department){
        this.name = name;
        this.department = department;
    }

    public TrainModelEvent CreateEvent(Data data){
        Model train = new Model(this);
    }
}
