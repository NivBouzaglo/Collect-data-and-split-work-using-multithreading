package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
    private LinkedBlockingDeque<CPU> cpu;
    private List<GPU> gpu;
    private ConcurrentHashMap<GPU, Queue<DataBatch>> processedData;
    private final statistics statistics;
    private static class SingletonHolder{
        private static Cluster instance = new Cluster();
    }


    public static Cluster getInstance(){return Cluster.SingletonHolder.instance;}
    /**
     * Retrieves the single instance of this class.
     */

    public Cluster() {
        cpu = new LinkedBlockingDeque<>();
        gpu = new LinkedList<>();
        statistics = new statistics();
        processedData = new ConcurrentHashMap<>();
    }

    public void setProcessedData() {
        for (GPU g : gpu) {
            processedData.put(g, new LinkedList<>());
        }
    }

    public void sendToCPU(DataBatch batch) {
        CPU c = null;
        synchronized (cpu) {
            c = cpu.poll();
            cpu.add(c);
        }
            c.receiveData(batch);
    }

    public List<GPU> getGpu() {
        return gpu;
    }

    public Queue<CPU> getCpu() {
        return cpu;
    }

    public void addProcessedData(DataBatch batch) {
        GPU g =batch.getGpu();
        g.getProcessed().add(batch);
    }

//    public void askForBatch(GPU g) {
//        if (g.getProcessed().size() < g.getCapacity() && !processedData.get(g).isEmpty()) {
//            System.out.println("asking for data ");
//            DataBatch d = processedData.get(g).poll();
//            if (d != null & g.getProcessed() != null)
//                g.getProcessed().add(d);
//        }
//    }

    public statistics getStatistics() {
        return statistics;
    }

    public void setGpu(List<GPU> gpu) {
        this.gpu = gpu;
        setProcessedData();
    }

    public void setCpu(LinkedList<CPU> cpu) {
        this.cpu.addAll(cpu);
    }

}
