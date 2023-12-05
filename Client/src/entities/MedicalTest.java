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

    private String[] params; 

    public MedicalTest(Integer patID, String symptoms, Date date){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.params=null;
    }
    
    public MedicalTest(Integer id, Integer patID, String symptoms, String doctorComments, Date date, String[] params){
        this.patientID=patID;
        this.patientComments=symptoms;
        this.reportDate=date;
        this.testID=id;
        this.doctorComments=doctorComments;
        this.params=params;
    }

    public MedicalTest(Integer id, String doctVeredic){
        this.testID=id;
        this.doctorComments=doctVeredic;
    }     

    //TODO Create a function that translates data into plottable thingy

    public void getReportFile() throws IOException{
        FileOutputStream fos = new FileOutputStream("dowloadedReport.txt");
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(fos));
        dout.writeUTF("Report "+this.testID+" from Patient "+this.patientID+"\n");
        int i=0;
        for (String string : params) {
            if(string==null){continue;}
            dout.writeUTF("Channel "+i+" data:\n");
            dout.writeUTF(string);
            dout.writeUTF("\n");
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
}
