package bgu.spl.mics.application.objects;

import static bgu.spl.mics.application.objects.Data.Type.Images;
import static bgu.spl.mics.application.objects.Data.Type.Text;

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

    public Data(String t, int dataSize) {
        this.setType(t);
        this.processed = 0;
        this.size = dataSize;
    }

    public int getSize() {
        return size;
    }

    public void proccesed(int dataBatchSize) {
        this.processed = dataBatchSize + this.processed;
    }

    public void setType(String t) {
        if (t.compareTo("images") == 0)
            this.type = Type.Images;
        else if (t.compareTo("text") == 0)
            this.type = Text;
        else if (t.compareTo("tabular") == 0)
            this.type = Type.Tabular;
    }

    public Type getType() {
        if (type == Text)
            return Text;
        else if (type == Images)
            return Images;
        else
            return Type.Tabular;
    }
}