package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> models ;
    private int time = 0;
    private ConferenceService conf;
    private boolean finish =false;

    public ConfrenceInformation(String name , int date){
        this.name = name;
        this.date = date;
        this.models=new LinkedList<Model>();
    }
    public void addToModels(Model model){
        models.add(model);
    }
    public LinkedList<Model> getModels(){
        return models;
    }
    public String getName() {
        return name;
    }
    public int getDate() {
        return date;
    }

    public void addTime(){
        time++;
        if (time == date)
            finish = true;
    }
   public void setService(ConferenceService c){
        this.conf=c;
   }

    public boolean isFinish() {
        return finish;
    }
}
