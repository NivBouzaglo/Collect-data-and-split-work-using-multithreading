package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(String  t ,int dataSize ){
        this.setType(t);
        processed=0;
        size= (int)Math.ceil((double)dataSize/1000);
    }
    public int getSize(){return size;}
    public void setType(String t){}
}