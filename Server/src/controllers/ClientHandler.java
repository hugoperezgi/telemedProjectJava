package controllers;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler extends Thread {

    private SocketChannel sck;
    private ByteBuffer inByteBuffer;

    public static boolean serverRunning=true;
    public static int currentConnectedUsers;

    public ClientHandler(SocketChannel socket){
        currentConnectedUsers++;
        this.sck=socket;
    }

    @Override
    public void run() {
        //TODO
    }
}
