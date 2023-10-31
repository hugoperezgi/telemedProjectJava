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

    /** Int[channel(0->5)][sample(0->n)] */
    private Integer[][] params; 

    public MedicalTest(Integer patID, String symptoms, Date date){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
    }
    
    public MedicalTest(Integer id, Integer patID, String symptoms, String doctorComments, Date date, String[] params){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.testID=id;
        this.doctorComments=doctorComments;
        this.params=getParamsFromString(params);
    }

    public MedicalTest(Integer id, String doctVeredic){
        this.testID=id;
        this.patientComments=doctVeredic;
    }     

    /**
     * Undo the formatting done by paramsToString(), used to recover the data from String format
     * @param paramString Array of 6 Strings with the format "parameter0 parameter1 ... parameterN ", one for each channel.
     * @return <b>params</b> Integer[channel][sample] with the parameters
     */
    private Integer[][] getParamsFromString(String[] paramString){
        if(params==null){return null;}
        for (int j = 0; j < paramString.length; j++) {
            String[] s=paramString[j].split(" ");
            Integer[][] params= new Integer[paramString.length][s.length];
            for (int i=0; i<s.length;i++) {
                if(s[i]==""){break;}
                params[j][i]=Integer.parseInt(s[i]);
            }
        }
        return params;
    }
    
    /**
     * For db storage, cba doing blobs
     * @return String array with format: "parameter0 parameter1 ... parameterN "
     */
    public String[] paramsToString(){
        String s[] = new String[6];
        for (int i = 0; i < s.length; i++) {
            s[i]="";
            for (Integer integer : this.params[i]) {    
                s[i]+=(integer+" ");
            }
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
}
