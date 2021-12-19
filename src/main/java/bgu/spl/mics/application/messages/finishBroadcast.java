package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class finishBroadcast implements Broadcast {
    private Model model;
    private Event event;
    public finishBroadcast(Model model , Event event){
        this.model = model;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public Model getModel() {
        return model;
    }
}
