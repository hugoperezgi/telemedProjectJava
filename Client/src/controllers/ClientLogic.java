package controllers;

import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class ClientLogic {
    

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
            ClientThread.setClientQuery(q);
            ClientThread.sendQuery();
            Query serverResponse = ClientThread.getServerResponse();

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

	private static byte[] hashPassword(String psw) throws Exception{
        byte[] s = {(byte) 0, (byte) 1};
        KeySpec k = new PBEKeySpec(psw.toCharArray(), s, 65353, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return f.generateSecret(k).getEncoded();
    }  

}
