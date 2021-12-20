package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.LinkedList;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws IOException {
        FileWriter output = new FileWriter("output.txt");
        Cluster cluster = Cluster.getInstance();
        LinkedList<Student> students = new LinkedList<>();
        LinkedList<ConfrenceInformation> conferences = new LinkedList<>();
        LinkedList<GPU> gpus = new LinkedList<>();
        LinkedList<CPU> cpus = new LinkedList<>();
        TimeService timeService = new TimeService();
        readInputFile(args[0], timeService, cluster, students, gpus, cpus, conferences);
        start(timeService, students, conferences);
        writeOutputFile(output, students, conferences, cluster);
    }

    public static void start(TimeService timeService, LinkedList<Student> students, LinkedList<ConfrenceInformation> conference) {
        LinkedList<Thread> threads = new LinkedList<>();
        int i = 0;
        for (GPU gpu : Cluster.getInstance().getGpu()) {
            GPUService service = new GPUService("GPUId" + i, gpu);
            i++;
            Thread t = new Thread(service);
            threads.add(t);
            t.start();
        }
        i = 0;
        for (CPU cpu : Cluster.getInstance().getCpu()) {
            CPUService service = new CPUService("CPUId" + i, cpu);
            i++;
            Thread t = new Thread(service);
            threads.add(t);
            t.start();
        }
        for (ConfrenceInformation c : conference) {
            ConferenceService service = new ConferenceService(c);
            Thread t = new Thread(service);
            threads.add(t);
            t.start();
        }
        Thread time = new Thread(timeService);
        for (Student s : students) {
            StudentService service = new StudentService(s);
            Thread t = new Thread(service);
            threads.add(t);
            t.start();
        }
        threads.addLast(time);
        time.start();
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {}
        }
    }


    //Reading JSON File
    public static void readInputFile(String input, TimeService timeService, Cluster
            cluster, LinkedList<Student> students, LinkedList<GPU> gpus1, LinkedList<CPU> cpus1, LinkedList<ConfrenceInformation> confrenceInformations) throws
            FileNotFoundException {
        JsonElement tree = JsonParser.parseReader(new FileReader(input));
        JsonObject obj = tree.getAsJsonObject();
        JsonArray arr = obj.get("Students").getAsJsonArray();
        int x = 0;
        for (JsonElement e : arr) {
            JsonObject object = e.getAsJsonObject();
            students.add(new Student(object.get("name").getAsString(),
                    object.get("department").getAsString(), object.get("status").getAsString()));
            JsonArray models = object.get("models").getAsJsonArray();
            LinkedList<Model> e_models = new LinkedList<>();
            for (JsonElement m : models) {
                JsonObject model_object = m.getAsJsonObject();
                Data data = new Data(model_object.get("type").getAsString(), model_object.get("size").getAsInt());
                e_models.add(new Model(students.get(x), data, model_object.get("name").getAsString()));

            }
            students.get(x).setModels(e_models);
            x++;
        }
        JsonArray gpus = obj.get("GPUS").getAsJsonArray();
        for (JsonElement e : gpus) {
            gpus1.add(new GPU(e.getAsString()));
        }
        cluster.setGpu(gpus1);
        JsonArray cpus = obj.get("CPUS").getAsJsonArray();
        for (JsonElement e : cpus) {
            cpus1.add(new CPU(e.getAsInt()));
        }
        cluster.setCpu(cpus1);
        JsonArray conf = obj.get("Conferences").getAsJsonArray();
        for (JsonElement e : conf) {
            JsonObject object = e.getAsJsonObject();
            confrenceInformations.add(new ConfrenceInformation(object.get("name").getAsString(), object.get("date").getAsInt()));
        }
        int ticks = obj.get("TickTime").getAsInt();
        int duration = obj.get("Duration").getAsInt();
        timeService.set(ticks, duration);
    }

    public static void writeOutputFile(FileWriter file, LinkedList<Student> students, LinkedList<ConfrenceInformation> conferences, Cluster cluster) throws
            IOException {
        //Students
        file.write("Students: ");
        for (Student student : students) {
            file.write('\n');
            file.write(" name: " + student.getName());
            file.write('\n');
            file.write(" department: " + student.getDepartment());
            file.write('\n');
            file.write(" degree: " + student.getStatus());
            file.write('\n');
            file.write(" publication: " + student.getPublications());
            file.write('\n');
            file.write(" papersRead: " + student.getPapersRead());
            file.write('\n');
            file.write("   TrainedModels:");
            for (Model model : student.getModels()) {
                if (model.getStatus().compareTo(Model.status.Tested) == 0) {
                    file.write('\n');
                    file.write("            name: " + model.getName());
                    file.write('\n');
                    file.write("            Data: ");
                    file.write('\n');
                    file.write("            type: " + model.getData().getType());
                    file.write('\n');
                    file.write("                   size: " + model.getData().getSize());
                    file.write('\n');
                    file.write("                   status: " + model.getStatus());
                    file.write('\n');
                    file.write("                   result: " + model.getR());
                    file.write('\n');
                }
            }
        }
        //Conferences
        file.write('\n');
        file.write("Conferences: ");
        file.write('\n');
        for (ConfrenceInformation conf : conferences) {
            file.write("name: " + conf.getName());
            file.write('\n');
            file.write("date: " + conf.getDate());
            file.write('\n');
            file.write("Publications: ");
            file.write('\n');
            for (Model m : conf.getModels()) {
                file.write("     name: " + m.getName());
                file.write('\n');
                file.write("     Data: ");
                file.write('\n');
                file.write("         type: " + m.getData().getType());
                file.write('\n');
                file.write("         size: " + m.getData().getSize());
                file.write('\n');
                file.write("     status: " + m.getStatus());
                file.write('\n');
                file.write("     result: " + m.getR());
                file.write('\n');
            }
        }
        //GPU time use.
        file.write("cpuTimeUsed: " + cluster.getStatistics().getUnit_used_cpu());
        file.write('\n');
        file.write("gpuTimeUsed: " + cluster.getStatistics().getUnit_used_gpu());
        file.write('\n');
        file.write("batchesProcessed: " + cluster.getStatistics().getNumber_of_DB());
        file.close();
    }
}
