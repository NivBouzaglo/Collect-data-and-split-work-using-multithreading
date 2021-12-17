package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private Event<Model> event;

    public GPUService(String name ,GPU gpu) {
        super(name);
        //this.gpu = new GPU(gpu.getType());
        this.gpu= gpu;
        this.gpu.setGPU(this);
    }


    @Override
    protected void initialize() {
        // TODO Implement this
        //gpu.setGPU(this);
        subscribeBroadcast(TickBroadcast.class , m ->{gpu.addTime();});
        subscribeEvent(TrainModelEvent.class , t->{gpu.setModel(t.getModel());
            gpu.setEvent(t);
            gpu.divide();});
        subscribeEvent(TestModelEvent.class , g->{gpu.test(g.getModel());
        complete((Event)g , g.getFuture().get());});
        subscribeBroadcast(TerminateBroadcast.class ,m1->{terminate();});
    }
    public void completeTest(Event event,Model model){
        MessageBusImpl.getInstance().complete(event,model.getR());

    }
    public void completeTrain(Event event,Model model){
        MessageBusImpl.getInstance().complete(event,model);

    }
}
