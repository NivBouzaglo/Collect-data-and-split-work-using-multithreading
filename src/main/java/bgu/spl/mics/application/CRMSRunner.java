package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    //I added throws IOSException ,Hopes its fine
    public static void main(String[] args) throws IOException {
        File output = new File("output.txt");
        try {
            if (!output.createNewFile())
                System.out.println("File is already exists");
        } catch (IOException e) {
            System.out.println("Error while creating output file.");
        }
        Cluster cluster = Cluster.getInstance();
        LinkedList<Student> students = new LinkedList<Student>();
        LinkedList<ConfrenceInformation> conferences = new LinkedList<ConfrenceInformation>();
        LinkedList<GPU> gpus = new LinkedList<GPU>();
        LinkedList<CPU> cpus = new LinkedList<CPU>();
        TimeService timeService = new TimeService();
        readInputFile(args[0], timeService, cluster, students, gpus, cpus, conferences);
        start(timeService, students, gpus, cpus, conferences);
        System.out.println("Finish processing");
        if (timeService.getCurrentTime() == timeService.getDuration())
            writeOutputFile(output, students, conferences, cluster);
    }

    public static void start(TimeService timeService, LinkedList<Student> students, LinkedList<GPU> gpus, LinkedList<CPU> cpus, LinkedList<ConfrenceInformation> conference) {
        LinkedList<Thread> threads = new LinkedList<>();
        int i = 0;
        for (GPU gpu : gpus) {
            GPUService service = new GPUService("GPUId" + i, gpu);
            i++;
            Thread t = new Thread(service);
            threads.add(t);
        }
        for (CPU cpu : cpus) {
            CPUService service = new CPUService("GPUId" + i, cpu);
            i++;
            Thread t = new Thread(service);
            threads.add(t);
        }
        for (ConfrenceInformation c : conference) {
            ConferenceService service = new ConferenceService(c);
            Thread t = new Thread(service);
            threads.add(t);
        }
        for (Student s : students) {
            StudentService service = new StudentService(s);
            Thread t = new Thread(service);
            threads.add(t);
        }
        Thread time = new Thread(timeService);
        //threads.add(time);
        timeService.setThreads(threads);
        time.start();
        for (Thread t : threads) {
            t.start();
        }
        System.out.println("Finish start ");

    }


    //Reading JSON File
    public static void readInputFile(String input, TimeService timeService, Cluster
            cluster, LinkedList<Student> students, LinkedList<GPU> gpus1, LinkedList<CPU> cpus1, LinkedList<ConfrenceInformation> confrenceInformations) throws
            FileNotFoundException {
        Reader reader = null;
        Gson g = new Gson();
        try {
            reader = Files.newBufferedReader(Paths.get(input));
        } catch (IOException ignored) {
        }
        JsonElement tree = JsonParser.parseReader(new FileReader(input));
        JsonObject obj = tree.getAsJsonObject();
        JsonArray arr = obj.get("Students").getAsJsonArray();
        for (JsonElement e : arr) {
            JsonObject object = e.getAsJsonObject();
            students.add(new Student(object.get("name").getAsString(),
                    object.get("department").getAsString(), object.get("status").getAsString()));
            JsonArray models = object.get("models").getAsJsonArray();
            LinkedList<Model> e_models = new LinkedList<Model>();
            for (JsonElement m : models) {
                JsonObject model_object = m.getAsJsonObject();
                Data data = new Data(model_object.get("type").getAsString(), model_object.get("size").getAsInt());
                e_models.add(new Model(students.get(students.size() - 1), data, model_object.get("name").getAsString()));
            }
            students.get(students.size() - 1).setModels(e_models);
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

    public static void writeOutputFile(File file, LinkedList<Student> students, LinkedList<ConfrenceInformation> conferences, Cluster cluster) throws
            IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        //Students
        writer.append("Students: ");
        for (Student student : students) {
            writer.append('\n');
            writer.append(" name: " + student.getName());
            writer.append('\n');
            writer.append(" department: " + student.getDepartment());
            writer.append('\n');
            writer.append(" degree: " + student.getStatus());
            writer.append('\n');
            writer.append(" publication: " + student.getPublications());
            writer.append('\n');
            writer.append(" papersRead: " + student.getPapersRead());
            writer.append('\n');
            writer.append(" TrainedModels:");
            for (Model model : student.getModels()) {
                writer.append('\n');
                writer.append("  name: " + model.getName());
                writer.append('\n');
                writer.append("   Data: ");
                writer.append('\n');
                writer.append("    type: " + model.getData().getType());
                writer.append('\n');
                writer.append("    size: " + model.getData().getSize());
                writer.append('\n');
                writer.append("   status: " + model.getStatus());
                writer.append('\n');
                if (model.isPublish())
                    writer.append("  Published.");
                else
                    writer.append("  Not published.");
            }
            writer.append((char) student.getPapersRead());
        }
        //Conferences
        writer.append('\n');
        writer.append("Conferences: ");
        writer.append('\n');
        for (ConfrenceInformation conf : conferences) {
            writer.append("name: " + conf.getName());
            writer.append('\n');
            writer.append("date: " + conf.getDate());
            writer.append('\n');
            writer.append("Publications: ");
            writer.append('\n');
            for (Model m : conf.getModels()) {
                writer.append("name: " + m.getName());
                writer.append('\n');
                writer.append("  Data: ");
                writer.append('\n');
                writer.append("   type: " + m.getData().getType());
                writer.append('\n');
                writer.append("   size: " + m.getData().getSize());
                writer.append('\n');
                writer.append("  status: " + m.getStatus());
                writer.append('\n');
                writer.append("  result: " + m.getR());
                writer.append('\n');
            }
            //GPU time use.
            writer.append("cpuTimeUsed: " + (char) cluster.getStatistics().getUnit_used_cpu());
            writer.append('\n');
            writer.append("gpuTimeUsed: " + (char) cluster.getStatistics().getUnit_used_gpu());
            writer.append('\n');
            writer.append("batchesProcessed: " + (char) cluster.getStatistics().getNumber_of_DB());
        }
        writer.close();
    }
}
