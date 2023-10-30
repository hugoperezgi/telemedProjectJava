package controllers;

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
        return q;
    }
    public static Query handle_editUserQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_deleteUserQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_showAllPatientsQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_showClinicalHistoryQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_editReportQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_checkRealTimeQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_showAllUsersQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_showAllWorkersQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_createPatientQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_editPatientQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_createWorkerQuery(Query clientQuery){
        Query q = new Query();
        return q;
    }
    public static Query handle_editWorkerQuery(Query clientQuery){
        Query q = new Query();
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
