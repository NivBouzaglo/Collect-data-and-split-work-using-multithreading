package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

public class statistics {
    private List<String> names;
    private int number_of_DB = 0;
    private int unit_used_cpu = 0;
    private int unit_used_gpu = 0;

    public statistics(){
        names = new LinkedList<>();
    }

    public int getNumber_of_DB() {
        return number_of_DB;
    }

    public int getUnit_used_gpu() {
        return unit_used_gpu;
    }

    public int getUnit_used_cpu() {
        return unit_used_cpu;
    }

    public void addNames(String name) {
        this.names.add(name);
    }

    public void setUnit_used_cpu(int unit_used_cpu) {
        this.unit_used_cpu = this.unit_used_cpu + unit_used_cpu;
    }

    public void setUnit_used_gpu(int unit_used_gpu) {
        this.unit_used_gpu = this.unit_used_gpu + unit_used_gpu;
    }

    public void setNumber_of_DB(int number_of_DB) {
        this.number_of_DB = number_of_DB + this.number_of_DB;
    }
}
