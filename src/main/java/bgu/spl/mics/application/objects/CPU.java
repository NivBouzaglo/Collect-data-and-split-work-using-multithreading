package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int cores;
    private Queue<DataBatch> data;
    private Cluster cluster;
    private boolean processed;
    private int time = 0;
    private int currentTime = 0;
    private int ticks = 0;


    public CPU(int i_cores) {
        cores = i_cores;
        cluster = Cluster.getInstance();
        data = new LinkedList<>();
        processed = false;
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

    private void setCurrentTime() {
        if (!processed)
            this.currentTime = time;
    }

    /**
     * @pre process was called.
     * @inv cluster!=null
     * @post data.size()=0.
     */
    public void sendData(DataBatch unit) {
        //   System.out.println("Sending from CPU");
        //  processed = true;
        ticks=0;
        if (unit != null) {
            cluster.addProcessedData(unit);
            //if (time-currentTime>= unit.getTicks()*(32/cores)){
            // data.poll();
            //    processed = false;
        }
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
            cluster.getStatistics().setUnit_used_cpu();
            if (ticks < data.peek().getTicks() * (32 / cores))
                ticks++;
            else
                sendData(data.poll());
            }
        }

    public long getTicks() {
        return time;
    }
}