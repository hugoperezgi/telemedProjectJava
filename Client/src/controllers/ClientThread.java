package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientThread extends Thread{

    private static SocketChannel sck;
    private static Query clientQuery;
    private static Query serverResponse;

    public static boolean clientRunning = true;

    private static boolean sendQuery = false;
    
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;

    public ClientThread(){
        clientRunning=true;
        sendQuery=false;
        clientQuery=null;
    }

    @Override
    public void run() {
        setup();
        runClient();
    }

    private void setup(){
        try {
            sck = SocketChannel.open(new InetSocketAddress("127.0.0.1", 50500));

            oos = new ObjectOutputStream(sck.socket().getOutputStream());
            ois = new ObjectInputStream(sck.socket().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void runClient(){
        
        while (clientRunning) {
            
            if (sendQuery) {

                sendQuery=false; //reset control

                if(clientQuery!=null){

                        serverResponse=null;

                    try {

                        oos.writeObject(clientQuery);

                        serverResponse=(Query) ois.readObject();

                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                }

                clientQuery=null;
                    //Clear the client query for next use.
            }

        }

        try {
            sck.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the server response to the query sent
     */    
    public static Query getServerResponse() {
        return serverResponse;
    }

    /**
     * Load the client query that you want to send
     */
    public static void setClientQuery(Query clientQuery) {
        ClientThread.clientQuery = clientQuery;
    }

    /**
     * Execute for the thread to send the provided query to the server <p>
     * The query must be provided using the {@code setClientQuery()} method <p>
     * Get the response through the {@code getServerResponse()} method. <p>
     */
    public static void sendQuery() {
        ClientThread.sendQuery = true;
    }
}
