package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;

    enum status{
        PreTrained,Training
    }
    public Model(Student student , Data data , String name){
        this.name = name;
        this.data = data;
        this.student = student;
    }
    public Data getData(){return data;}
    public String getName(){return name;}
    public Student getStudent(){return student;}


}