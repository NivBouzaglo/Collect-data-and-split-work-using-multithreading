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
    private boolean processed;
    private int time = 0;


    public CPU(int i_cores) {
        cores = i_cores;
        cluster = Cluster.getInstance();
        data = new LinkedList<DataBatch>();
        processed = false;
    }

    public int getCores() {
        return cores;
    }

    public Collection<DataBatch> getData() {
        return data;
    }

    public Cluster getCluster() {
        return cluster;
    }

    /**
     * @pre data.size()==0 cores>0
     * @inv cores>0
     * @post data.size()>0
     */
    public void receiveData(DataBatch unit) {
        data.add(unit);
    }

    /**
     * @pre process was called.
     * @inv cluster!=null
     * @post data.size()=0.
     */
    public void sendData(DataBatch unit) {
        cluster.addProcessedData(unit);
        data.remove(unit);
    }

    /**
     * @pre data!=null & cores>0
     * @inv
     * @post data is processed.
     */
    public void process(DataBatch d) {
        if (d.getData().getType() == "Images")
            while (time < 4*cores)
                try {
                    wait();
                }catch (InterruptedException e){}
        if (d.getData().getType() == "Text")
            while (time < 2*cores) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
        if (d.getData().getType() == "Tabular")
            while (time < cores)
                try {
                    wait();
                }catch (InterruptedException e){}
        this.processed = true;
    }

    /**
     * @return
     * @pre
     * @inv
     * @post
     */
    public void addTime() {
        time++;
        check();
    }

    private void check() {

    }

    public boolean isProcessing() {
        return processed;
    }

    public long getTicks() {
        return time;
    }
}