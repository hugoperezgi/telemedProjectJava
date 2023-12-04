package controllers;

import bitalino.*;

public class BIT extends Thread{

    private String macAddrss;
    private int[] channels;
    private long time=10000;
    private Integer reportID;
    
    public int err;
    public static boolean pleaseStopit=false;

    public BIT(String MacAddress,int[] Channels,int RecordTime,Integer ReportId){
        this.macAddrss=MacAddress;
        this.channels=Channels;
        this.reportID=ReportId;
        this.time=RecordTime*1000;
    }
    
    @Override
    public void run() {
        try {

            BITalino biTalino = new BITalino();
            
            int sr = 10;
            biTalino.open(macAddrss, sr);
            
            biTalino.start(channels);

            Frame[] frame;
            int block_size=10;
            String[] parametersFromChannels= new String[6];

            String s=null;

            long endT=System.currentTimeMillis()+time;
            
            do{

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

                if(pleaseStopit){break;}

            }while(System.currentTimeMillis()<endT);

            s=sendReportParameters(reportID, parametersFromChannels); 
                //Send Server whole signal batch

            biTalino.stop();

            if(s.contains("Success")){ //Check for control msg from server
                err=0; 
            }else{ 
                err=-3; throw new JustDont();
            }


        } catch (BITalinoException e) {
            err=-1; 
        } catch(Throwable e){
            err=-2; 
        } 
    }

    private static String sendReportParameters(Integer testID, String[] params) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_SendReport_Query(testID, params); 
            ClientLogic.sendQuery(q);

        return ClientLogic.getServerResponse().getControlMsg();
    }
    

}
