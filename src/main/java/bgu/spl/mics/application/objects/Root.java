package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Root{
    @JsonProperty("Students")
    public List<Student> students;
    @JsonProperty("GPUS")
    public List<String> gPUS;
    @JsonProperty("CPUS")
    public List<Integer> cPUS;
    @JsonProperty("Conferences")
    public List<ConfrenceInformation> conferences;
    @JsonProperty("TickTime")
    public int tickTime;
    @JsonProperty("Duration")
    public int duration;
}

