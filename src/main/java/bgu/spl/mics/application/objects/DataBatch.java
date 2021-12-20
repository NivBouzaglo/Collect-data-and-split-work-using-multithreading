package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private boolean proccesed;
    private GPU gpu;
    private int ticks;
    private int tickCounter=0;


    public DataBatch(Data d, int start){
        data=d;
        start_index = start;
        this.proccesed = false;
        switch (data.getType()){
            case Text:
                ticks=2;
            case Images:
                ticks= 4;
            case Tabular:
                ticks= 1;
        }
    }

    public void setTickCounter() {
        this.tickCounter++;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public int getTicks() {
        return ticks;
    }


    public void setGpu(GPU gpu) {
        this.gpu = gpu;
    }
    public GPU getGpu(){
        return this.gpu;
    }

    public Data getData() { return data; }
    public boolean isProccesed() { return proccesed; }

    public int getStart_index() { return start_index; }
}
