package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

public class fml extends Thread{

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

    /**
     * Returns: 
     * <p>{@code null} if connection was closed by server (AKA finished transmission)
     * <p>{@code null} if something went wrong
     * <p>{@code Query} - with the params
     */
    public static Query getRTParamQuery(){
        if(param_ois!=null){
            try {
                return (Query) param_ois.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }else{
            return null;
        }
    }

    private static void party(){
        while(running){
            Query q;
            try {
                q = (Query)param_ois.readObject();
                if(q.getQueryType()==2){
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

    public fml(){
        fml.running=true;
    }

    @Override
    public void run() {
        listenForConnection();
        party();
    }
}
