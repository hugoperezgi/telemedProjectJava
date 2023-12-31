package entities;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;

public class MedicalTest implements Serializable {
    static final long serialVersionUID = 42L;
    
    private Integer testID;
    private Integer patientID;
    private String patientComments;
    private String doctorComments;
    private Date reportDate;
    private Byte sympByte;

    private String[] params; 

    public MedicalTest(Integer patID, String symptoms, Date date,Byte s){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.params=null;
        this.sympByte=s;
    }
    
    public MedicalTest(Integer id, Integer patID, String symptoms, String doctorComments, Date date, String[] params, Byte sByte){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.testID=id;
        this.doctorComments=doctorComments;
        this.params=params;
        this.sympByte=sByte;
    }

    public MedicalTest(Integer id, String doctVeredic){
        this.testID=id;
        this.doctorComments=doctVeredic;
    }     

    //TODO Create a function that translates data into plottable thingy

    public void getReportFile() throws IOException{
        FileOutputStream fos = new FileOutputStream("dowloadedReport.txt");
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(fos));
        dout.writeBytes("Report "+this.testID+" from Patient "+this.patientID+"\n");
        int i=0;
        for (String string : params) {
            if(string==null){continue;}
            dout.writeBytes("Channel "+i+" data:\n");
            dout.writeBytes(string);
            dout.writeBytes("\n");
            i++;
        }
        dout.flush();
        dout.close();
        fos.close();
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
        for (String string : params) {
            if(string!=null){ return "BITalino data available.";}
        }
        return "No BITalino data.";
    }
    public byte getSympByte() {
        return sympByte;
    }
    public String bitalinoParams(){
        for (String string : params) {
            if(string!=null){ return string;}
        }
        return null;
    }
}
