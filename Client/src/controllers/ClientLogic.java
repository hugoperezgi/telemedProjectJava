package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class ClientLogic {
    
    private static Query serverResponse;

    /**
     * Used to log in into the server, requires username + password <p>
     * @return Possible return codes:
     * <p>{@code -1} - Client Reception Error
     * <p>{@code -2} - Wrong username/password
     * <p>{@code -3} - Server Error
     * <p>{@code 0} - Admin
     * <p>{@code 1} - Doctor
     * <p>{@code 2} - Patient
     */
    public static Integer logIn(String user, String password){

        try {
            Query q = new Query();
            q.construct_LogIn_Query(user, hashPassword(password));
            sendQuery(q);
            serverResponse = getServerResponse();

            if(serverResponse!=null){

                String response = serverResponse.getControlMsg();

                if (response.contains("WrongUserOrPassword")) {
                    return -2;
                }

                if(response.contains("LoggedIn")){
                    String[] s = response.split(":");
                    return Integer.parseInt(s[1]);
                }

            }else{return -1;}

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return -3;
    }

	public static byte[] hashPassword(String psw) throws Exception{
        byte[] s = {(byte) 0, (byte) 1};
        KeySpec k = new PBEKeySpec(psw.toCharArray(), s, 65353, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return f.generateSecret(k).getEncoded();
    }  

    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static SocketChannel sck;

        public static void setup(){
            try {
                sck = SocketChannel.open(new InetSocketAddress("127.0.0.1", 50500));

                oos = new ObjectOutputStream(sck.socket().getOutputStream());
                ois = new ObjectInputStream(sck.socket().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    
        public static void sendQuery(Query clientQuery) throws IOException {
            oos.writeObject(clientQuery);
        }

        public static Query getServerResponse() throws ClassNotFoundException, IOException {
            return (Query) ois.readObject();
        }
        
        public static void closeAll(){
            try {
                sck.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
}
