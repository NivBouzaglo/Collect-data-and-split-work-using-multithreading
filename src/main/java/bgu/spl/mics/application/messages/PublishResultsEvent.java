package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent<T> implements Event<T>{
    @Override
    public Model action() {
        return null;
    }

    @Override
    public T result() {
        return null;
    }

    @Override
    public boolean process() {
        return false;
    }
}
