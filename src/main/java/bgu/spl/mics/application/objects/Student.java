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
    public enum Degree {MSc, PhD}

    private String name;
    private String department;
    private Degree status;
    private int publications = 0;
    private int papersRead = 0;
    private LinkedList<Model> models;


    public Student(String name, String department, String degree ) {
        this.name = name;
        this.department = department;
        this.models=new LinkedList<>();
        status = degree(degree);
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

    public Degree getStatus() {
        return status;
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

    public void addPublication(){
        publications++;
    }

    public void addPapersRead(){
        papersRead++;
    }
}
