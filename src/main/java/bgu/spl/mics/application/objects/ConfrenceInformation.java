package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.finishBroadcast;
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
    private int time;
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
        if (time == date) {
            finish = true;
            MessageBusImpl.getInstance().sendBroadcast(new finishBroadcast());
        }
    }
   public void setService(ConferenceService c){
        this.conf=c;
   }

    public boolean isFinish() {
        return finish;
    }
}
