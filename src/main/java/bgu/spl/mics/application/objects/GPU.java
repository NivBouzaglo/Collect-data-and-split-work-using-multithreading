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
    private int index=0;
    private Event event;
    private GPUService GPU;
    private long time = 0;

    public GPU(String t){
        this.setType(t);
    }

    //Swe need to fix it.
    public  GPU(String type , Model model, Event event) {
        this.setType(type);
        this.model = model;
        cluster = Cluster.getInstance();
        this.event = event;
        batches = new LinkedList<DataBatch>();
        setProcessed();
    }

    private void setProcessed() {
        switch (type){
            case GTX1080:
                processed=new DataBatch[8];
            case RTX2080:
                processed=new DataBatch[16];
            case RTX3090:
                processed=new DataBatch[32];
        }
    }

    public String getType(){
        if (type == Type.RTX3090) return "RTX3090";
        else
        if(type == Type.RTX2080) return "RTX2080";
        else
        if(type == Type.GTX1080) return "GTX1080";
        return null;
    }
    public Model getModel(){return model;}
    public Cluster getCluster(){return cluster;}
    public Event getEvent(){return event;}
    public Queue<DataBatch> getDataBatchList(){ return batches;}
    //added by bar - this feild is not recognized in the test class.

    public void setType(String t){
        if (t.compareTo("RTX3090") == 0) type = Type.RTX3090 ;
        else
        if (t.compareTo("RTX2080") == 0) type = Type.RTX2080;
        else
        if(t.compareTo("GTX1080") == 0) type = Type.GTX1080;
    }
    /**
     * @pre batch!=null
     * @inv batches!=null.
     * @post batches.size()--.
     */
    public void sendToCluster(){
        while (!batches.isEmpty()) {
            while (cluster.full())
                try {
                    GPU.wait();
                } catch (InterruptedException e) {
                }
            cluster.addUnProcessed(batches.poll());
        }
    }
    /**
     * @pre model.data!=null
     * @inv
     * @post All the data is stores in one of the data batch.
     */
    public void divide(){
        for (int i=1; i<=model.getData().getSize();i++){
            batches.add(new DataBatch(model.getData(),i*1000));
        }
    }

    /**
     * @pre batches!=null
     * @inv model.status="Training".
     * * @post model.status = "Trained".
     */
    public void train(){
        model.setStatus(Model.status.Training);
        switch (type){
            case GTX1080:
                while (4>getTicks()){
                    try {
                        GPU.wait();

                    } catch (InterruptedException e) {}
                }
            case RTX2080:
                while (2>getTicks()){
                    try {
                        GPU.wait();

                    } catch (InterruptedException e) {}
                }
            case RTX3090:
                while (1>getTicks()){
                    try {
                        GPU.wait();
                    } catch (InterruptedException e) {}
                }
        }
        model.setStatus(Model.status.Trained);

    }

    /**
     * @pre
     * @inv
     * @post batches!=null
     */

    public void receiveFromCluster(DataBatch unit){
        if (processed[processed.length-1] == null) {
            processed[index] = unit;
            index++;
        }
        if(index< processed.length){
           processed[index]=unit;
           index++;
        }

    }
    public long getTicks(){
        return time;
    }


}