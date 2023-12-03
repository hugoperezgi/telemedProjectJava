package controllers;

import java.util.List;

import entities.*;

public class AdminLogic { 

    public static List<User> showAllUsers() throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_ShowAllUsers_Query();
        ClientLogic.sendQuery(q);

        return ClientLogic.getServerResponse().getUser_List();
    }

    public static List<Patient> showAllPatients() throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_ShowAllPatients_Query();
        ClientLogic.sendQuery(q);

        return ClientLogic.getServerResponse().getPatient_List();
    }

    public static List<Worker> showAllWorkers() throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_ShowAllWorkers_Query();
        ClientLogic.sendQuery(q);

        return ClientLogic.getServerResponse().getWorker_List();
    }

    public static Integer deleteUser(Integer userId) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_DeleteUser_Query(userId);
        ClientLogic.sendQuery(q);

        Query srvResponse= ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }
        return -1;
    }

    public static Integer createUser(String username, byte[] password, Integer role) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_CreateUser_Query(username, password, role);
        ClientLogic.sendQuery(q);

        Query srvResponse= ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return Integer.parseInt(srvResponse.getControlMsg().split(":")[1]);
        }else if(srvResponse.getControlMsg().contains("UsernameTaken")){
            return null;
        }
        return -1;
    }

    public static Integer createPatient(Patient p) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_CreatePatient_Query(p);
        ClientLogic.sendQuery(q);

        if(ClientLogic.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

    public static Integer createWorker(Integer userid, String name, String surname) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_CreateWorker_Query(userid, name, surname);
        ClientLogic.sendQuery(q);

        if(ClientLogic.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

    public static Integer editPatient(Patient p) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_EditPatient_Query(p);
        ClientLogic.sendQuery(q);

        if(ClientLogic.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

    public static Integer editDoctor(Worker w) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_EditWorker_Query(w.getWorkerID(),w.getName(),w.getSurname());
        ClientLogic.sendQuery(q);

        if(ClientLogic.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

    public static Integer createLink(Integer pId, Integer wId) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_CreateLink_Query(pId,wId);
        ClientLogic.sendQuery(q);

        if(ClientLogic.getServerResponse().getControlMsg().contains("Error")){return null;}

        return 0;
    }

    public static Worker getDoctor(Integer userID) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_GetDoctor_Query(userID);
        ClientLogic.sendQuery(q);

        Query srvResponse= ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg()!=null){return null;}

        return srvResponse.getWorker();
    }
    public static Patient getPatient(Integer userID) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_GetPatient_Query(userID);
        ClientLogic.sendQuery(q);

        Query srvResponse= ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg()!=null){return null;}

        return srvResponse.getPatient();
    }

}
