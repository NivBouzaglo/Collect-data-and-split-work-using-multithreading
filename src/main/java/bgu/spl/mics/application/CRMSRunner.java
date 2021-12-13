package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        //String p = args[0];
        //Reading json file
        Cluster cluster = new Cluster();
        Reader reader =null;
        Gson g = new Gson();
        try {
            reader = Files.newBufferedReader(Paths.get("example_input.json"));
        } catch (IOException ignored) {}

        List<Student> students= new LinkedList<Student>();
        JsonParser parser = new JsonParser();
        JsonElement tree= parser.parse(reader);
       // JsonArray arr = tree.getAsJsonArray();
        JsonObject obj = tree.getAsJsonObject();
        JsonArray arr = obj.get("Students").getAsJsonArray();
        for (JsonElement e: arr){
            JsonObject object = e.getAsJsonObject();
            students.add(new Student(object.get("name").getAsString(),
                    object.get("department").getAsString(),object.get("status").getAsString()));
            JsonArray models = object.get("models").getAsJsonArray();
            LinkedList<Model> e_models= new LinkedList<Model>();
            for (JsonElement m: models ){
                JsonObject model_object = e.getAsJsonObject();
                Data data = new Data(model_object.get("type").getAsString(),model_object.get("size").getAsInt());
                e_models.add(new Model(students.get(students.size()-1),data,model_object.get("name").getAsString()));
            }
            students.get(students.size()-1).setModels(e_models);
        }
       JsonArray gpus = obj.get("GPUS").getAsJsonArray();
        LinkedList<GPU> gpus1 = new LinkedList<GPU>();
        for (JsonElement e : gpus){
            JsonObject object = e.getAsJsonObject();
            gpus1.add(new GPU(object.getAsString()));
        }
        cluster.setGpu(gpus1);
        JsonArray cpus = obj.get("CPUS").getAsJsonArray();
        LinkedList<CPU> cpus1 = new LinkedList<CPU>();
        for (JsonElement e : cpus){
            JsonObject object = e.getAsJsonObject();
            cpus1.add(new CPU(object.getAsInt()));
        }
        cluster.setCpu(cpus1);
//Continue from Conferences


    }
}
