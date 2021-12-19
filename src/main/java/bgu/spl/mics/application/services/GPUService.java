package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private Event<Model> event;

    public GPUService(String name, GPU gpu) {
        super(name);
        //this.gpu = new GPU(gpu.getType());
        this.gpu = gpu;
        this.gpu.setGPU(this);
        this.gpu.setBusy();
    }

    public GPU getGpu() {
        return gpu;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        subscribeBroadcast(TickBroadcast.class, m -> {
            gpu.addTime();
            if (gpu.getModel() != null && gpu.getModel().getStatus().compareTo("Trained") == 0){
                sendBroadcast(new finishBroadcast( gpu.getModel() , event));
                gpu.setModel(null);
                gpu.setEvent(null);
                gpu.setBatches();
            }
        });
        subscribeEvent(TrainModelEvent.class, t -> {
            System.out.println("start training "+ t.getModel());
            this.event = t;
            t.setService(this);
            gpu.setModel(t.getModel());
            gpu.setEvent(t);
            gpu.divide();
            gpu.setModel(null);
            gpu.setBatches();
            gpu.setProcessed();

        });
        subscribeBroadcast(TerminateBroadcast.class, m1 -> {
            terminate();
        });
    }

}
