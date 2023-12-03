package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientHandler extends Thread {

    private SocketChannel sck;
    private Query clientQuery=null, servQuery=null;

    //Info about the user that this thread is handling
    public Integer userID=null;
    public Integer role=-1;

    //String with all the data that has been received from a patient when 
    //performing a test 
    public String[] testParamsReceived=null;
    
    //Server control stuff
    public static boolean serverRunning=true;
    public static int currentConnectedUsers;

    //Things used for Real time data transfer to "listening" doctors when a patient is
    //sending data to the server and a doctor chooses to check in RT these params

    /** List that includes all theads whose client (patient) is currently sending parameters from a test */
    private static List<ClientHandler> txPatientList = Collections.synchronizedList(new ArrayList<ClientHandler>());
    private static List<ObjectOutputStream> stalkerList = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());

    /**Returns a list with the threads that have an user sending data currently*/
    public List<ClientHandler> getTransmittingPatients() {
        return txPatientList;
    }

    /**Returns a list with the oos of the doctors that wants to peek*/
    public List<ObjectOutputStream> getStalkingDoctors() {
        return stalkerList;
    }

    /**Is the client connected to this thread sending data? */
    private Boolean receivingData=false;

    /**
     * Adds the thread to the {@code txPatientList}, where all
     * patient threads that are receiving data from patient clients are logged.<p>
     * Nothing happens if this function is called again.
     *  (Unless state is resetted via {@code dataTransmissionEnded()})
    */
    public void receivingDataFromPatient(){
        if(!receivingData){
            receivingData=true;
            stalkerList.clear();
            txPatientList.add(this);
        }
    }

    public void addIptoStalkers(InetSocketAddress ip){
        try {
            SocketChannel s = SocketChannel.open(ip);
            stalkerList.add(new ObjectOutputStream(s.socket().getOutputStream()));    
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * Removes {@code self} from {@code txPatientList}, resets state of receiving data.
     * <p> Call {@code receivingDataFromPatient()} to set the state again.
     */
    public void dataTransmissionEnded(){
        receivingData=false;
        stalkerList.clear();
        txPatientList.remove(this);
    }



    public ClientHandler(SocketChannel socket){
        currentConnectedUsers++;
        this.sck=socket;
        this.receivingData=false;
    }

    @Override
    public void run() {
        try {
            
            //Create the input/output streams for our queries
            ObjectInputStream ois = new ObjectInputStream(this.sck.socket().getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(this.sck.socket().getOutputStream());

            while(true){

                //Get data query from client
                clientQuery=(Query) ois.readObject();

                if(clientQuery==null){

                    servQuery = ServerLogic.notAValidQueryFormat();

                }else{

                    switch (role) {
                        case 0: //admin options

                            //Process the query if data type is correct
                            switch (clientQuery.getQueryType()) { 
                                case 0: // login
                                    servQuery = ServerLogic.handle_logInQuery(clientQuery,this); //ALL ROLES
                                    break;                
                                case 3: // Create User
                                    servQuery = ServerLogic.handle_createUserQuery(clientQuery); //ADMIN
                                    break;                
                                case 4: // Edit User
                                    servQuery = ServerLogic.handle_editUserQuery(clientQuery); //ADMIN, SELF
                                    break;                
                                case 5: // Delete User
                                    servQuery = ServerLogic.handle_deleteUserQuery(clientQuery); //ADMIN, SELF
                                    break;                
                                case 6: // Show All patients
                                    servQuery = ServerLogic.handle_showAllPatientsQuery(clientQuery); //ADMIN
                                    break;                
                                case 13: // Show All users
                                    servQuery = ServerLogic.handle_showAllUsersQuery(clientQuery); //ADMIN
                                    break;                
                                case 14: // Show All workers
                                    servQuery = ServerLogic.handle_showAllWorkersQuery(clientQuery); //ADMIN
                                    break;                
                                case 15: // Create patient
                                    servQuery = ServerLogic.handle_createPatientQuery(clientQuery); //ADMIN
                                    break;                
                                case 16: // Edit patient
                                    servQuery = ServerLogic.handle_editPatientQuery(clientQuery); //ADMIN, PATIENT
                                    break;                
                                case 17: // Create worker
                                    servQuery = ServerLogic.handle_createWorkerQuery(clientQuery); //ADMIN
                                    break;                
                                case 18: // Edit worker
                                    servQuery = ServerLogic.handle_editWorkerQuery(clientQuery);//ADMIN, WORKER
                                    break;                
                                case 24: // Get doctor by userId
                                    servQuery = ServerLogic.handle_getDoctor(clientQuery);//ADMIN, WORKER
                                    break;                
                                case 25: // Get patient by userId
                                    servQuery = ServerLogic.handle_getPatient(clientQuery);//ADMIN, WORKER
                                    break;                
                                case 26: // Create Link
                                    servQuery = ServerLogic.handle_createLink(clientQuery);//ADMIN, WORKER
                                    break;                
                                default: // Not a valid Client query type
                                    servQuery = ServerLogic.forbiddenAccess();
                                    break;
                            }
                            break;


                        case 1: //doctor options

                            //Process the query if data type is correct
                            switch (clientQuery.getQueryType()) { 
                                case 0: // login
                                    servQuery = ServerLogic.handle_logInQuery(clientQuery,this); //ALL ROLES
                                    break;                
                                case 4: // Edit User
                                    servQuery = ServerLogic.handle_editUserQuery(clientQuery); //ADMIN, SELF
                                    break;                
                                case 5: // Delete User
                                    servQuery = ServerLogic.handle_deleteUserQuery(clientQuery); //ADMIN, SELF
                                    break;                
                                case 7: // Show Clinical history
                                    servQuery = ServerLogic.handle_showClinicalHistoryQuery(clientQuery); //PATIENT, WORKER
                                    break;                
                                case 8: // Edit Report
                                    servQuery = ServerLogic.handle_editReportQuery(clientQuery); //WORKER
                                    break;                
                                case 11: // Check Real-time
                                    servQuery = ServerLogic.handle_checkRealTimeQuery(clientQuery,this); //WORKER
                                    break;                
                                case 18: // Edit worker
                                    servQuery = ServerLogic.handle_editWorkerQuery(clientQuery);//ADMIN, WORKER
                                    break;                
                                case 21: // Get myself
                                    servQuery = ServerLogic.handle_GetMyself(this); //PATIENT, WORKER
                                    break;     
                                case 23: // Get my patients
                                    servQuery = ServerLogic.handle_ShowMyPatients(this); //WORKER
                                    break;                
                                default: // Not a valid Client query type
                                    servQuery = ServerLogic.forbiddenAccess();
                                    break;
                            }
                            break;


                        case 2://patient options

                            //Process the query if data type is correct
                            switch (clientQuery.getQueryType()) { 
                                case 0: // login
                                    servQuery = ServerLogic.handle_logInQuery(clientQuery,this); //ALL ROLES
                                    break;                
                                case 2: // Send Report
                                    servQuery = ServerLogic.handle_sendReportQuery(clientQuery,this); //PATIENT
                                    break;                
                                case 4: // Edit User
                                    servQuery = ServerLogic.handle_editUserQuery(clientQuery); //ADMIN, SELF
                                    break;                
                                case 5: // Delete User
                                    servQuery = ServerLogic.handle_deleteUserQuery(clientQuery); //ADMIN, SELF
                                    break;                
                                case 7: // Show Clinical history
                                    servQuery = ServerLogic.handle_showClinicalHistoryQuery(clientQuery); //PATIENT, WORKER
                                    break;                
                                case 16: // Edit patient
                                    servQuery = ServerLogic.handle_editPatientQuery(clientQuery); //ADMIN, PATIENT
                                    break;                
                                case 21: // Get myself
                                    servQuery = ServerLogic.handle_GetMyself(this); //PATIENT, WORKER
                                    break;                
                                default: // Not a valid Client query type
                                    servQuery = ServerLogic.forbiddenAccess();
                                    break;
                            }
                            break;


                        default:
                            //Process the query if data type is correct
                            switch (clientQuery.getQueryType()) { 
                                case 0: // login
                                    servQuery = ServerLogic.handle_logInQuery(clientQuery,this); //ALL ROLES
                                    break;                
                                default: // Not a valid Client query type
                                    servQuery = ServerLogic.forbiddenAccess();
                                    break;
                            }
                            break;
                    }

                }

                //Send data query back to client
                oos.writeObject(servQuery);

            }
        
        }catch(IOException e){
            //El cliente ha cerrado la conexión

            try {
                currentConnectedUsers--;
                this.sck.close();
            } catch (Exception e2) {
                // TODO: handle exception
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public boolean equals(Object obj) {
        ClientHandler other = (ClientHandler) obj;
        return (this.threadId()==other.threadId());
    }
}
