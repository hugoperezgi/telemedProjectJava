package entities;

import java.io.Serializable;
import java.sql.*;

public class Patient implements Serializable {
    static final long serialVersionUID = 42L;

    private Integer userID;

    private Integer patientID;

    private String name;
    private String surname;
    private byte bloodTypeAndGender; 
        // 0 [0] [A] [B] 0 [+/-] 0 [g] -> 01000001 == Male with 0- blood
    private Date birthDate;


    /**
     * Sets the bloodtype of the patient:
     * <p>{@code 0-} Bloodtype 0-
     * <p>{@code 0+} Bloodtype 0+
     * <p>{@code A-} Bloodtype A-
     * <p>{@code A+} Bloodtype A+
     * <p>{@code B-} Bloodtype B-
     * <p>{@code B+} Bloodtype B+
     */
    public void setBloodType(String type){
        this.bloodTypeAndGender&=0b00000001;
        switch (type) {
            case "0-": //0-
                this.bloodTypeAndGender|=0b01000000; 
                break;
            case "0+": //0+
                this.bloodTypeAndGender|=0b01000100;
                break;
            case "A-": //A-
                this.bloodTypeAndGender|=0b00100000;
                break;
            case "A+": //A+
                this.bloodTypeAndGender|=0b00100100;
                break;
            case "B-": //B-
                this.bloodTypeAndGender|=0b00010000;
                break;
            case "B+": //B+
                this.bloodTypeAndGender|=0b00010100;
                break;
            default:
                throw new Error("Not a valid type");
        }
    }

    public int getBloodType() {
        byte temp = this.bloodTypeAndGender;
        temp&=0b01110100;
        switch (temp) {
            case 0b01000000: return 0;
            case 0b01000100: return 1;
            case 0b00100000: return 2;
            case 0b00100100: return 3;
            case 0b00010000: return 4;
            case 0b00010100: return 5;
            default: return -1;
        }
    }

    /**
     * 
     * Sets the gender of the patient:
     * <p>{@code false}-Female
     * <p>{@code true}-Male
     */
    public void setGender(boolean gender){
        if(gender){
            this.bloodTypeAndGender|=0b00000001;
        } else {
            this.bloodTypeAndGender&=0b01110100;
        }
    }   

    /**
     * 
     * @return Patient gender:
     * <p>{@code False}-Female
     * <p>{@code True}-Male
     */
    public boolean getGender(){
        byte temp = this.bloodTypeAndGender;
        temp&=0b00000001;
        if(temp == 0b00000000){
            return true;
        } 
        return false;
    }

    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPatientID() {
        return patientID;
    }
    public void setPatientID(Integer patientID) {
        this.patientID = patientID;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public Integer getUserID() {
        return userID;
    }
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
