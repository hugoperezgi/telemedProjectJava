package controllers;

import bitalino.*;

public class PatientLogic {
    

    /**
     * Used to connect and send data from a bitalino device to the server. <p>
     * @param channels - int array with the analog channels to be received, Must be on order {0,1,2,3,4,5}
     * @return Return codes:
     * <p>{@code -1} - Bitalino error //TODO actually filter the error code
     * <p>{@code -2} - 
     * <p>{@code 0} - Success.
     */
    public static Integer sendBitalinoData(String macAddrss,int[]channels, Integer reportID){

        try {

            BITalino biTalino = new BITalino();
            int rs = 10;
            biTalino.open(macAddrss, rs);
            
            biTalino.start(channels);

            Frame[] frame;
            int block_size=10;
            String[] parametersFromChannels= new String[6];
            String s;

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

                sendReportParameters(reportID, parametersFromChannels);//TODO check for error code from server
                    //Send sample block to the server

            }

            sendReportParameters(reportID, null); //TODO check for error code from server
                //Tell the server were donezo

            biTalino.stop();

            return 0;

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

    public static Integer fuck2(){
        ClientThread
    }


}
