package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

public class ClientHandler extends Thread {

    private SocketChannel sck;
    private Query clientQuery=null, servQuery=null;

    public Integer userID=null;
    public Integer role=null;
    
    public static boolean serverRunning=true;
    public static int currentConnectedUsers;

    public ClientHandler(SocketChannel socket){
        currentConnectedUsers++;
        this.sck=socket;
    }

    @Override
    public void run() {
        try {

            while(true){

                //Get data query from client
                ObjectInputStream ois = new ObjectInputStream(this.sck.socket().getInputStream());
                clientQuery=(Query) ois.readObject();
                ois.close(); //TODO puede esto tenga q desparecer

                if(clientQuery==null){

                    servQuery = ServerLogic.notAValidQueryFormat();

                }else{
                    //Process the query if data type is correct
                    switch (clientQuery.getQueryType()) { //1 9 10 12 19 20
                        case 0: // login
                            servQuery = ServerLogic.handle_logInQuery(clientQuery,this);
                            break;                
                        case 2: // Send Report
                            servQuery = ServerLogic.handle_sendReportQuery(clientQuery);
                            break;                
                        case 3: // Create User
                            servQuery = ServerLogic.handle_createUserQuery(clientQuery);
                            break;                
                        case 4: // Edit User
                            servQuery = ServerLogic.handle_editUserQuery(clientQuery);
                            break;                
                        case 5: // Delete User
                            servQuery = ServerLogic.handle_deleteUserQuery(clientQuery);
                            break;                
                        case 6: // Show All patients
                            servQuery = ServerLogic.handle_showAllPatientsQuery(clientQuery);
                            break;                
                        case 7: // Show Clinical history
                            servQuery = ServerLogic.handle_showClinicalHistoryQuery(clientQuery);
                            break;                
                        case 8: // Edit Report
                            servQuery = ServerLogic.handle_editReportQuery(clientQuery);
                            break;                
                        case 11: // Check Real-time
                            servQuery = ServerLogic.handle_checkRealTimeQuery(clientQuery);
                            break;                
                        case 13: // Show All users
                            servQuery = ServerLogic.handle_showAllUsersQuery(clientQuery);
                            break;                
                        case 14: // Show All workers
                            servQuery = ServerLogic.handle_showAllWorkersQuery(clientQuery);
                            break;                
                        case 15: // Create patient
                            servQuery = ServerLogic.handle_createPatientQuery(clientQuery);
                            break;                
                        case 16: // Edit patient
                            servQuery = ServerLogic.handle_editPatientQuery(clientQuery);
                            break;                
                        case 17: // Create worker
                            servQuery = ServerLogic.handle_createWorkerQuery(clientQuery);
                            break;                
                        case 18: // Edit worker
                            servQuery = ServerLogic.handle_editWorkerQuery(clientQuery);
                            break;                
                        default: // Not a valid Client query type
                            servQuery = ServerLogic.notAValidQueryType();
                            break;
                    }
                }

                //Send data query back to client
                ObjectOutputStream oos = new ObjectOutputStream(this.sck.socket().getOutputStream());
                oos.writeObject(servQuery);
                oos.close(); //TODO idem here


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
}
