package controllers;

import java.net.InetSocketAddress;
import java.util.List;

import entities.*;

public class DoctorLogic {
    //Check RT params

    /** <b>Returns:</b> <p>{@code List<Patient>} if query was successful <p>{@code null} if something went wrong */
    public static List<Patient> showMyPatients(Integer workerId){
        Query q = new Query();
        q.construct_ShowMyPatients_Query(workerId);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if(srvResponse.getQueryType()==9){
            return srvResponse.getPatient_List();
        }
        return null;
    }

    /** <b>Returns:</b> <p>{@code List<MedicalTest>} if query was successful <p>{@code null} if something went wrong */
    public static List<MedicalTest> showClinicalHist(Integer patientId){
        Query q = new Query();
        q.construct_ShowClinical_Query(patientId);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if(srvResponse.getQueryType()==10){
            return srvResponse.getMedicalTest_List();
        }
        return null;
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if something went wrong */
    public static int editReport(Integer reportId, String doctComments){
        Query q = new Query();
        q.construct_EditReport_Query(reportId, doctComments);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();
    
        Query srvResponse = ClientThread.getServerResponse();
        if(srvResponse.getQueryType()==1){
            if(srvResponse.getControlMsg().contains("Success")){
                return 0;
            }
        } 
        return -1;
    }

    /** <b>Returns:</b> <p>{@code 0} if you're an stalker <p>{@code -1} if something went wrong */
    public static int showRTparams(Integer patientUserId){
        Query q = new Query();
        fml fuck = new fml();
        q.construct_CheckRealTime_Query(patientUserId,(InetSocketAddress) fml.setUpParamsSCK());
        fuck.setDaemon(true);
        fuck.start();
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if(srvResponse.getQueryType()==1){
            if(srvResponse.getControlMsg().contains("AddedToStalkers")){
                return 0;
            }
        } 
        return -1;
    }

    /**Returns the patientID of the current user*/
    public static Worker getMyself(){
        Query q = new Query();
        q.construct_GetMyself_Query();
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if (srvResponse.getQueryType()==22) {
            return srvResponse.getWorker();
        }
        return null;        
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if something went wrong */
    public static int editMyself(Worker w){
        Query q = new Query();
        q.construct_EditWorker_Query(w.getUserID(),w.getName(),w.getSurname());
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse=ClientThread.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }
        return -1;
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if username is already taken <p>{@code -2} if something went wrong */
    public static int editLogin(Integer userId, String user, byte[] psw){
        Query q = new Query();
        q.construct_EditUser_Query(userId, user, psw);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse=ClientThread.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }else if(srvResponse.getControlMsg().contains("UsernameTaken")) {
            return -1;
        }
        return -2;
    }

}
