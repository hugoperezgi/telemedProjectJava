package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public class StalkerSocket extends Thread{

    private static ServerSocketChannel paramSCK=null;
    private static ObjectInputStream param_ois;

    public static Boolean running=true;

    /**The params sent by the patient*/
    public static String[] paramStrings=null;


    /**
     * Creates a new TCP socket for this thread
     */
    public static SocketAddress setUpParamsSCK(){
        if(paramSCK==null){
            try {
                paramSCK = ServerSocketChannel.open();
                paramSCK.bind(null);
                // paramSCK.configureBlocking(false);
                return paramSCK.getLocalAddress();
            } catch (IOException e) {
                return null;
            }
        } else {
            try {
                return paramSCK.getLocalAddress();
            } catch (IOException e) {
                return null;
            }
        }
        
    }

    /**
     * Uses the secondary tcp socket to attempt a co
     */
    private static void listenForConnection(){
        try {
            param_ois = new ObjectInputStream(paramSCK.accept().socket().getInputStream());
        } catch (Exception e) {
            param_ois = null;
        }
    }


    private static void party(){
        while(running){
            Query q;
            try {
                q = (Query)param_ois.readObject();
                if(q.getQueryType()==12){
                    paramStrings = q.getParamString();
                }else{
                    running=false;
                }
            } catch (ClassNotFoundException e) {
                running=false;
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                running=false;
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public StalkerSocket(){
        StalkerSocket.running=true;
    }

    @Override
    public void run() {
        listenForConnection();
        party();
    }
}
