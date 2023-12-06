package controllers;

import bitalino.*;

public class BIT extends Thread{

    private String macAddrss;
    private boolean Ready;
    private boolean GoAway;
    private int[] channels;
    public static int time;
    private Integer reportID;
    
    public static Integer err;
    public static int timeElapsed;
    public static String errMsg;    
    public static boolean pleaseStopIt=false, running=false;

    public BIT(){
        timeElapsed=0;
        err=null;
    }
    public void setup(String MacAddress,int[] Channels,int RecordTime,Integer ReportId){
        timeElapsed=0;
        err=1;

        this.macAddrss=MacAddress;
        this.channels=Channels;
        this.reportID=ReportId;
        time=RecordTime;
        this.Ready=true;
        // this.interrupt();
    }
    public void gtfo(){
        this.GoAway=true;
    }

    @Override
    public void run() {

        while(!GoAway){
            try {
                Thread.sleep(2500);
                if(Ready){
                    doingTheDoing();
                    Ready=false;
                }
            } catch (InterruptedException e) {
            }

        }

    }

    private static String sendReportParameters(Integer testID, String[] params) throws ClassNotFoundException, DeadServer{
        Query q = new Query();
        q.construct_SendReport_Query(testID, params); 
            ClientLogic.sendQuery(q);

        return ClientLogic.getServerResponse().getControlMsg();
    }
    
    private void doingTheDoing(){
        try {

            BITalino biTalino = new BITalino();
            
            int sr = 1000;
            biTalino.open(macAddrss, sr);
            
            biTalino.start(channels);

            Frame[] frame;
            int block_size=100;
            String[] parametersFromChannels= new String[6];

            String s=null;
            boolean f=true;

            time=time*sr/block_size;
            
            for(timeElapsed = 0; timeElapsed < time; timeElapsed++){
                running=true;

                //Each time read a block of block_size samples 
                frame = biTalino.read(block_size);

                //Format the samples
                for (int i = 0; i < frame.length; i++) {

                    for(int k=0; k<channels.length;k++){

                        if(f){
                            parametersFromChannels[channels[k]]=""+frame[i].analog[k]+" ";
                            f=false;
                        }else{
                            parametersFromChannels[channels[k]]+=frame[i].analog[k]+" ";
                        }

                    }

                }

                if(pleaseStopIt){break;}

            }
            
            BIT.pleaseStopIt=true;

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
            BIT.pleaseStopIt=true;
            errMsg=e.getMessage();
        } catch(Throwable e){
            BIT.pleaseStopIt=true;
            err=-2; 
        } 
    }

}
