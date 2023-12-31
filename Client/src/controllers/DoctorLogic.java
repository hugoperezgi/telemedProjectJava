package controllers;

import java.net.InetSocketAddress;
import java.util.List;

import entities.*;

public class DoctorLogic {
    //Check RT params

    /** <b>Returns:</b> <p>{@code List<Patient>} if query was successful <p>{@code null} if something went wrong 
     * @throws DeadServer, ClassNotFoundException
     */
    public static List<Patient> showMyPatients(Integer workerId) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_ShowMyPatients_Query(workerId);
        ClientLogic.sendQuery(q);

        Query srvResponse = ClientLogic.getServerResponse();
        if(srvResponse.getQueryType()==19){
            return srvResponse.getPatient_List();
        }
        return null;
    }

    /** <b>Returns:</b> <p>{@code List<MedicalTest>} if query was successful <p>{@code null} if something went wrong 
     * @throws DeadServer, ClassNotFoundException
     */
    public static List<MedicalTest> showClinicalHist(Integer patientId) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_ShowClinical_Query(patientId);
        ClientLogic.sendQuery(q);

        Query srvResponse = ClientLogic.getServerResponse();
        if(srvResponse.getQueryType()==10){
            return srvResponse.getMedicalTest_List();
        }
        return null;
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if something went wrong 
     * @throws DeadServer, ClassNotFoundException
     */
    public static int editReport(Integer reportId, String doctComments) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_EditReport_Query(reportId, doctComments);
        ClientLogic.sendQuery(q);
    
        Query srvResponse = ClientLogic.getServerResponse();
        if(srvResponse.getQueryType()==1){
            if(srvResponse.getControlMsg().contains("Success")){
                return 0;
            }
        } 
        return -1;
    }

    /** <b>Returns:</b> <p>{@code 0} if you're an stalker <p>{@code -1} if something went wrong 
     * @throws DeadServer, ClassNotFoundException
     */
    public static int showRTparams(Integer patientUserId) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        StalkerSocket ssck = new StalkerSocket();
        q.construct_CheckRealTime_Query(patientUserId,(InetSocketAddress) StalkerSocket.setUpParamsSCK());
        ssck.setDaemon(true);
        ssck.start();
        ClientLogic.sendQuery(q);

        Query srvResponse = ClientLogic.getServerResponse();
        if(srvResponse.getQueryType()==1){
            if(srvResponse.getControlMsg().contains("AddedToStalkers")){
                return 0;
            }
        } 
        return -1;
    }

    /**Returns the patientID of the current user
     * @throws DeadServer, ClassNotFoundException
     */
    public static Worker getMyself() throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_GetMyself_Query();
        ClientLogic.sendQuery(q);

        Query srvResponse = ClientLogic.getServerResponse();
        if (srvResponse.getQueryType()==22) {
            return srvResponse.getWorker();
        }
        return null;        
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if something went wrong 
     * @throws DeadServer, ClassNotFoundException
     */
    public static int editMyself(Worker w) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_EditWorker_Query(w.getUserID(),w.getName(),w.getSurname());
        ClientLogic.sendQuery(q);

        Query srvResponse=ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }
        return -1;
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if username is already taken <p>{@code -2} if something went wrong 
     * @throws DeadServer, ClassNotFoundException
     */
    public static int editLogin(Integer userId, String user, byte[] psw) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_EditUser_Query(userId, user, psw);
        ClientLogic.sendQuery(q);

        Query srvResponse=ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }else if(srvResponse.getControlMsg().contains("UsernameTaken")) {
            return -1;
        }
        return -2;
    }

}
