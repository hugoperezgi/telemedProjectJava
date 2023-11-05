package controllers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import entities.*;

public class ServerLogic {
    
    public static Query handle_logInQuery(Query clientQuery, ClientHandler c){
        Query q = new Query();
        try {
            User u=clientQuery.getUser();
            User loggedUser=ServerThread.sql.checkPassword(u.getUsername(), u.getPasswordHash());
            if(loggedUser==null){
                q.construct_Control_Query("WrongUserOrPassword");
            }else{
                c.userID = loggedUser.getUserID();
                c.role = loggedUser.getRole();
                q.construct_Control_Query("LoggedIn:"+c.role);
            }
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_sendReportQuery(Query clientQuery, ClientHandler c){
        Query q = new Query();
        Query q2= new Query();
        try {
            MedicalTest t=clientQuery.getMedicalTest();
            if(t==null){
                String[] params=clientQuery.getParamString();
                
                c.receivingDataFromPatient(); 
                    //Adds this thread "c" to the "Transmitting Clients List" so that other threads
                    //(doctor threads) can add their sck to a public list that is then used to send them
                    //the parameters as they're received from patient (in near-RT)

                if(params==null){/*End data transmission + Return*/
                    
                    ServerThread.sql.addParametersToMedicalTest(clientQuery.getCurrentTest(), c.testParamsReceived);
                    q.construct_Control_Query("Success");

                    List<ObjectOutputStream> stalkerList = c.getStalkingDoctors();
                    for (ObjectOutputStream stalker : stalkerList) {
                        try{stalker.close();}catch(Exception e){}
                    }
                        //Close all sockets connected to stalking doctors

                    c.dataTransmissionEnded(); 
                        //Remove the client thread from the list of receiving params list + clear stalker list

                } else {

                    //Store the received data
                    for (int i = 0; i < params.length; i++) {
                        c.testParamsReceived[i]+=params[i];
                    }

                    //Send all received data to the Stalking doctors
                    List<ObjectOutputStream> stalkerList = c.getStalkingDoctors();
                    q2.construct_SendParamsRT_Query(c.testParamsReceived);
                    if(!stalkerList.isEmpty()){
                        for (ObjectOutputStream stalker : stalkerList) {
                            try {
                                stalker.writeObject(q2);
                            } catch (IOException e) {
                                try {
                                    stalkerList.remove(stalker);
                                    stalker.close();
                                } catch (Exception e2) {}
                            }
                        }
                    }

                    //Create the ACK for the received block
                    String s = "ACK";
                    for (int i = 0; i < c.testParamsReceived.length; i++) {
                        s+=(":"+c.testParamsReceived[i].length());
                    }

                    q.construct_Control_Query(s);
                        //Send the ACK to client
                }

            }else{
                Integer i=ServerThread.sql.addMedicalTest(t);
                c.testParamsReceived=new String[6];
                q.construct_Control_Query("TestAdded:"+i);
            }
        } catch (Exception e) {
            q.construct_Control_Query("Error");
            c.dataTransmissionEnded(); //Remove the client thread from the list of receiving params list
        }
        return q;
    }

    public static Query handle_createUserQuery(Query clientQuery){
        Query q = new Query();
        try {
            if(ServerThread.sql.isUserNameFree(clientQuery.getUser().getUsername())){
                Integer userId=ServerThread.sql.addUser(clientQuery.getUser());
                q.construct_Control_Query("Success:"+userId);
            }else{
                q.construct_Control_Query("UsernameTaken");
            }
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_editUserQuery(Query clientQuery){
        Query q = new Query();
        try {
            if(clientQuery.getUser().getUsername()!=null){
                if(ServerThread.sql.isUserNameFree(clientQuery.getUser().getUsername())){
                    ServerThread.sql.changeUserName(clientQuery.getUser().getUserID(), clientQuery.getUser().getUsername());
                }else{
                    q.construct_Control_Query("UsernameTaken");
                    return q;   //If edit user tried to change both username and password, username change wont work unless username is unique
                }
            }
            if(clientQuery.getUser().getPasswordHash()!=null){
                ServerThread.sql.changeUserPassword(clientQuery.getUser().getUserID(), clientQuery.getUser().getPasswordHash());
            }
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_deleteUserQuery(Query clientQuery){
        Query q = new Query();
        try {
            ServerThread.sql.deleteUser(clientQuery.getUserIdToDelete());
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_showAllPatientsQuery(Query clientQuery){
        Query q = new Query();
        try {
            q.construct_SendAllPatients_Query(ServerThread.sql.selectAllPatients());
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_showClinicalHistoryQuery(Query clientQuery){
        Query q = new Query();
        try {
            List<MedicalTest> tests = ServerThread.sql.searchMedicalTestByPatientID(clientQuery.getPatientID());
            q.construct_SendMedicalHistory_Query(tests);
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_editReportQuery(Query clientQuery){
        Query q = new Query();
        try {
            MedicalTest m = clientQuery.getMedicalTest();
            ServerThread.sql.addCommentsToMedicalTest(m.getTestID(), m.getDoctorComments());
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_checkRealTimeQuery(Query clientQuery,ClientHandler c){ 
        Query q = new Query();
        try { 
            if(clientQuery.getPatientID()==null){//Send the doctor which of their patients are sending params.

                List<ClientHandler> sendingPatients=c.getTransmittingPatients();
                // ServerThread.sql.searchPatientByDoctor(ServerThread.sql.selectWorkerByUserId(c.userID).getWorkerID());
                List<Patient> pList=ServerThread.sql.searchPatientByDoctor(c.userID);
                Boolean remove = true;
                for (Patient patient : pList) {
                    for (ClientHandler cThread : sendingPatients) {
                        if(cThread.userID==patient.getUserID()){
                            remove=false;
                        }
                    }
                    if(remove){
                        pList.remove(patient);
                    }else{
                        remove=true;
                    }
                }

                if(pList.isEmpty()){
                    q.construct_Control_Query("NoTarget");
                }else{
                    q.construct_SendAllPatients_Query(pList);
                }

            }else{ //Doctor chose a patient that is sending data

                List<ClientHandler> sendingPatients=c.getTransmittingPatients();
                for (ClientHandler clientHandler : sendingPatients) {
                    if(clientHandler.userID==clientQuery.getPatientID()){
                        clientHandler.addIptoStalkers(clientQuery.getIpAddrss());
                        q.construct_Control_Query("AddedToStalkers");
                        return q;
                    }
                }
            }

        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_showAllUsersQuery(Query clientQuery){
        Query q = new Query();
        try {
            q.construct_SendAllUsers_Query(ServerThread.sql.getAllUsers());
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_showAllWorkersQuery(Query clientQuery){
        Query q = new Query();
        try {
            q.construct_SendAllWorkers_Query(ServerThread.sql.getAllWorkers());
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_createPatientQuery(Query clientQuery){
        Query q = new Query();
        try {
            ServerThread.sql.addPatient(clientQuery.getPatient());
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_editPatientQuery(Query clientQuery){
        Query q = new Query();
        try {
            Patient p = clientQuery.getPatient();
            ServerThread.sql.editPatient(p);
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_createWorkerQuery(Query clientQuery){
        Query q = new Query();
        try {
            ServerThread.sql.addWorker(clientQuery.getWorker());
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_editWorkerQuery(Query clientQuery){
        Query q = new Query();
        try {
            ServerThread.sql.editWorker(clientQuery.getWorker());
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_GetMyself(ClientHandler c){
        Query q = new Query();
        try {
            switch (c.role) {
                case 1:
                    q.construct_Yourself_Worker_Query(ServerThread.sql.selectWorkerByUserId(c.userID));
                    break;
                case 2:
                    q.construct_Yourself_Patient_Query(ServerThread.sql.selectPatientByUserId(c.userID));
                    break;
                default: 
                    q.construct_Control_Query("ADMIN");
                    break;
            }
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query handle_ShowMyPatients(ClientHandler c){
        Query q = new Query();
        try {
            q.construct_SendAllPatients_Query(ServerThread.sql.searchPatientByDoctor(c.userID));
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query forbiddenAccess(){
        Query q = new Query();
        q.construct_Control_Query("forbiddenAccess");
        return q;
    }

    public static Query notAValidQueryFormat(){
        Query q = new Query();
        q.construct_Control_Query("Wrong query format");
        return q;
    }

}
