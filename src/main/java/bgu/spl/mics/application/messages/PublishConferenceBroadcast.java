package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;
import java.util.List;

public class PublishConferenceBroadcast implements Broadcast {

    private List<Model> modelsName;

    public PublishConferenceBroadcast(List<Model>models) {
        this.modelsName = models;
    }
    public List<Model> getModelsName() {
        return this.modelsName;
    }
}
