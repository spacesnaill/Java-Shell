package edu.frostburg.cosc460;
/**
 * Created by Patrick
 */

import java.io.*;
import java.util.ArrayList;

public class Shell {

    public static void main(String[] args) throws java.io.IOException {
        String currentDirectory = System.getProperty("user.dir");
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in)); //setting up a way to read the console
        ArrayList<String> historyArray = new ArrayList<>(); //where the history since the shell was invoked is kept

        //we break out with <control><C>
        while(true) {
            //reading what the user enters
            System.out.print(currentDirectory + ">");
            commandLine = console.readLine();


            //if the user just pressed return and that's it, keep looping
            if(commandLine.equals("")){
                continue;
            }
            if(commandLine.toLowerCase().equals("exit")){
                System.out.println("Thank you for using this program. Goodbye.");
                break;
            }
            if(commandLine.toLowerCase().equals("quit")){
                System.out.println("Thank you for using this program. Goodbye.");
                break;
            }
            if(commandLine.toLowerCase().equals("history")){
                for(int i = 0; i < historyArray.size(); i++){
                    System.out.println(i+" "+ historyArray.get(i)); //prints out the history in an orderly manner
                }
                commandLine = console.readLine(); //ask for another command before continuing
                //double !! gives the last command and !leave quits the history command
                if(commandLine.equals("!leave".toLowerCase())){
                    System.out.println("Exiting history");
                    continue;
                }
                if(commandLine.equals("!!")){
                    try {
                        commandLine = historyArray.get(historyArray.size() - 1);
                    }
                    catch(IndexOutOfBoundsException e){
                        System.out.println("Error, no previous command");
                        continue;
                    }
                }
                else{
                    try{
                        int i = Integer.parseInt(commandLine.substring(1));
                        commandLine = historyArray.get(i);
                    }
                    catch(IndexOutOfBoundsException e){
                        System.out.println("There is no such command in your history with that number");
                    }
                    catch(NumberFormatException e){
                        System.out.println("Please enter ! followed by the number of the command you wish to execute. Or simply type !! for the most recent command");
                    }
                }
            }//end of History section
            historyArray.add(commandLine); //adding commands to the history

            //outline of the steps:
            //(1)Parse the input to obtain the command and parameters
            //(2)Create a ProcessBuilder object
            //(3)State the process
            //(4)obtain the output stream
            //(5)output the contents returned by the command

            //(1)
            ArrayList<String> parameters = new ArrayList<>(5); //parameters of the command entered into the console
            String[] parsed = commandLine.split("\\s+");
            for(int i = 0; i < parsed.length; i++){
                parameters.add(parsed[i]);
            }

            //setting directory

            String oldDirectory = currentDirectory; //this will hold the old directory before it is changed in case the directory is invalid
            if(parameters.get(0).equals("cd")){
                if(commandLine.equals("cd")){
                    currentDirectory = System.getProperty("user.home");
                    System.out.println("Sent to the home directory: " + System.getProperty("user.home")); //sets the current directory the home directory if only cd is typed
                    continue;
                }
                else {
                    currentDirectory = currentDirectory + "/" + parameters.get(1); //sets the currentDirectory variable to what was given regardless of if it is valid or not
                }
            }
            File f = new File(currentDirectory);


            //(2)
            ProcessBuilder processBuilder = new ProcessBuilder(parameters);
            if(f.exists() && f.isDirectory()) {
                processBuilder.directory(f);
            }
            else{
                System.out.println("The directory "+currentDirectory+" does not exist. Type ls and then try a directory from that list."); //tells the user their directory is invalid
                currentDirectory = oldDirectory;
                continue;
            }

            if(parameters.get(0).equals("cd")){
                continue; //skip through the rest of this if the command was cd, since it's pointless to run it through just to get a false-positive error
            }

            //we see if the command is valid
            try {
                //if the process involves changing a directory, this must be done in house with .directory
                /*
                if(parameters.get(0).equals("cd")){
                    if(commandLine.equals("cd")){
                        File f = new File(System.getProperty("user.home"));
                        processBuilder.directory(f);
                        System.out.println("Sent to the home directory: " + System.getProperty("user.home"));
                    }
                    else {
                        File f = new File(parameters.get(1));
                        processBuilder.directory(f);
                        System.out.println("Directory Changed");
                    }
                }*/

                //(3)

                Process process = processBuilder.start();


                //(4)
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                //(5)
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                br.close();
            }
            // if the command is not valid, we provide the user an error message
            catch(IOException e){
                System.out.println("Sorry but " + " \" " + commandLine + " \" " + " is not a valid command. Please try again.");
            }

        }//end of while loop
    }//end of main method


}
