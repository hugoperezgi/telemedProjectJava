package controllers;

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
    public static Query handle_sendReportQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_createUserQuery(Query clientQuery){
        Query q = new Query();
        try {
            //TODO Check duplicate username
            ServerThread.sql.addUser(clientQuery.getUser());
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }
    public static Query handle_editUserQuery(Query clientQuery){
        Query q = new Query();
        try {
            if(clientQuery.getUser().getUsername()!=null){
                //TODO Check duplicate username
                ServerThread.sql.changeUserName(clientQuery.getUser().getUserID(), clientQuery.getUser().getUsername());
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
            // ServerThread.sql. TODO QUERY
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
    public static Query handle_checkRealTimeQuery(Query clientQuery){ //TODO fml
        Query q = new Query();
        try {
            // ServerThread.sql.
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }
    public static Query handle_showAllUsersQuery(Query clientQuery){
        Query q = new Query();
        try {
            // ServerThread.sql.us TODO QUERY
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }
    public static Query handle_showAllWorkersQuery(Query clientQuery){
        Query q = new Query();
        try {
            // ServerThread.sql. TODO QUERY
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }
    public static Query handle_createPatientQuery(Query clientQuery){
        Query q = new Query();
        try {
            Patient p = clientQuery.getPatient();
            ServerThread.sql.addPatient(p);
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
            // ServerThread.sql.worker TODO QUERY
            q.construct_Control_Query("Success");
        } catch (Exception e) {
            q.construct_Control_Query("Error");
        }
        return q;
    }

    public static Query notAValidQueryType(){
        Query q = new Query();
        q.construct_Control_Query("Wrong query format");
        return q;
    }

    public static Query notAValidQueryFormat(){
        Query q = new Query();
        q.construct_Control_Query("Not a valid query type");
        return q;
    }

}
