package bgu.spl.mics.application.objects;
//added by bar

import bgu.spl.mics.Event;
import bgu.spl.mics.application.services.GPUService;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */

    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private Queue<DataBatch> batches;
    private DataBatch[] processed;
    private int index = 0;
    private Event event;
    private long change = 0;
    private GPUService GPU;
    private long time = 1;

    public GPU(String t) {
        this.setType(t);
    }

    //Swe need to fix it.
    public GPU(String type, Model model, Event event) {
        this.setType(type);
        this.model = model;
        cluster = Cluster.getInstance();
        this.event = event;
        batches = new LinkedList<DataBatch>();
        setProcessed();
    }

    private void setProcessed() {
        switch (type) {
            case GTX1080:
                processed = new DataBatch[8];
            case RTX2080:
                processed = new DataBatch[16];
            case RTX3090:
                processed = new DataBatch[32];
        }
    }

    public String getType() {
        if (type == Type.RTX3090) return "RTX3090";
        else if (type == Type.RTX2080) return "RTX2080";
        else if (type == Type.GTX1080) return "GTX1080";
        return null;
    }
    public DataBatch[] getProcessed(){return processed;}

    public Model getModel() {
        return model;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public Event getEvent() {
        return event;
    }

    public Queue<DataBatch> getDataBatchList() {
        return batches;
    }
    //added by bar - this feild is not recognized in the test class.

    public void setType(String t) {
        if (t.compareTo("RTX3090") == 0) type = Type.RTX3090;
        else if (t.compareTo("RTX2080") == 0) type = Type.RTX2080;
        else if (t.compareTo("GTX1080") == 0) type = Type.GTX1080;
    }

    /**
     * @pre batch!=null
     * @inv batches!=null.
     * @post batches.size()--.
     */
    public void sendToCluster() {
            cluster.addUnProcessed(batches.poll(),this);
        }

    /**
     * @pre model.data!=null
     * @inv
     * @post All the data is stores in one of the data batch.
     */
    public void divide() {
        for (int i = 1; i <= model.getData().getSize(); i++) {
            DataBatch dataBatch= new DataBatch(model.getData(), i * 1000);
            dataBatch.setGpuIndex(cluster.findGPU(this));
            batches.add(dataBatch);
        }
    }

    /**
     * @pre batches!=null
     * @inv model.status="Training".
     * * @post model.status = "Trained".
     */
    public void train() {

    }

    /**
     * @pre
     * @inv
     * @post batches!=null
     */

    public void receiveFromCluster(DataBatch unit) {
        if (index < processed.length) {
            processed[index] = unit;
            index++;
        }
    }

    public void addTime() {
        time++;
    }

    public long getTicks() {
        return time;
    }

    public Model.result test(Model model) {
        double rand = Math.random();
        switch (model.getStudent().getStatus()){
            case PhD:
                if (rand>=0.8){
                    model.setResult("Good");
                    return Model.result.Good;
                }
            case MSc:
                if (rand>=0.6){
                    model.setResult("Good");
                    return Model.result.Good;
                }
        }
        model.setResult("Bad");
        return Model.result.Bad;
    }


}