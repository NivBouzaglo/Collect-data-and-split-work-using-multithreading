package bgu.spl.mics.application.objects;

import org.junit.Test;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;
    private status s;
    private boolean publish;
    private result r;

    enum status {
        PreTrained, Training, Trained, Tested
    }

    public enum result {
        Good, Bad , None
    }

    public Model(Student student, Data data, String name) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.s = Model.status.PreTrained;
        this.publish = false;
        this.r=result.None;
    }

    public String getStatus() {
        switch (s){
            case Tested:
                return "Tested";
            case PreTrained:
                return "PreTrained";
            case Training:
                return "Training";
            case Trained:
                return "Trained";
        }
        return null;
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

    public void Tested() {
        s = status.Tested;
    }

    public void setPublish() {
        publish = true;
    }

    public result getR() {
        return r;
    }

    public void setResult(String rt) {
        switch (rt) {
            case "bad":
                this.r = result.Bad;
                break;
            case "good":
                this.r = result.Good;
                break;
        }
    }

    public boolean good(){
        if (r == result.Good)
            return true;
        return false;
    }

    public boolean isPublish () {
            return publish;
        }
    }