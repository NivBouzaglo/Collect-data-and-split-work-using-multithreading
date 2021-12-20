package bgu.spl.mics.application.objects;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int cores;
    private LinkedBlockingDeque<DataBatch> data;
    private Cluster cluster;
    private int time = 0;
    private int ticks = 0;


    public CPU(int i_cores) {
        cores = i_cores;
        cluster = Cluster.getInstance();
        data = new LinkedBlockingDeque<>();
    }

    public int getCores() {
        return cores;
    }

    public Queue<DataBatch> getData() {
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
//    public void receiveData(){
//        if(!cluster.getUnprocessedData().isEmpty()){
//            synchronized (cluster.getUnprocessedData().peek()){
//         data.add(cluster.getUnprocessedData().poll());
//    }}}

    /**
     * @pre process was called.
     * @inv cluster!=null
     * @post data.size()=0.
     */
    public void sendData(DataBatch unit) {
        cluster.getStatistics().setNumber_of_DB();
        ticks=0;
        cluster.addProcessedData(unit);
    }


    /**
     * @pre data!=null & cores>0
     * @inv
     * @post data is processed.
     */
    /**
     * @return
     * @pre
     * @inv
     * @post
     */
    public void addTime() {
        if (!data.isEmpty()) {
            if (ticks < data.peek().getTicks() * (32 / cores))
                ticks++;
            else
                sendData(data.poll());
            cluster.getStatistics().setUnit_used_cpu();
        }
    }

    public long getTicks() {
        return time;
    }
}