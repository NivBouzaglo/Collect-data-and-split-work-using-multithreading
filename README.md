# Collect-data-and-split-work-using-multithreading

The idea of the project is to receive a list of student that have a list of models and to training as much as possible data in the models on the time you gave him.
I receive a Json file that include the student, the models i have to train,cpus ,gpus and the time the program will run.
The target of this program is to see the level of the synchronize between the threads , more DataBatch that get trained mean more activity from the gpu and cpu that we get.

To run the projects:
1.mvn clean
2.mvn package -Dmaven.test.skip=true
3.java - cp target/spl221ass2-1.0-jar-with-dependencies.jar bgu.spl.mics.application.CRMSRunner example_input.json
