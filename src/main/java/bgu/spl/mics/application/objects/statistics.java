package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class statistics {
    private List<String> names;
    private AtomicInteger number_of_DB = new AtomicInteger(0);
    private AtomicInteger unit_used_cpu = new AtomicInteger(0);
    private AtomicInteger unit_used_gpu = new AtomicInteger(0);

    public statistics(){
        names = new LinkedList<>();
    }

    public AtomicInteger getNumber_of_DB() {
        return number_of_DB;
    }

    public AtomicInteger getUnit_used_gpu() {
        return unit_used_gpu;
    }

    public AtomicInteger getUnit_used_cpu() {
        return unit_used_cpu;
    }

    public void addNames(String name) {
        this.names.add(name);
    }

    public void setUnit_used_cpu() {unit_used_cpu.incrementAndGet();}

    public void setUnit_used_gpu() {unit_used_gpu.incrementAndGet();
    }

    public void setNumber_of_DB() {
        number_of_DB.incrementAndGet();
    }
}