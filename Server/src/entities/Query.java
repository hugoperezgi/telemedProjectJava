package entities;

import java.io.Serializable;
// import java.sql.Date;
// import java.util.List;
import java.sql.Date;

public class Query implements Serializable {
    static final long serialVersionUID = 42L;

    private byte queryType;

    private String controlMsg=null;

    private MedicalTest[] medicalTest_List=null;
    private MedicalTest medicalTest=null;
    private String paramString=null;
    private Integer currentTest=null;

    private User[] user_List=null; 
    private User user=null;
    private Integer userToDelete=null;

    private Patient[] patient_List=null; 
    private Patient patient=null;
    private Integer checkPatient=null;

    private Worker[] worker_List=null; 
    private Worker worker=null;

        
    Query(){}

    /**
     * Constructor for Query Type 0 <b>Log in</b> <p>
     * Log in query, expected a control response with the role of the user
     * {@code Client -> Server} <p>
     */
    public void construct_LogIn_Query(String name, byte[] psw){
        this.queryType = (byte) 0;
        this.user= new User(name, psw);
    }

    /**
     * Constructor for Query Type 1 <b>Control</b> <p>
     * Control responses sent by server to client
     * {@code Server -> Client} <p>
     */
    public void construct_Control_Query(String msg){
        this.queryType = (byte) 1;
        this.controlMsg=msg;
    }

    /**
     * Constructor for Query Type 2 <b>Send Report</b> <p>
     * Send a report, expected a control response with the reportID
     * {@code Client -> Server} <p>
     */
    public void construct_SendReport_Query(Integer patientId, String symptoms, Date date){
        this.queryType = (byte) 2;
        this.medicalTest = new MedicalTest(patientId, symptoms, date);
    }

    /**
     * Constructor for Query Type 2 <b>Send Report</b> <p>
     * Keep sending parameter data to server. Parameters = null when finished; expected control response once finished.
     * {@code Client -> Server} <p>
     */
    public void construct_SendReport_Query(Integer testID, String parameters){
        this.queryType = (byte) 2;
        this.currentTest=testID;
        this.paramString=parameters;
    }

    /**
     * Constructor for Query Type 3 <b>Create user</b> <p>
     * Create user, expect a control response <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CreateUser_Query(String user, byte[] psw, Integer role){
        this.queryType = (byte) 3;
        this.user = new User(user, psw, role);
    }

    /**
     * Constructor for Query Type 4 <b>Edit user</b> <p>
     * Edit a user info, expect control response <p>
     * {@code Client -> Server} <p>
     */
    public void construct_EditUser_Query(Integer userID, String user, byte[] psw, Integer role){
        this.queryType = (byte) 4;
        this.user = new User(userID,user,psw,role);
    }

    /**
     * Constructor for Query Type 5 <b>Delete user</b> <p>
     * Delete a given user from the userbase, expected a control response <p>
     * {@code Client -> Server} <p>
     */
    public void construct_DeleteUser_Query(Integer userID){
        this.queryType = (byte) 5;
        this.userToDelete=userID;
    }

    /**
     * Constructor for Query Type 6 <b>Show all patients</b> <p>
     * Ask server for a list of all the patients <p>
     * {@code Client -> Server} <p>
     */
    public void construct_ShowAllPatients_Query(){
        this.queryType = (byte) 6;
    }

    /**
     * Constructor for Query Type 7 <b>Show clinical history</b> <p>
     * Ask server for all tests of a given patient <p>
     * {@code Client -> Server} <p>
     */
    public void construct_ShowClinical_Query(Integer patientID){
        this.queryType = (byte) 7;
        this.checkPatient=patientID;
    } 

    /**
     * Constructor for Query Type 8 <b>Edit report</b> <p>
     * Send updated comments for a given patient report. <p>
     * {@code Client -> Server} <p>
     */
    public void construct_EditReport_Query(Integer reportID,String doctorComments){
        this.queryType = (byte) 8;
        this.medicalTest = new MedicalTest(reportID, doctorComments);
    } 

    /**
     * Constructor for Query Type 9 <b>Send all patients</b> <p>
     * Response to query 6 of client <p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendAllPatients_Query(Patient[] patients){
        this.queryType = (byte) 9;
        this.patient_List=patients;
    } 

    /**
     * Constructor for Query Type 10 <b>Send clinical history</b> <p>
     * Response to query 7 of client <p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendMedicalHistory_Query(MedicalTest[] tests){
        this.queryType = (byte) 10;
        this.medicalTest_List=tests;
    } 

    /**
     * Constructor for Query Type 11 <b>Check Real time</b> <p>
     * Expect the server to tell you in a control query which users are sending stuff, if any <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CheckRealTime_Query(){
        this.queryType = (byte) 11;
    } 

    /**
     * Constructor for Query Type 11 <b>Check Real time</b> <p>
     * Ask server to send you the parameters uploaded by {@code patientId} <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CheckRealTime_Query(Integer patientId){
        this.queryType = (byte) 11;
        this.checkPatient=patientId;
    } 

    /**
     * Constructor for Query Type 12 <b>Send RT params</b> <p>
     * Response to query 11 of client. Will keep sending till patient is finished.<p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendParamsRT_Query(String params){
        this.queryType = (byte) 12;

    } 
    
    /**Type of the query
     * <p>{@code 0} <b>Log in:</b> Cli -> Srv
     * <p>{@code 1} <b>Control:</b> Srv -> Cli (Error/ACK/Confirmations) 
     * <p>{@code 2} <b>Send Report:</b> Cli -> Srv (Either report, report+params or keep sending params) 
     * <p>{@code 3} <b>Create user:</b> Cli -> Srv
     * <p>{@code 4} <b>Edit user:</b> Cli -> Srv
     * <p>{@code 5} <b>Delete user:</b> Cli -> Srv
     * <p>{@code 6} <b>Show all patients:</b> Cli -> Srv
     * <p>{@code 7} <b>Show clinical history:</b> Cli -> Srv
     * <p>{@code 8} <b>Edit report:</b> Cli -> Srv
     * <p>{@code 9} <b>Send all patients:</b> Srv -> Cli
     * <p>{@code 10} <b>Send clinical history:</b> Srv -> Cli
     * <p>{@code 11} <b>Check Real time</b> Cli -> Srv
     * <p>{@code 12} <b>Send RT params</b> Srv -> Cli
    */
    public int getQueryType() {
        return queryType;
    }

    /**
     * Valid for query types:
     * <p> {@code 7} - Send all tests of this patient
     * <p> {@code 11} - Send RT info of this patient
     */
    public int getPatientID(){
        return checkPatient;
    }

    /**
     * Retrieve the testID to send parameters if client choses to do so
     */
    public int getCurrentTest(){
        return currentTest;
    }

    /**
     * Set the current testID so that client can retrieve it to send
     * parameters.<p> Should only be set when sending the ACK control 
     * msg for test created.
     */
    public void setCurrentTest(Integer currentTest) {
        this.currentTest = currentTest;
    }

    public String getControlMsg() {
        return controlMsg;
    }
    public User getUser() {
        return user;
    }
    public MedicalTest getMedicalTest() {
        return medicalTest;
    }
    public String getParamString() {
        return paramString;
    }
    public MedicalTest[] getMedicalTest_List() {
        return medicalTest_List;
    }
    public Patient[] getPatient_List() {
        return patient_List;
    }
    /**
     * Get the used marked for deletion.
     */
    public Integer getUserToDelete() {
        return userToDelete;
    }


    //TODO Edit Worker/create worker
    //TODO Show all users
    //TODO Edit patient/create patient
}
