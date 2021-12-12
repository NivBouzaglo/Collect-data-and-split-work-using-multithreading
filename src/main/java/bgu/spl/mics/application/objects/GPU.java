package bgu.spl.mics.application.objects;
//added by bar
import java.lang.reflect.Array;
import java.security.Provider;
import java.util.*;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.GPUService;

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



    public  GPU(String t , Model m, Event e) {
        this.setType(t);
        model = m;
        cluster = Cluster.getInstance();
        event = e;
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
            cluster.add(batches.poll());
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
        model.getTraining();
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
    }
    public long getTicks(){return 0;}


}