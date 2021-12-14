package bgu.spl.mics.application.objects;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
	private Queue<DataBatch> endProcessing;
	private Queue<DataBatch> unProcess;
	private static Cluster INSTANCE= null;
	private statistics statistics;



	/**
	 * Retrieves the single instance of this class.
	 */
	public static Cluster getInstance() {
		//TODO: Implement this
		if (INSTANCE == null)
			INSTANCE = new Cluster();
		return INSTANCE;
	}
	public Cluster(){
		endProcessing = new LinkedBlockingDeque<>();
		unProcess = new LinkedBlockingDeque<>();
		cpu = new LinkedList<>();
		gpu = new LinkedList<>();
		statistics = new statistics();
	}
//Receives unprocessed databatch and checks what is his GPU . Then ,sending him to one of the CPUs .
	public void addUnProcessed(DataBatch batch,GPU gp) {
		int index =0;
		for (GPU g: gpu){
			if (g.equals(gp)){
				sendToCPU(batch,index);
				break;
			}
			index++;
		}
		//unProcess.add(batch);
	}

	private void sendToCPU(DataBatch batch, int gpu) {
		int size= cpu.get(0).getData().size();
		int index=0;
		for(int i=0; i< cpu.size();i++){
			if (cpu.get(i).getData().size()<size){
				size = cpu.get(i).getData().size();
				index=i;
			}
		}
		cpu.get(index).receiveData(batch,gpu);
	}
//I dont think we need it.
	public boolean full(){
		for (CPU c : cpu){
			if (!c.isProcessing())
				return false;
		}
		return true;
	}

	public void addProcessedData(DataBatch d,Integer gpuIndex) {
		if (gpu.get(gpuIndex).getProcessed()[gpu.get(gpuIndex).getProcessed().length-1]==null)
			gpu.get()


	}
	public statistics getStatistics(){
		return statistics;
	}

	public void setGpu(List<GPU> gpu) {
		this.gpu = gpu;
	}

	public void setCpu(LinkedList<CPU> cpu) {
		this.cpu = cpu;
	}

}
