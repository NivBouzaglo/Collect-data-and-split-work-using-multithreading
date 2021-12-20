package bgu.spl.mics.application.objects;


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
    //private LinkedBlockingDeque<CPU> cpu;
    private LinkedBlockingDeque<CPU> cpu;
    private List<GPU> gpu;
    private LinkedBlockingDeque<DataBatch> unprocessedData;
    private ConcurrentHashMap<GPU, Queue<DataBatch>> processedData;
    private final statistics statistics;

    private static class SingletonHolder {
        private static Cluster instance = new Cluster();
    }


    public static Cluster getInstance() {
        return Cluster.SingletonHolder.instance;
    }

    /**
     * Retrieves the single instance of this class.
     */

    public Cluster() {
        //cpu = new LinkedBlockingDeque<>();
        cpu = new LinkedBlockingDeque<>();
        gpu = new LinkedList<>();
        statistics = new statistics();
        processedData = new ConcurrentHashMap<>();
        unprocessedData = new LinkedBlockingDeque<>();
    }

    public void setProcessedData() {
        for (GPU g : gpu) {
            processedData.put(g, new LinkedList<>());
        }
    }

    public LinkedBlockingDeque<DataBatch> getUnprocessedData() {
        return unprocessedData;
    }

    public void sendToCPU(DataBatch batch) {
        CPU c = null;
        synchronized (cpu) {
            c = cpu.poll();
        }
        cpu.add(c);
        c.receiveData(batch);
    }

    public List<GPU> getGpu() {
        return gpu;
    }

    public Queue<CPU> getCpu() {
        return cpu;
    }

    public void addProcessedData(DataBatch batch) {
        GPU g = batch.getGpu();
        g.getProcessed().add(batch);
    }


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
