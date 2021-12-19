package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    ConfrenceInformation conf;
    PublishConferenceBroadcast b;
    public ConferenceService(ConfrenceInformation c) {
        super(c.getName());
        this.conf = c;
        c.setService(this);
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeBroadcast(TickBroadcast.class, m->{conf.addTime(); if (conf.isFinish()){
            sendBroadcast(new PublishConferenceBroadcast(conf.getModels()));
            terminate();
            MessageBusImpl.getInstance().unregister(this);
        }});
        subscribeEvent(PublishResultsEvent.class , t ->{
            t.setService(this);
            conf.addToModels(t.getModel());});

    }
}
