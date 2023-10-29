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

    public User(Integer id, String uname, byte[] psw, int role){
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

}
