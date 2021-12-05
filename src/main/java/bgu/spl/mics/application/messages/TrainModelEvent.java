package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Model> {
    @Override
    public Model action() {
        return null;
    }

    @Override
    public Model result() {
        return null;
    }

    @Override
    public boolean process() {
        return false;
    }
}
