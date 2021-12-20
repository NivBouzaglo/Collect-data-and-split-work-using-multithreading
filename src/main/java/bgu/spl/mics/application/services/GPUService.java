package bgu.spl.mics.application.services;
import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
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
        this.gpu = gpu;
        this.gpu.setGPU(this);
    }

    public GPU getGpu() {
        return gpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, m -> {
            gpu.addTime();
            if (gpu.getModel() != null && gpu.getModel().getStatus().compareTo(Model.status.Trained) == 0){
                MessageBusImpl.getInstance().complete(event, gpu.getModel());
                gpu.setModel(null);
            }
        });
        subscribeEvent(TrainModelEvent.class, t -> {
            this.event = t;
            gpu.setModel(t.getModel());
            this.gpu.getModel().setStatus(Model.status.Training);
            gpu.divide();
        });
        subscribeEvent(TestModelEvent.class, t->{
            complete(t,this.gpu.testGPU(t.getModel()));
        });
        subscribeBroadcast(TerminateBroadcast.class, m1 -> {
            terminate();
        });
    }

}
