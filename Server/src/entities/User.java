package entities;

import java.io.Serializable;

public class User implements Serializable {
    static final long serialVersionUID = 42L;


    private Integer userID;
        //Unique key for the db

    private String username;
    private byte[] passwordHash;
        //User login info

    private Integer role;
        //0:Admin 1:Doctor 2:Patient	

    public User(Integer id, String uname, byte[] psw, Integer role){
        this.userID=id;
        this.username=uname;
        this.passwordHash=psw;
        this.role=role;
    }
    public User(String uname, byte[] psw, int role){
        this.username=uname;
        this.passwordHash=psw;
        this.role=role;
    }
    public User(String uname, byte[] psw){
        this.username=uname;
        this.passwordHash=psw;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * {@code 0} - Admin<p>
     * {@code 1} - Doctor<p>
     * {@code 2} - Patient<p>
     */
    public Integer getRole() {
        return role;
    }
    public String getUsername() {
        return username;
    }
    public Integer getUserID() {
        return userID;
    }

}
