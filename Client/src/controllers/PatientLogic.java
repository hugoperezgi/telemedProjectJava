package controllers;

import java.sql.Date;
import java.util.List;

import bitalino.*;
import entities.MedicalTest;
import entities.Patient;

public class PatientLogic {
    

    /**
     * Used to connect and send data from a bitalino device to the server. <p>
     * @param channels - int array with the analog channels to be received, Must be on order {0,1,2,3,4,5}
     * @return Return codes:
     * <p>{@code -1} - Bitalino error //TODO actually filter the error codes
     * <p>{@code -2} - ?
     * <p>{@code -3} - Something went wrong while adding the parameters to the test.
     * <p>{@code -4} - Server had a fucking stroke
     * <p>{@code -5} - Parameter missmatch with server
     * <p>{@code 0} - Success.
     */
    public static Integer sendBitalinoData(String macAddrss,int[]channels, Integer reportID){

        try {

            BITalino biTalino = new BITalino();
            
            int sr = 10;
            biTalino.open(macAddrss, sr);
            
            biTalino.start(channels);

            Frame[] frame;
            int block_size=10;
            String[] parametersFromChannels= new String[6];

            String s=null;

            for (int j = 0; j < 10000000; j++) {

                //Each time read a block of 10 samples 
                frame = biTalino.read(block_size);

                //Format the samples
                for (int i = 0; i < frame.length; i++) {

                    for(int k=0; k<channels.length;k++){

                        if(i==0){
                            parametersFromChannels[channels[k]]=""+frame[i].analog[k]+" ";
                        }else{
                            parametersFromChannels[channels[k]]+=frame[i].analog[k]+" ";
                        }

                    }

                }

                s=sendReportParameters(reportID, parametersFromChannels);
                    //Send sample block to the server

                if(s.contains("ACK")){ //Check server ACK for correct transmission of sample block
                    String[] strings=s.split(":");
                    for (int i = 0; i < parametersFromChannels.length; i++) {
                        if(Integer.parseInt(strings[i+1])!=parametersFromChannels[i].length()){   return -5;   }
                    }
                }else{
                    return -4;
                }
            }

            s=sendReportParameters(reportID, null); 
                //Tell the server were donezo

            biTalino.stop();

            if(s.contains("Success")){ //Check for control msg from server
                return 0;
            }else{ 
                return -3;
            }


        } catch (BITalinoException e) {
            return -1;
        } catch(Throwable e){
            return -2;
        }

    }

    private static String sendReportParameters(Integer testID, String[] params){
        Query q = new Query();
        q.construct_SendReport_Query(testID, params); 
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        return ClientThread.getServerResponse().getControlMsg();
    }

    /**
     * Used to create a patient report. Returns the {@code reportId} of the report created.
     * <p>Check return for possible errors:
     * <p>{@code -1} - Error creating the report
     * <p>{@code -2} - Server Error, unknown
     */
    public static Integer createReport(Integer patientId, String patientComments, Date date){
        Query q = new Query();
        q.construct_SendReport_Query(patientId, patientComments, date);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query serverResponse = ClientThread.getServerResponse();
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

    /**Returns the patientID of the current user*/
    public static Patient getMyself(){
        Query q = new Query();
        q.construct_GetMyself_Query();
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if (srvResponse.getQueryType()==22) {
            return srvResponse.getPatient();
        }
        return null;        
    }

    /** return 0 if edit was successful, -1 if something went wrong */
    public static int editMyself(Patient p){
        Query q = new Query();
        q.construct_EditPatient_Query(p);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse=ClientThread.getServerResponse();
        if(srvResponse.getControlMsg().contains("Success")){
            return 0;
        }
        return -1;
    }

    public static List<MedicalTest> checkMyReports(Integer patientID){
        Query q = new Query();
        q.construct_ShowClinical_Query(patientID);
        ClientThread.setClientQuery(q);
        ClientThread.sendQuery();

        Query srvResponse = ClientThread.getServerResponse();
        if(srvResponse.getQueryType()!=10){
            return null;
        }else{
            return srvResponse.getMedicalTest_List();
        }
    }

}
