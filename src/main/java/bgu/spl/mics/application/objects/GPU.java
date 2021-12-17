package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.services.GPUService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

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
    private BlockingDeque processed;
    private int processedData = 0;
    private int capacity = 0, time = 1, currentTime = 0;
    private Event event;
    private GPUService GPU;
    private boolean free;

    public GPU(String t) {
        this.setType(t);
        cluster = Cluster.getInstance();
        batches = new LinkedList<DataBatch>();
        processed = new LinkedBlockingDeque();
        free = true;
    }

    //Swe need to fix it.
    public GPU(String type, Event event) {
        this.setType(type);
        this.model = null;
        cluster = Cluster.getInstance();
        this.event = null;
        batches = new LinkedList<DataBatch>();
        processed = new LinkedBlockingDeque();
    }

    public String getType() {
        if (type == Type.RTX3090) return "RTX3090";
        else if (type == Type.RTX2080) return "RTX2080";
        else if (type == Type.GTX1080) return "GTX1080";
        return null;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setEvent(Event e) {
        this.event = e;
    }

    public Model getModel() {
        return model;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public BlockingDeque getProcessed() {
        return processed;
    }

    public int getCapacity() {
        return capacity;
    }

    public Event getEvent() {
        return event;
    }

    public GPUService getGPU() {
        return GPU;
    }

    public Queue<DataBatch> getDataBatchList() {
        return batches;
    }
    //added by bar - this feild is not recognized in the test class.

    public void setType(String t) {
        if (t.compareTo("RTX3090") == 0) {
            type = Type.RTX3090;
            capacity = 32;
        } else if (t.compareTo("RTX2080") == 0) {
            type = Type.RTX2080;
            capacity = 16;
        } else if (t.compareTo("GTX1080") == 0) {
            type = Type.GTX1080;
            capacity = 8;
        }
    }

    /**
     * @pre batch!=null
     * @inv batches!=null.
     * @post batches.size()--.
     */
    public void sendToCluster() {
        if (!batches.isEmpty())
            cluster.addUnProcessed(batches.remove());
    }

    /**
     * @pre model.data!=null
     * @inv
     * @post All the data is stores in one of the data batch.
     */
    public void divide() {
        for (int i = 1; i <= model.getData().getSize() / 1000; i++) {
            DataBatch dataBatch = new DataBatch(model.getData(), i * 1000);
            dataBatch.setGpuIndex(cluster.findGPU(this));
            batches.add(dataBatch);
        }
        for (int i = 0; i < capacity; i++) {
            sendToCluster();
        }
    }

    public String getName() {
        return GPU.getName();
    }

    /**
     * @pre batches!=null
     * @inv model.status="Training".
     * * @post model.status = "Trained".
     */
    public void train(DataBatch unit) {
        model.setStatus(Model.status.Training);
        switch (type) {
            case RTX3090:
                if (time - currentTime == 1) {
                    subTrain(1);
                }
            case RTX2080:
                if (time - currentTime == 2) {
                    subTrain(2);
                }
            case GTX1080:
                if (time - currentTime == 1) {
                    subTrain(4);
                }
        }
        if (processedData * 1000 >= model.getData().getSize()) {
            model.endTraining();
            GPU.completeTrain(event, model);
        }
    }

    public void subTrain(int ticks) {
        free = true;
        processedData++;
        cluster.getStatistics().setUnit_used_gpu(ticks);
        cluster.getStatistics().setNumber_of_DB(1);
        processed.poll();
        if (!batches.isEmpty())
            sendToCluster();
    }

    /**
     * @pre
     * @inv
     * @post batches!=null
     */

    public void receiveFromCluster(DataBatch unit) {
        processed.add(unit);
        if (free) {
            setCurrentTime();
            free = false;
            train(unit);
        }
    }

    public void addTime() {
        time++;
        if (!free) {
            train((DataBatch) processed.peek());
        } else if (processed != null && !processed.isEmpty()) {
            free = false;
            setCurrentTime();
            train((DataBatch) processed.peek());
        }
    }

    private void setCurrentTime() {
        currentTime = time;
    }

    public int getTicks() {
        return time;
    }

    public void test(Model model) {
        System.out.println("Start testing");
        double rand = Math.random();
        switch (model.getStudent().getStatus()) {
            case PhD:
                if (rand >= 0.8) {
                    model.setResult("Good");
                } else
                    model.setResult("Bad");
            case MSc:
                if (rand >= 0.6) {
                    model.setResult("Good");
                } else
                    model.setResult("Bad");
        }
        model.Tested();
    }

    public void setGPU(GPUService s) {
        this.GPU = s;
    }
}