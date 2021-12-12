package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Collection<DataBatch> data;
    private Cluster cluster;
    private boolean processing;

    public CPU(int i_cores,Cluster i_cluster){
        cores=i_cores;
        cluster=i_cluster;
        data= new LinkedList<DataBatch>();
    }

    public int getCores(){return cores;}
    public Collection<DataBatch> getData(){return data;}
    public Cluster getCluster(){return cluster;}

    /**
     * @pre data.size()==0 cores>0
     * @inv cores>0
     * @post data.size()>0
     */
    public void receiveData(DataBatch unit){
        data.add(unit);
    }

    /**
     * @pre process was called.
     * @inv cluster!=null
     * @post data.size()=0.
     */
    public void sendData(){
    }

    /**
     * @pre data!=null & cores>0
     * @inv
     * @post data is processed.
     */
    public void process(){}

    /**
     * @pre
     * @inv
     * @post
     * @return
     */
    public long getTicks(){return 0;}

    public boolean isProcessing() {
        return processing;
    }
}