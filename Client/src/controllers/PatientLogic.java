package controllers;

import java.sql.Date;
import java.util.List;


import entities.MedicalTest;
import entities.Patient;

public class PatientLogic {

    public static BIT bThread;

    public static void sendBitalinoData(String macAddrss,int[]channels, Integer reportID, int time){

        bThread.setup(macAddrss,channels,time,reportID);

    }


    /**
     * Used to create a patient report. Returns the {@code reportId} of the report created.
     * <p>Check return for possible errors:
     * <p>{@code -1} - Error creating the report
     * <p>{@code -2} - Server Error, unknown
     * @throws DeadServer
     * @throws ClassNotFoundException
     */
    public static Integer createReport(Integer patientId, String patientComments, Date date) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_SendReport_Query(patientId, patientComments, date);
            ClientLogic.sendQuery(q);

        Query serverResponse = ClientLogic.getServerResponse();
        if(serverResponse==null){return -2;}
        String msg = serverResponse.getControlMsg();
        if(msg.contains("Error")){
            return -1;
        }else{
            String[] s=msg.split(":");
            if(s.length!=2||!s[0].contains("TestAdded")){return -2;}
            return Integer.parseInt(s[1]);
        }
    }

    /**Returns the patientID of the current user
     * @throws DeadServer
     * @throws ClassNotFoundException
     * */
    public static Patient getMyself() throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_GetMyself_Query();
            ClientLogic.sendQuery(q);

        Query srvResponse = ClientLogic.getServerResponse();
        if (srvResponse.getQueryType()==22) {
            return srvResponse.getPatient();
        }
        return null;        
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if something went wrong 
     * @throws DeadServer
     * @throws ClassNotFoundException
     * */
    public static int editMyself(Patient p) throws DeadServer, ClassNotFoundException{
        Query q = new Query();
        q.construct_EditPatient_Query(p);
            ClientLogic.sendQuery(q);

        Query srvResponse=ClientLogic.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }
        return -1;
    }

    /** <b>Returns:</b> <p>{@code 0} if edit was successful <p>{@code -1} if username is already taken <p>{@code -2} if something went wrong 
     * @throws DeadServer
     * @throws ClassNotFoundException 
     **/
    public static int editLogin(Integer userId, String user, byte[] psw) throws ClassNotFoundException, DeadServer{
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

    /** <b>Returns:</b> <p>{@code List<MedicalTest>} if query was successful <p>{@code null} if something went wrong 
     * @throws DeadServer
     * @throws ClassNotFoundException
     * */
    public static List<MedicalTest> checkMyReports(Integer patientID) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_ShowClinical_Query(patientID);
            ClientLogic.sendQuery(q);

        Query srvResponse = ClientLogic.getServerResponse();
        if(srvResponse==null){
            return null;
        }else if(srvResponse.getQueryType()!=10){
            return null;
        }else{
            return srvResponse.getMedicalTest_List();
        }
    }

}
