package entities;

import java.io.Serializable;
import java.sql.*;

public class Patient implements Serializable {
    static final long serialVersionUID = 42L;

    private Integer userID=null;

    private Integer patientID;

    private String name=null;
    private String surname=null;
    private byte bloodTypeAndGender=0b00000010; 
        // 0 [0] [A] [B] 0 [+/-] 0 [g] -> 01000001 == Male with 0- blood
    private Date birthDate=null;

    //(patientID,name,surname,gender,birthdate,blood_type,userId)

    /**
     * DO NOT USE FOR PATIENT EDIT.
     */
    public Patient(Integer userid, Integer patientid, String name, String surname, String bloodtype, String gender, Date bdate){
        this.userID=userid;
        this.patientID=patientid;
        this.name=name;
        this.surname=surname;
        this.birthDate=bdate;
        this.bloodTypeAndGender=0b00000000;
        this.setBloodType(bloodtype);
        this.setGender(gender);
    }

    /**
     * DO NOT USE FOR PATIENT EDIT.
     */    
    public Patient(Integer userid, String name, String surname, String bloodtype, String gender, Date bdate){
        this.userID=userid;
        this.patientID=null;
        this.name=name;
        this.surname=surname;
        this.birthDate=bdate;
        this.bloodTypeAndGender=0b00000000;
        this.setBloodType(bloodtype);
        this.setGender(gender);
    }

    /**
     * Patient constructor for edits on patient, use setters to change parameters
     */
    public Patient(Integer patientid){
        this.patientID=patientid;
        this.name=null;
        this.surname=null;
        this.birthDate=null;
        this.bloodTypeAndGender=0b00000010;
    }

    /**
     * Sets the bloodtype of the patient:
     * <p>{@code 0-} Bloodtype 0-
     * <p>{@code 0+} Bloodtype 0+
     * <p>{@code A-} Bloodtype A-
     * <p>{@code A+} Bloodtype A+
     * <p>{@code B-} Bloodtype B-
     * <p>{@code B+} Bloodtype B+
     * <p>{@code B+} Bloodtype AB-
     * <p>{@code B+} Bloodtype AB+
     */
    public void setBloodType(String type){
        this.bloodTypeAndGender&=0b00000011;
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
            case "AB-": //AB-
                this.bloodTypeAndGender|=0b00010100;
                break;
            case "AB+": //B+
                this.bloodTypeAndGender|=0b00010100;
                break;
            default:break;
        }
    }

    public String getBloodType() {
        byte temp = this.bloodTypeAndGender;
        temp&=0b01111100;
        switch (temp) {
            case 0b01000000: return "0-";
            case 0b01000100: return "0+";
            case 0b00100000: return "A-";
            case 0b00100100: return "A+";
            case 0b00010000: return "B-";
            case 0b00010100: return "B+";
            case 0b00001000: return "AB-";
            case 0b00001100: return "AB+";
            default: return null;
        }
    }

    /**
     * 
     * Sets the gender of the patient:
     * <p>{@code Female}
     * <p>{@code Male}
     */
    public void setGender(String gender){
        if(gender.contains("Male")){
            this.bloodTypeAndGender|=0b00000001;
        } else if (gender.contains("Female")) {
            this.bloodTypeAndGender&=0b11111100;
        } else {
            this.bloodTypeAndGender&=0b11111100;
            this.bloodTypeAndGender|=0b00000010;
        }
    }   

    /**
     * 
     * @return Patient gender:
     * <p>{@code Female}
     * <p>{@code Male}     
     */
    public String getGender(){
        byte temp = this.bloodTypeAndGender;
        temp&=0b00000011;
        if(temp == 0b00000000){
            return "Female";
        } else if (temp == 0b00000010){ // Gender field has NOT been changed
            return null;
        }
        return "Male";
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

    @Override
    public boolean equals(Object obj) {
        Patient p = (Patient) obj;
        if( (this.patientID==p.patientID) || (this.userID==p.userID) ){
            return true;
        }
        return false;
    }
}
