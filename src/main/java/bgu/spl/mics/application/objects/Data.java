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
        this.processed=0;
        this.size= (int)Math.ceil((double)dataSize/1000);
    }
    public int getSize(){return size;}
    public void proccesed(int dataBatchSize){
        this.processed = dataBatchSize + this.processed;
    }
    public void setType(String t){
        if (t.compareTo("Images") == 0)
            this.type = Type.Images;
        else if (t.compareTo("Text") == 0)
            this.type=Type.Text;
        else if(t.compareTo("Tabular") == 0)
            this.type = Type.Tabular;
    }
}