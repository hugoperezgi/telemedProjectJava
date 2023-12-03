package controllers;

import java.rmi.NotBoundException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import entities.*;

public class SQL {
    
    private static Connection c;

	public void connect() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection("jdbc:sqlite:./db/data.db");
		c.createStatement().execute("PRAGMA foreign_keys=ON");
	}

    public void disconnect() throws SQLException{
        c.close();
    }

    public void setupDb() throws SQLException{

        Statement s = c.createStatement();                      
        String str = "CREATE TABLE users " 
                    + "(userId INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " username TEXT NOT NULL unique,"
                    + " password VARBINARY(32) NOT NULL," //psw hashcode			   
                    + " role INTEGER NOT NULL)"; //0:Admin 1:medstaff 2:Patient		   
        s.executeUpdate(str);                                       
        s.close();        

        Statement s1 = c.createStatement();   
        String str1 = "create TABLE patients "
                    + "(patientID INTEGER  PRIMARY KEY AUTOINCREMENT," 
                    + " name TEXT NOT NULL, "
                    + " surname TEXT NOT NULL, "
                    + " gender TEXT NOT NULL, "
                    + " birthdate DATE NOT NULL, "
                    + " blood_type TEXT NOT NULL, "
                    + " userId INTEGER REFERENCES users(userId) ON UPDATE CASCADE ON DELETE CASCADE)";
        s1.executeUpdate(str1);                                       
        s1.close();       

        Statement s2 = c.createStatement();                      
        String str2 = "CREATE TABLE workers "
                    + "(workerId INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " workerName TEXT NOT NULL, "
                    + " workerSurname TEXT NOT NULL, "
                    + " userId INTEGER REFERENCES users(userId) ON UPDATE CASCADE ON DELETE CASCADE)";
        s2.executeUpdate(str2);                                       
        s2.close();   

        Statement s3 = c.createStatement();                      
        String str3 = "CREATE TABLE doctor_patient "
                    + "(patient_id INTEGER REFERENCES patients(patientID) ON UPDATE CASCADE ON DELETE CASCADE,"
                    + " doctor_id INTEGER REFERENCES workers(workerId) ON UPDATE CASCADE ON DELETE CASCADE,"
                    + " PRIMARY KEY (patient_id,doctor_id))";
        s3.executeUpdate(str3);                                       
        s3.close();  

        Statement s4 = c.createStatement();                      
        String str4 = "CREATE TABLE medical_tests "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " patient_id INTEGER REFERENCES patients(patientID) ON UPDATE CASCADE ON DELETE CASCADE,"
                    + " signs TEXT NOT NULL,"	//Signs and Symptoms			   
                    + " doct_comments TEXT,"	//Doctor edits
                    + " date DATE NOT NULL,"				   
                    + " param0 LONGTEXT,"				   
                    + " param1 LONGTEXT,"				   
                    + " param2 LONGTEXT,"				   
                    + " param3 LONGTEXT,"				   
                    + " param4 LONGTEXT,"				   
                    + " param5 LONGTEXT)";		//String with the params (Up to 4 294 967 295 characters) -> Maybe needs change
        s4.executeUpdate(str4);                                       
        s4.close();        

		User p=null,w=null;

		try {
			this.addUser(new User("admin", hashPassword("admin"), 0));
		} catch (Exception e) {
			System.out.println("Something went wrong while creating the admin");
			e.printStackTrace();
		}
		try {
			this.addUser(new User("doctor", hashPassword("doctor"), 1));
			 w=this.checkPassword("doctor", hashPassword("doctor"));
			this.addWorker(new Worker(w.getUserID(), "name", "surname"));
		} catch (Exception e) {
			System.out.println("Something went wrong while creating the doctor");
			e.printStackTrace();
		}
		try {
			this.addUser(new User("patient", hashPassword("patient"), 2));
			 p=this.checkPassword("patient", hashPassword("patient"));
			this.addPatient(new Patient(p.getUserID(), 0, "name", "surname", "0-", "Male", java.sql.Date.valueOf(LocalDate.now())));
		} catch (Exception e) {
			System.out.println("Something went wrong while creating the patient");
			e.printStackTrace();
		}
		try {
			this.createLinkDoctorPatient(this.selectPatientByUserId(p.getUserID()).getPatientID(), this.selectWorkerByUserId(w.getUserID()).getWorkerID());
		} catch (Exception e) {
			System.out.println("Something went wrong while creating the patient");
			e.printStackTrace();
		}

		System.out.println("Database setup.");		
    }                
	
	public Integer addUser(User u) throws SQLException{
        String str = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        PreparedStatement prepS = c.prepareStatement(str);
        prepS.setString(1, u.getUsername());
        prepS.setBytes(2, u.getPasswordHash());
        prepS.setInt(3, u.getRole());
        prepS.executeUpdate();
        prepS.close();
		return this.checkPassword(u.getUsername(), u.getPasswordHash()).getUserID();
	}

	public List<User> getAllUsers() throws SQLException{ 
		String str = "SELECT userId, username, role FROM users";
		PreparedStatement p = c.prepareStatement(str);
		ResultSet rs = p.executeQuery();
		List <User> uList = new ArrayList<User>();
		while(rs.next()){
			uList.add(new User(rs.getInt("userId"), rs.getString("username"), null, rs.getInt("role")));
		}
		p.close();
		rs.close();
		return uList;
	}

	public User getUser(Integer userId) throws SQLException{
		String str = "SELECT * FROM users WHERE userId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1, userId);
		ResultSet rs = p.executeQuery();
		User u = null;
		if(rs.next()){
			u= new User(rs.getInt("userId"), rs.getString("username"), rs.getBytes("password"), rs.getInt("role"));
		}
		p.close();
		rs.close();
		return u;
	}
	public boolean isUserNameFree(String username) throws SQLException{
		String str = "SELECT * FROM users WHERE username = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setString(1, username);
		ResultSet rs = p.executeQuery();
		User u = null;
		if(rs.next()){
			u= new User(rs.getInt("userId"), rs.getString("username"), null, rs.getInt("role"));
		}
		p.close();
		rs.close();
		if(u==null){return true;}else{return false;}
	}
	public User checkPassword(String username, byte[] password) throws SQLException{
		String str = "SELECT * FROM users WHERE username = ? AND password = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setString(1, username);
		p.setBytes(2, password);
		ResultSet rs = p.executeQuery();
		User u = null;
		if(rs.next()){
			u= new User(rs.getInt("userId"), rs.getString("username"), rs.getBytes("password"), rs.getInt("role"));
		}
		p.close();
		rs.close();
		return u;
	}
	public void deleteUser(Integer userId) throws SQLException{
		String str = "DELETE FROM users WHERE userId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1, userId);
		p.executeUpdate();
		p.close();
	}

    public void changeUserPassword(Integer userID, byte[] psw) throws SQLException{ 
		String str = "UPDATE users SET password = ? WHERE userId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setBytes(1, psw);
		p.setInt(2, userID);
		p.executeUpdate();
		p.close();
	}	

	public void changeUserName(Integer userID, String name) throws SQLException{ 
		String str = "UPDATE users SET username = ? WHERE userId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setString(1, name);
		p.setInt(2, userID);
		p.executeUpdate();
		p.close();
	}	

    public void addPatient(Patient p) throws SQLException{
        String str = "INSERT INTO patients (name, surname, gender, birthdate, blood_type, userId) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement prepS = c.prepareStatement(str);
        prepS.setString(1, p.getName());
        prepS.setString(2, p.getSurname());
        prepS.setString(3, p.getGender());
        prepS.setDate(4, p.getBirthDate());
        prepS.setString(5, p.getBloodType());
        prepS.setInt(6, p.getUserID());
        prepS.executeUpdate();
        prepS.close();
    }

    public void addWorker(Worker w) throws SQLException{
        String str = "INSERT INTO workers (workerName, workerSurname, userId) VALUES (?, ?, ?)";
        PreparedStatement p = c.prepareStatement(str);
        p.setString(1, w.getName());
        p.setString(2, w.getSurname());
        p.setInt(3, w.getUserID());
        p.executeUpdate();
        p.close();	
    }

	public Integer addMedicalTest(MedicalTest medtest) throws SQLException{
		String str = "INSERT INTO medical_tests (patient_id, signs, date) VALUES (?, ?, ?)";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1, medtest.getPatientID());
		p.setString(2, medtest.getPatientComments());
		p.setDate(3, medtest.getReportDate());
		p.executeUpdate();
		p.close();
		String query = "SELECT last_insert_rowid() AS lastId";
		PreparedStatement p2 = c.prepareStatement(query);
		ResultSet rs = p2.executeQuery();
		Integer lastId = rs.getInt("lastId");
		return lastId;
    }

    public void addParametersToMedicalTest(Integer testID, String[] params) throws SQLException{
        String str;
		PreparedStatement p;
		for (int i = 0; i < params.length; i++) {
			str = "UPDATE medical_tests SET param"+i+" = ? WHERE id = ?";
			p = c.prepareStatement(str);
			p.setString(1, params[i]);
			p.setInt(2, testID);
			p.executeUpdate();
			p.close();
		}
    }

    public void addCommentsToMedicalTest(Integer testID, String comments) throws SQLException{
        String str = "UPDATE medical_tests SET doct_comments = ? WHERE id = ?";
        PreparedStatement p = c.prepareStatement(str);
        p.setString(1, comments);
        p.setInt(2, testID);
        p.executeUpdate();
        p.close();
    }
	//Change this
	public List<MedicalTest> searchMedicalTestByPatientID(Integer patientID) throws SQLException, Exception{
		String str = "SELECT * FROM medical_tests WHERE patient_id = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1, patientID);
		ResultSet rs = p.executeQuery();
		List <MedicalTest> mList = new ArrayList<MedicalTest>();
		while(rs.next()){
			String[] s = {rs.getString("param0"),rs.getString("param1"),rs.getString("param2"),rs.getString("param3"),rs.getString("param4"),rs.getString("param5")};
			mList.add( new MedicalTest(rs.getInt("id"),rs.getInt("patient_id"), rs.getString("signs"), rs.getString("doct_comments"), rs.getDate("date"), s));
		}
		p.close();
		rs.close();
		return mList;
	}
    
	// public List<Patient> searchPatient(String surname) throws SQLException, NotBoundException {
	// 	String str = "SELECT * FROM patients WHERE surname LIKE ?";
	// 	PreparedStatement p = c.prepareStatement(str);
	// 	p.setString(1,"%" + surname + "%");
	// 	ResultSet rs = p.executeQuery();
	// 	List <Patient> pList = new ArrayList<Patient>();
	// 	while(rs.next()){
    //         pList.add( new Patient(rs.getInt("userId"), rs.getInt("patientID"), rs.getString("name"), rs.getString("surname"), rs.getString("blood_type"), rs.getString("gender"), rs.getDate("birthdate")) );
	// 	}
	// 	p.close();
	// 	rs.close();
	// 	return pList;
	// }
	public List<Patient> selectAllPatients() throws SQLException, NotBoundException {
		String str = "SELECT * FROM patients";
		PreparedStatement p = c.prepareStatement(str);
		ResultSet rs = p.executeQuery();
		List <Patient> pList = new ArrayList<Patient>();
		while(rs.next()){
            pList.add( new Patient(rs.getInt("userId"), rs.getInt("patientID"), rs.getString("name"), rs.getString("surname"), rs.getString("blood_type"), rs.getString("gender"), rs.getDate("birthdate")) );
		}
		p.close();
		rs.close();
		return pList;
	}
	
	
	public List<Patient> searchPatientByDoctor(Integer userId) throws SQLException, NotBoundException {
		String str ="SELECT * FROM patients AS p JOIN doctor_patient AS dp ON p.patientID=dp.patient_id JOIN workers AS w ON w.workerId=dp.doctor_id WHERE w.userId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1,userId);
		ResultSet rs = p.executeQuery();
		List <Patient> pList = new ArrayList<Patient>();
		while(rs.next()){
			pList.add( new Patient(rs.getInt("userId"), rs.getInt("patientID"), rs.getString("name"), rs.getString("surname"), rs.getString("blood_type"), rs.getString("gender"), rs.getDate("birthdate")) );
		}
		p.close();
		rs.close();
		return pList;
	}
	
	
	// public List<Worker> searchWorker(String surname) throws SQLException, NotBoundException {
	// 	String str = "SELECT * FROM workers WHERE workerSurname LIKE ?";
	// 	PreparedStatement p = c.prepareStatement(str);
	// 	p.setString(1,"%" + surname + "%");
	// 	ResultSet rs = p.executeQuery();
	// 	List <Worker> wList = new ArrayList<Worker>();
	// 	while(rs.next()){ 
	// 		wList.add( new Worker(rs.getInt("workerId"), rs.getString("workerName"), rs.getString("workerSurname")) );
	// 	}
	// 	p.close();
	// 	rs.close();
	// 	return wList;
	// }
	public List<Worker> getAllWorkers() throws SQLException, NotBoundException {
		String str = "SELECT * FROM workers";
		PreparedStatement p = c.prepareStatement(str);
		ResultSet rs = p.executeQuery();
		List <Worker> wList = new ArrayList<Worker>();
		while(rs.next()){ 
			wList.add( new Worker(rs.getInt("workerId"),null, rs.getString("workerName"), rs.getString("workerSurname")) );
		}
		p.close();
		rs.close();
		return wList;
	}

	public Patient selectPatient(Integer patientID) throws SQLException, NotBoundException {
		String str = "SELECT * FROM patients WHERE patientID = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1,patientID);
		ResultSet rs = p.executeQuery();
		Patient patient = null;
		if(rs.next()){
			patient = new Patient(rs.getInt("userId"), rs.getInt("patientID"), rs.getString("name"), rs.getString("surname"), rs.getString("blood_type"), rs.getString("gender"), rs.getDate("birthdate"));
		}
		p.close();
		rs.close();
		return patient;	
	}
	
	public Patient selectPatientWithDoctor(Integer patientID, Integer workerId) throws SQLException, NotBoundException {
		String str = "SELECT * FROM patients AS p JOIN doctor_patient AS dp ON dp.patient_id= p.patientID WHERE dp.patient_id = ? AND dp.doctor_id =?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1,patientID);
		p.setInt(2,workerId);
		ResultSet rs = p.executeQuery();
		Patient patient = null;
		if(rs.next()){
			patient = new Patient(rs.getInt("userId"), rs.getInt("patientID"), rs.getString("name"), rs.getString("surname"), rs.getString("blood_type"), rs.getString("gender"), rs.getDate("birthdate"));
		}
		p.close();
		rs.close();
		return patient;	
	}

	
	public Worker selectWorker(Integer workerId) throws SQLException, NotBoundException {
		String str = "SELECT * FROM workers WHERE workerId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1,workerId);
		ResultSet rs = p.executeQuery();
		Worker worker = null;
		if(rs.next()){
			worker = new Worker(rs.getInt("workerId"), rs.getString("workerName"), rs.getString("workerSurname"));
		}
		p.close();
		rs.close();
		return worker;	
	}

	public void createLinkDoctorPatient(int patientID, int workerId) throws SQLException {
		String str = "INSERT INTO doctor_patient (patient_id, doctor_id) VALUES (?,?)";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1, patientID);
		p.setInt(2, workerId);
		p.executeUpdate();
		p.close();
	}
	
	public Worker selectWorkerByUserId(Integer userID) throws SQLException  {
		String str = "SELECT * FROM workers WHERE userId = ? ";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1, userID);
		ResultSet rs = p.executeQuery();
		Worker worker = null;
		if(rs.next()){
			worker = new Worker(rs.getInt("workerId"),userID, rs.getString("workerName"), rs.getString("workerSurname"));
		}
		p.close();
		rs.close();
		return worker;
	}
	
	
	public Patient selectPatientByUserId(Integer userId) throws SQLException, NotBoundException {
		String str = "SELECT * FROM patients WHERE userId = ?";
		PreparedStatement p = c.prepareStatement(str);
		p.setInt(1,userId);
		ResultSet rs = p.executeQuery();
		Patient patient = null;
		if(rs.next()){
			patient = new Patient(rs.getInt("userId"), rs.getInt("patientID"), rs.getString("name"), rs.getString("surname"), rs.getString("blood_type"), rs.getString("gender"), rs.getDate("birthdate"));
		}
		p.close();
		rs.close();
		return patient;	
	}
    
	public void editPatient(Patient patient) throws SQLException{

		String str;
		PreparedStatement p;

		if (patient.getName() != null) {
			str = "UPDATE patients SET name = ? WHERE patientID = ?";
			p = c.prepareStatement(str);
			p.setString(1, patient.getName());
			p.setInt(2, patient.getPatientID());
			p.executeUpdate();	
			p.close();
		} 
		if (patient.getSurname() != null) {
			str = "UPDATE patients SET surname = ? WHERE patientID = ?";
			p = c.prepareStatement(str);
			p.setString(1, patient.getSurname());
			p.setInt(2, patient.getPatientID());
			p.executeUpdate();
			p.close();
		}
		if (patient.getGender() != null) {
			str = "UPDATE patients SET gender = ? WHERE patientID = ?";
			p = c.prepareStatement(str);
			p.setString(1, patient.getGender());
			p.setInt(2, patient.getPatientID());
			p.executeUpdate();	
			p.close();
		}
		if (patient.getBloodType() != null) {
			str = "UPDATE patients SET blood_type = ? WHERE patientID = ?";
			p = c.prepareStatement(str);
			p.setString(1, patient.getBloodType());
			p.setInt(2, patient.getPatientID());
			p.executeUpdate();
			p.close();
		}
		if (patient.getBirthDate() != null) {
			str = "UPDATE patients SET birthdate = ? WHERE patientID = ?";
			p = c.prepareStatement(str);
			p.setDate(1, patient.getBirthDate());
			p.setInt(2, patient.getPatientID());
			p.executeUpdate();
			p.close();
		}

	}

	public void editWorker(Worker w) throws SQLException{
		String str;
		PreparedStatement p;

		if(w.getName()!=null){
			str = "UPDATE workers SET workerName = ? WHERE workerId = ?";
			p = c.prepareStatement(str);
			p.setString(1, w.getName());
			p.setInt(2, w.getWorkerID());
			p.executeUpdate();	
		}
		if(w.getSurname()!=null){
			str = "UPDATE workers SET workerSurname = ? WHERE workerId = ?";
			p = c.prepareStatement(str);
			p.setString(1, w.getSurname());
			p.setInt(2, w.getWorkerID());
			p.executeUpdate();	
		}
		
	}

	public int getLastIdIntroduced() throws SQLException {
		String query = "SELECT last_insert_rowid() AS lastId";
		PreparedStatement p = c.prepareStatement(query);
		ResultSet rs = p.executeQuery();
		Integer lastId = rs.getInt("lastId");
		return lastId;
	}

	public static byte[] hashPassword(String psw) throws Exception{
        byte[] s = {(byte) 0, (byte) 1};
        KeySpec k = new PBEKeySpec(psw.toCharArray(), s, 65353, 256);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return f.generateSecret(k).getEncoded();
    }  

    
}
