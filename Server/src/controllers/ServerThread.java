package controllers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.sql.SQLException;


public class ServerThread extends Thread{
    
    private ServerSocketChannel serverSocket=null;
    public static SQL sql= new SQL();

    public ServerThread(){
    }

    @Override
    public void run() {
        serverSetup();
        serverRun();
    }

    private void serverRun(){

        while (ClientHandler.serverRunning){   //Main server thread - Server listening for new client connections

            try { 
                
                ClientHandler clientThread = new ClientHandler(serverSocket.accept());
                    //New thread is created to handle a new client connection

                clientThread.setDaemon(true);
                clientThread.start();
                    //Thread starts working

            } catch (Exception e) {
                if(ClientHandler.serverRunning){
                    System.out.println("Something went wrong while accepting a new client.");
                    e.printStackTrace();
                }
            }

        }

    }

    private void serverSetup(){

        //Main server thread - Server socket setup
        try {

            serverSocket = ServerSocketChannel.open();
            InetSocketAddress serverIPAddr = new InetSocketAddress("127.0.0.1",50500);
            serverSocket.bind(serverIPAddr);

            ClientHandler.currentConnectedUsers = 0; //Current client count set to zero

        } catch (Exception e) {
            System.out.println("Something went wrong while setting up the server socket.");
            e.printStackTrace();
            serverShutdown();
        } 

        //Main server thread - Database setup
        try {

            try {

                sql.connect();

                try {   //Tries to setup the db if this was the first startup

                    sql.setupDb();

                } catch (SQLException e) {
                    if (!e.getMessage().contains("already exists")) {
                        e.printStackTrace();
                        serverShutdown();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                serverShutdown();
            }

        } catch (Exception e) {
            System.out.println("Something went wrong while setting up the database.");
            e.printStackTrace();
            serverShutdown();
        }


    }

    public void serverShutdown(){

        ClientHandler.serverRunning = false;
            //Shuts down every thread while() loop

        if(serverSocket!=null){ //Close main server sck
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sql!=null) { //Close db connection
            try {
                sql.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.exit(0);
            //Exits Java

    }


}
