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
    private List<CPU> cpu;
    private List<GPU> gpu;
    private ConcurrentHashMap<GPU,Queue<DataBatch>> processedData=null;
    private Queue<DataBatch> endProcessing;
    private static Cluster INSTANCE = null;
    private statistics statistics;
    private int index= -1;


    /**
     * Retrieves the single instance of this class.
     */
    public static Cluster getInstance() {
        //TODO: Implement this
        if (INSTANCE == null)
            INSTANCE = new Cluster();
        return INSTANCE;
    }

    public Cluster() {
        endProcessing = new LinkedBlockingDeque<>();
        cpu = new LinkedList<>();
        gpu = new LinkedList<>();
        statistics = new statistics();
        processedData=new ConcurrentHashMap<>();
    }

    public void setProcessedData() {
        for(GPU g: gpu ){
            processedData.put(g, new LinkedList<DataBatch>());
        }
    }

    public void addUnProcessed(DataBatch batch) {
        sendToCPU(batch);
    }

    public void sendToCPU(DataBatch batch) {
            if (index >= cpu.size() - 1)
                index = 0;
            else
                index++;
            if (index < cpu.size())
            cpu.get(index).receiveData(batch);
    }

    public List<GPU> getGpu() {
        return gpu;
    }
    public List<CPU> getCpu(){return cpu;}

    public void addProcessedData(DataBatch batch) {
        GPU g = gpu.get(batch.getGpuIndex());
        processedData.get(g).add(batch);

    }
    public void askForBatch(GPU g){
        if (processedData.get(g).isEmpty() || g.getProcessed()==null)
            return;
        //else{
        if (g.getProcessed().size()<g.getCapacity() && !processedData.get(g).isEmpty()){
            System.out.println("send to gpu to train");
            DataBatch d = processedData.get(g).poll();
            if (d!=null& g.getProcessed()!=null)
                g.getProcessed().add(d);
        }
        //  }
    }

    public statistics getStatistics() {
        return statistics;
    }

    public void setGpu(List<GPU> gpu) {
        this.gpu = gpu;
        setProcessedData();
    }

    public void setCpu(LinkedList<CPU> cpu) {
        this.cpu = cpu;
    }

    public int findGPU(GPU g) {
        for (int i = 0; i < gpu.size(); i++) {
            if (gpu.get(i).getName().equals(g.getName())) {
                gpu.get(i).setModel(g.getModel());
                return i;
            }
        }
        return 0;
    }
}
