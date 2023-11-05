package controllers;

import java.util.List;

import entities.*;

public class AdminLogic { 

    public static List<User> showAllUsers(){
        Query q = new Query();
        q.construct_ShowAllUsers_Query();
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        return ClientThread.getServerResponse().getUser_List();
    }

    public static List<Patient> showAllPatients(){
        Query q = new Query();
        q.construct_ShowAllPatients_Query();
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        return ClientThread.getServerResponse().getPatient_List();
    }

    public static List<Worker> showAllWorkers(){
        Query q = new Query();
        q.construct_ShowAllWorkers_Query();
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        return ClientThread.getServerResponse().getWorker_List();
    }

    public static Integer deleteUser(Integer userId){
        Query q = new Query();
        q.construct_DeleteUser_Query(userId);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse= ClientThread.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }
        return -1;
    }

    public static Integer createUser(String username, byte[] password, Integer role){
        Query q = new Query();
        q.construct_CreateUser_Query(username, password, role);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse= ClientThread.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return Integer.parseInt(srvResponse.getControlMsg().split(":")[1]);
        }else if(srvResponse.getControlMsg().contains("UsernameTaken")){
            return null;
        }
        return -1;
    }

    public static Integer createPatient(Patient p){
        Query q = new Query();
        q.construct_CreatePatient_Query(p);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        if(ClientThread.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

    public static Integer createWorker(Integer userid, String name, String surname){
        Query q = new Query();
        q.construct_CreateWorker_Query(userid, name, surname);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        if(ClientThread.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

}
