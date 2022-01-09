# Multithreading

The idea of the project is to recieve a list of student that have a list of models and to training as much as possible data in the models on the time you gave him.
The file that i recieve build as a Json file include the student, the models i have to train,cpus ,gpus and the time that the program will run.
The target of this progrem is to see the level of the synchronize between the threads , more DataBatch that get trained mean more activity from the gpu and cpu that we get.

To run the projects:
1.mvn clean
2.mvn package -Dmaven.test.skip=true
3.java - cp terget/spl221ass2-1.0-jar-with-dependencies.jar bgu.spl.mics.application.CRMSRunner example_input.json
