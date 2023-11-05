package controllers;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.sql.Date;
import java.util.List;

import entities.*;

public class Query implements Serializable {
    static final long serialVersionUID = 42L;

    private byte queryType;

    private String controlMsg=null;

    private List<MedicalTest> medicalTest_List=null;
    private MedicalTest medicalTest=null;
    private String[] paramStrings=null;
    private Integer currentTest=null;

    private List<User> user_List=null; 
    private User user=null;
    private Integer userIdToDelete=null;

    private List<Patient> patient_List=null; 
    private Patient patient=null;
    private Integer checkPatient=null;

    private List<Worker> worker_List=null; 
    private Worker worker=null;

    private InetSocketAddress ipAddrss=null;
        
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
        this.paramStrings=null;
    }

    /**
     * Constructor for Query Type 2 <b>Send Report</b> <p>
     * Keep sending parameter data to server. Parameters = null when finished; expected control response once finished.
     * {@code Client -> Server} <p>
     */
    public void construct_SendReport_Query(Integer testID, String[] parameters){
        this.queryType = (byte) 2;
        this.medicalTest=null;
        this.currentTest=testID;
        this.paramStrings=parameters;
    }

    /**
     * Constructor for Query Type 3 <b>Create user</b> <p>
     * Create user, expect a control response with userID <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CreateUser_Query(String user, byte[] psw, Integer role){
        this.queryType = (byte) 3;
        this.user = new User(user, psw, role);
    }

    /**
     * Constructor for Query Type 4 <b>Edit user</b> <p>
     * Edit a user info, expect control response <p>
     * userID field is required, set unchanged fields as {@code null}<p>
     * {@code Client -> Server} <p>
     */
    public void construct_EditUser_Query(Integer userID, String user, byte[] psw){
        this.queryType = (byte) 4;
        Integer role=null;
        this.user = new User(userID,user,psw,role);
    }

    /**
     * Constructor for Query Type 5 <b>Delete user</b> <p>
     * Delete a given user from the userbase, expected a control response <p>
     * {@code Client -> Server} <p>
     */
    public void construct_DeleteUser_Query(Integer userID){
        this.queryType = (byte) 5;
        this.userIdToDelete=userID;
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
     * Constructor for Query Type 9 <b>Send all users</b> <p>
     * Response to query 13 of client <p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendAllUsers_Query(List<User> users){
        this.queryType = (byte) 9;
        this.user_List=users;
    } 

    /**
     * Constructor for Query Type 10 <b>Send clinical history</b> <p>
     * Response to query 7 of client <p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendMedicalHistory_Query(List<MedicalTest> tests){
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
     * Ask server to send the parameters uploaded by {@code userIdOfPatient} to {@code ip} <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CheckRealTime_Query(Integer userIdOfPatient, InetSocketAddress ip){
        this.queryType = (byte) 11;
        this.checkPatient=userIdOfPatient;
        this.ipAddrss=ip;
    } 

    /**
     * Constructor for Query Type 12 <b>Send RT params</b> <p>
     * Response to query 11 of client. Will keep sending till patient is finished.<p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendParamsRT_Query(String[] params){
        this.queryType = (byte) 12;
        this.paramStrings=params;
    } 

    /**
     * Constructor for Query Type 13 <b>Show all users</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_ShowAllUsers_Query(){
        this.queryType = (byte) 13;

    } 

    /**
     * Constructor for Query Type 14 <b>Show all workers</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_ShowAllWorkers_Query(){
        this.queryType = (byte) 14;

    } 

    /**
     * Constructor for Query Type 15 <b>Create Patient</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CreatePatient_Query(Patient p){
        this.queryType = (byte) 15;
        this.patient=p;
    } 

    /**
     * Constructor for Query Type 16 <b>Edit Patient</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_EditPatient_Query(Patient p){
        this.queryType = (byte) 16;
        this.patient=p;
    } 

    /**
     * Constructor for Query Type 17 <b>Create Worker</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_CreateWorker_Query(Integer userID, String name, String surname){
        this.queryType = (byte) 17;
        this.worker=new Worker(userID, name, surname);
    } 
    
    /**
     * Constructor for Query Type 18 <b>Create Patient</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_EditWorker_Query(Integer userID, String name, String surname){
        this.queryType = (byte) 18;
        this.worker=new Worker(userID, name, surname);
    } 

    /**
     * Constructor for Query Type 19 <b>Create Patient</b> <p>
     * Response to query 6 of client.<p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendAllPatients_Query(List<Patient> patients){
        this.queryType = (byte) 19;
        this.patient_List=patients;
    } 


    /**
     * Constructor for Query Type 20 <b>Create Patient</b> <p>
     * {@code Server -> Client} <p>
     */
    public void construct_SendAllWorkers_Query(List<Worker> workers){
        this.queryType = (byte) 20;
        this.worker_List=workers;
    } 

    /**
     * Constructor for Query Type 21 <b>Get myself</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_GetMyself_Query(){
        this.queryType = (byte) 21;
    } 
 
    /**
     * Constructor for Query Type 22 <b>Yourself</b> <p>
     * {@code Server -> Client} <p>
     */
    public void construct_Yourself_Patient_Query(Patient p){
        this.queryType = (byte) 22;
        this.patient=p;
    } 
    /**
     * Constructor for Query Type 22 <b>Yourself</b> <p>
     * {@code Server -> Client} <p>
     */
    public void construct_Yourself_Worker_Query(Worker w){
        this.queryType = (byte) 22;
        this.worker=w;
    } 

    /**
     * Constructor for Query Type 23 <b>Get my patients</b> <p>
     * {@code Client -> Server} <p>
     */
    public void construct_ShowMyPatients_Query(Integer workerid){
        this.queryType = (byte) 23;
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
     * <p>{@code 9} <b>Send all users:</b> Srv -> Cli
     * <p>{@code 10} <b>Send clinical history:</b> Srv -> Cli
     * <p>{@code 11} <b>Check Real time:</b> Cli -> Srv
     * <p>{@code 12} <b>Send RT params:</b> Srv -> Cli
     * <p>{@code 13} <b>Show all users:</b> Cli -> Srv
     * <p>{@code 14} <b>Show all workers:</b> Cli -> Srv
     * <p>{@code 15} <b>Create patient:</b> Cli -> Srv
     * <p>{@code 16} <b>Edit patient:</b> Cli -> Srv
     * <p>{@code 17} <b>Create worker:</b> Cli -> Srv
     * <p>{@code 18} <b>Edit worker:</b> Cli -> Srv
     * <p>{@code 19} <b>Send all patients:</b> Srv -> Cli
     * <p>{@code 20} <b>Send all workers:</b> Srv -> Cli
     * <p>{@code 21} <b>Get myself:</b> Cli -> Srv
     * <p>{@code 22} <b>Yourself:</b> Srv -> Cli
     * <p>{@code 23} <b>Get my patients:</b> Cli -> Srv
    */
    public int getQueryType() {
        return queryType;
    }

    /**
     * Valid for query types:
     * <p> {@code 7} - Send all tests of this patient
     * <p> {@code 11} - Send RT info of this patient
     */
    public Integer getPatientID(){
        return checkPatient;
    }

    /**
     * Retrieve the testID to send parameters if client choses to do so
     */
    public Integer getCurrentTest(){
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
    /**
     * Valid for query types:
     * <p> {@code 1} - Log in
     * <p> {@code 3} - Create User
     * <p> {@code 4} - Edit User
     */
    public User getUser() {
        return user;
    }
    /**
     * Valid for query types:
     * <p> {@code 2} - Send Report
     * <p> {@code 8} - Edit report (testId field and Doctor comments field)
     */
    public MedicalTest getMedicalTest() {
        return medicalTest;
    }
    /**
     * Valid for query types:
     * <p> {@code 2} - Send Report
     * <p> If {@code return=null} then transmission has ended
     */
    public String[] getParamString() {
        return paramStrings;
    }
    public List<MedicalTest> getMedicalTest_List() {
        return medicalTest_List;
    }
    public List<Patient> getPatient_List() {
        return patient_List;
    }
    /**
     * Get the usedID marked for deletion.
     */
    public Integer getUserIdToDelete() {
        return userIdToDelete;
    }
    /**
     * Valid for query types:
     * <p> {@code 15} - Create patient
     * <p> {@code 16} - Edit patient
     * <p> {@code 21} - Get Myself
     */
    public Patient getPatient() {
        return patient;
    }

    public List<User> getUser_List() {
        return user_List;
    }

    public List<Worker> getWorker_List() {
        return worker_List;
    }
    /**
     * Valid for query types:
     * <p> {@code 17} - Create worker
     * <p> {@code 18} - Edit worker
     * <p> {@code 21} - Get Myself
     */
    public Worker getWorker() {
        return worker;
    }

    public InetSocketAddress getIpAddrss() {
        return ipAddrss;
    }
}
