package entities;

import java.io.Serializable;

public class Worker implements Serializable {
    static final long serialVersionUID = 42L;

    private Integer userID;

    private Integer workerID;

    private String name;
    private String surname;

    public Worker(Integer userID, String name, String surname){
        this.name=name;
        this.surname=surname;
    }

    public Worker(Integer workerID, Integer userID, String name, String surname){
        this.workerID=workerID;
        this.userID=userID;
        this.name=name;
        this.surname=surname;
    }

}
