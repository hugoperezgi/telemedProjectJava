package entities;

import java.io.Serializable;
import java.sql.*;

public class MedicalTest implements Serializable {
    static final long serialVersionUID = 42L;
    
    private Integer testID;
    private Integer patientID;
    private String patientComments;
    private String doctorComments;
    private Date reportDate;

    private Integer[] params; 

    public MedicalTest(Integer patID, String symptoms, Date date){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.params=null;
    }
    
    public MedicalTest(Integer id, Integer patID, String symptoms, String doctorComments, Date date, String params){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.testID=id;
        this.doctorComments=doctorComments;
        this.params=getParamsFromString(params);
    }

    public MedicalTest(Integer id, String doctVeredic){
        this.testID=id;
        this.doctorComments=doctVeredic;
    }     

    /**
     * Undo the formatting done by paramsToString(), used to recover the data from String format
     * @param paramString String with the format "parameter0 parameter1 ... parameterN "
     * @return <b>params</b> Integer[ ] with the parameters
     */
    private Integer[] getParamsFromString(String paramString){
        if(params==null){return null;}
        String[] s=paramString.split(" ");
        Integer[] params= new Integer[s.length];
        for (int i=0; i<s.length;i++) {
            if(s[i]==""){break;}
            params[i]=Integer.parseInt(s[i]);
        }
        return params;
    }
    
    /**
     * For db storage, cba doing blobs
     * @return "parameter0 parameter1 ... parameterN "
     */
    public String paramsToString(){
        String s = "";
        for (Integer integer : this.params) {
            s=s+integer+" ";
        }
        return s;
    }

    public Integer getPatientID() {
        return patientID;
    }
    
    public String getPatientComments() {
        return patientComments;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public String getDoctorComments() {
        return doctorComments;
    }
    public Integer getTestID() {
        return testID;
    }
    public String bitalinoDataAttached(){
        if(params==null){return "No BITalino data.";}
        return "BITalino data available.";
    }
}
