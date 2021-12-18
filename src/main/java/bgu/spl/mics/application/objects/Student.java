package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.StudentService;

import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {MSc, PhD}

    private String name;
    private String department;
    private Degree status;
    private int publications = 0;
    private int papersRead = 0;
    //added by bar
    private LinkedList<Model> models;
    private StudentService service;

    public Student(String name, String department, String degree ) {
        this.name = name;
        this.department = department;
        this.models=new LinkedList<Model>();
        //this.service = new StudentService(this);
        status = degree(degree);
    }

    public void setService(StudentService service) {
        this.service = service;
    }

    public  void setModels(LinkedList<Model> s_models){
        this.models=s_models;
    }

    public String getDepartment() {
        return department;
    }

    private Degree degree(String degree) {
        if (degree.compareTo("MSc") == 0)
            return Degree.MSc;
        else
            return Degree.PhD;
    }
    public StudentService getService() {
        return service;
    }

    public Degree getStatus() {
        return status;
    }

    public void setService(StudentService service) {
        this.service = service;
    }

    public int getPublications() {
        return publications;
    }
    public int getPapersRead() {
        return papersRead;
    }

    public String getName() {
        return name;
    }

    public LinkedList<Model> getModels() {
        return this.models;
    }

    public void createModel(Data data, String m_name) {
        Model train = new Model(this, data, m_name);
        models.add(train);
    }

    public void addPublication(){
        publications++;
    }

    public void addPapersRead(){
        papersRead++;
    }
}
