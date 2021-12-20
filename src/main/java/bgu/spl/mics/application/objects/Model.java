package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private final String name;
    private final Data data;
    private final Student student;
    private status s;

    private result r;

    public enum status {
        PreTrained, Training, Trained, Tested
    }

    public enum result {
        Good, Bad, None
    }

    public Model(Student student, Data data, String name) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.s = Model.status.PreTrained;
        this.r = result.None;
    }

    public status getStatus() {
        return this.s;
    }

    public Data getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStatus(status status) {
        s = status;
    }

    public void endTraining() {
        s = status.Trained;
    }

    public result getResult() {
        return r;
    }

    public String getR() {
        switch (r) {
            case Bad:
                return "Bad";
            case Good:
                return "Good";
            case None:
                return "None";
        }
        return null;
    }

    public void setResult(result rt) {
        this.r = rt;
    }

}