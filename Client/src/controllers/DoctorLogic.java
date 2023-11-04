package controllers;

import java.util.List;

import entities.*;

public class DoctorLogic {
    //Check RT params

    public static List<Patient> showMyPatients(){
        Query q = new Query();
        //show my patients ffs TODO
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if(srvResponse.getQueryType()==9){
            return srvResponse.getPatient_List();
        }
        return null;
    }

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

    public static void showRTparams(){
        //TODO
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

    /** return 0 if edit was successful, -1 if something went wrong */
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
}
