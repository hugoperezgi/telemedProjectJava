package gui;


import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import controllers.AdminLogic;
import controllers.ClientLogic;
import controllers.DeadServer;
import controllers.JustDont;
import entities.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

public class AdminController implements Initializable{
    
    private ErrorPopup ErrorPopup = new ErrorPopup();
    private SuccessPopup SuccessPopup = new SuccessPopup();
    private List<User> uList = null;
    private List<Worker> wList = null;
    private List<Patient> pList=null;    
    private Integer id;
    Integer roleCreating=null;
    private boolean creatingUser = false;

    @FXML
    Pane paneMainMenu;

    @FXML
    Button buttonMenuDeleteUser_L;
    @FXML
    Button buttonMenuEditUser_L;    
    @FXML
    Button buttonMenuCreateUser_L;
    @FXML
    Button buttonMenuLinkUsers_L;    
    @FXML
    Button buttonMenuDeleteUser_D;
    @FXML
    Button buttonMenuEditUser_D;    
    @FXML
    Button buttonMenuCreateUser_D;
    @FXML
    Button buttonMenuLinkUsers_D;    

    @FXML
    Pane paneCreateUser;
    
        @FXML
        private ComboBox<String> comboBoxRole;    
        @FXML
        private String[] roles = {"Admin", "Doctor", "Patient"};
    
        @FXML
        private TextField textFieldUserName;
        @FXML
        private PasswordField passwordFieldPSW;
        

        @FXML
        private void showCreateUser() throws IOException, JustDont{
            mainMenu();
            hideAll();

            clearCreateUser();

            buttonMenuCreateUser_L.setDisable(true);
            buttonMenuCreateUser_L.setVisible(false);
            buttonMenuCreateUser_D.setDisable(false);
            buttonMenuCreateUser_D.setVisible(true);

            paneCreateUser.setVisible(true);
            paneCreateUser.setDisable(false);
        }

        private void forceBackToCreate() throws IOException{
            if(roleCreating==2){
                setUpCreatePatientPane();
                hideAll();
                paneEditPatient.setVisible(true);
                paneEditPatient.setDisable(false);            
            }else if(roleCreating==1){
                setUpCreateDoctorPane();
                hideAll();
                paneEditDoctor.setVisible(true);
                paneEditDoctor.setDisable(false);            
            }
        }

        @FXML
        private void createUser() throws ClassNotFoundException, IOException, Exception{

            String uname = textFieldUserName.getText();
            String psw = passwordFieldPSW.getText();
            if (uname == "" || psw == "" || comboBoxRole.getSelectionModel().isEmpty()) {
                ErrorPopup.errorPopup(2);
            }
            roleCreating = comboBoxRole.getSelectionModel().getSelectedIndex();
            id=AdminLogic.createUser(uname,ClientLogic.hashPassword(psw),roleCreating);
            creatingUser=true;

            clearCreateUser();
            
            if(roleCreating==2){
                setUpCreatePatientPane();
                hideAll();
                paneEditPatient.setVisible(true);
                paneEditPatient.setDisable(false);            
            }else if(roleCreating==1){
                setUpCreateDoctorPane();
                hideAll();
                paneEditDoctor.setVisible(true);
                paneEditDoctor.setDisable(false);            
            }else{
                creatingUser=false;
                roleCreating=null;
                SuccessPopup.successPopup(0);
                mainMenu();
            }
        }

        private void clearCreateUser(){
            comboBoxRole.getSelectionModel().clearSelection();
            textFieldUserName.clear();
            passwordFieldPSW.clear();            
        }

    @FXML
    Pane paneSelectUser;

        @FXML
        Button buttonDeleteUser;
        @FXML
        Button buttonEditUser;
        
        private void populateUserTable() throws ClassNotFoundException, IOException{

            try {
                
                tableViewUsers.getItems().clear();
                uList=AdminLogic.showAllUsers();
                tableViewUsers.getItems().addAll(uList);
                comboBoxSelectedUser.getItems().clear();
                    String[] sList = new String[uList.size()];
                    int i=0;
                    for (User u : uList) {
                        sList[i]="Id: "+u.getUserID()+" - ("+u.getRoleString()+")";
                        i++;
                    }
                comboBoxSelectedUser.getItems().setAll(sList);

            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }

        }

        @FXML
        private void showEditUser() throws ClassNotFoundException, IOException, JustDont{
            mainMenu();
            hideAll();

            populateUserTable();

            buttonMenuEditUser_D.setVisible(true);
            buttonMenuEditUser_D.setDisable(false);
            buttonMenuEditUser_L.setVisible(false);
            buttonMenuEditUser_L.setDisable(true);

            buttonDeleteUser.setVisible(false);
            buttonDeleteUser.setDisable(true);
            buttonEditUser.setVisible(true);
            buttonEditUser.setDisable(false);
            
            paneSelectUser.setVisible(true);
            paneSelectUser.setDisable(false);        
        }
        @FXML
        private void showDeleteUser() throws ClassNotFoundException, IOException, JustDont{
            mainMenu();
            hideAll();

            populateUserTable();

            buttonMenuDeleteUser_D.setVisible(true);
            buttonMenuDeleteUser_D.setDisable(false);
            buttonMenuDeleteUser_L.setVisible(false);
            buttonMenuDeleteUser_L.setDisable(true);

            buttonDeleteUser.setVisible(true);
            buttonDeleteUser.setDisable(false);
            buttonEditUser.setVisible(false);
            buttonEditUser.setDisable(true);
            
            paneSelectUser.setVisible(true);
            paneSelectUser.setDisable(false);
        }
        @FXML
        private void deleteUser() throws ClassNotFoundException, IOException, JustDont{

            if(comboBoxSelectedUser.getSelectionModel().isEmpty()){
                ErrorPopup.errorPopup(2);
                return;
            }
            
            try {
                
                Integer index = comboBoxSelectedUser.getSelectionModel().getSelectedIndex();
                if(AdminLogic.deleteUser(uList.get(index).getUserID())==-1){
                    ErrorPopup.errorPopup(0);
                }else{
                    SuccessPopup.successPopup(6);
                    hideAll();
                    mainMenu();
                }
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }
        }

        @FXML
        TableColumn<User,Integer> columnUserId;
        @FXML
        TableColumn<User,String> columnUserRole;
        @FXML
        TableColumn<User,String> columnUserName;

        @FXML
        TableView<User> tableViewUsers;

        @FXML 
        ComboBox<String> comboBoxSelectedUser;

        public void selectUser() throws IOException, ClassNotFoundException, JustDont{

            if(comboBoxSelectedUser.getSelectionModel().isEmpty()){
                ErrorPopup.errorPopup(2);
                return;
            }
            int index = comboBoxSelectedUser.getSelectionModel().getSelectedIndex();
            Integer r = updateSelectedUser(uList.get(index));
            if(r==-1){return;}

            hideAll();
            
            switch (r) {
                case 0:
                    ErrorPopup.errorPopup(21);
                    mainMenu();
                    break;
                case 1:
                    paneEditDoctor.setVisible(true);
                    paneEditDoctor.setDisable(false);
                    break;
                case 2:
                    paneEditPatient.setVisible(true);
                    paneEditPatient.setDisable(false);
                    break;
                default:
                    break;
            }

        }

        private Integer updateSelectedUser(User u) throws ClassNotFoundException, IOException{
            Integer r = u.getRole();

            switch (r) {
                case 1:
                
                    try {
                        
                        Worker w = AdminLogic.getDoctor(u.getUserID());
    
                        if(w==null){
                            ErrorPopup.errorPopup(0);
                            return -1;
                        }
                        setUpEditDoctorPane();
                        textFieldWorkerRole.setText("Doctor");
                        textFieldWorkerRole.setEditable(false);
                        id=w.getWorkerID();
                        textFieldWorkerId.setText(Integer.toString(id));
                        textFieldWorkerId.setEditable(false);
                        textFieldWorkerName.setPromptText(w.getName());
                        textFieldPatientSurname.setPromptText(w.getSurname());
                        
                    } catch (DeadServer e) {
                        e.printStackTrace();
                        logOut();
                    }


                    break;
                case 2:

                    try {
                        
                        Patient p= AdminLogic.getPatient(u.getUserID());
                        if(p==null){
                            ErrorPopup.errorPopup(0);
                            return -1;
                        }
                        setUpEditPatientPane();
                        textFieldPatientRole.setText("Patient");
                        textFieldPatientRole.setEditable(false);
                        id = p.getPatientID();
                        textFieldPatientId.setText(Integer.toString(id));
                        textFieldPatientId.setEditable(false);
                        if(p.getGender().equalsIgnoreCase("Male")){
                            radioButtonMale.setSelected(true);
                            radioButtonFem.setSelected(false);
                        }else {
                            radioButtonMale.setSelected(false);
                            radioButtonFem.setSelected(true);
                        }
                        textFieldPatientName.setPromptText(p.getName());
                        textFieldPatientSurname.setPromptText(p.getSurname());
                        datePickerBDate.setPromptText(p.getBirthDate().toString());
                        comboBoxBloodType.setPromptText(p.getBloodType());
                        
                    } catch (DeadServer e) {
                        e.printStackTrace();
                        logOut();
                    }


                    
                    break;
                default:
                    break;
            }            
            return r;
        }
        
        //Table

    @FXML
    Pane paneEditPatient;
        //Edit patient Stuff
        @FXML
        private TextField textFieldPatientRole;
        @FXML
        private TextField textFieldPatientId;
        @FXML
        private ToggleGroup genderRadioButton;
        @FXML
        private RadioButton radioButtonMale;
        @FXML
        private RadioButton radioButtonFem;
        @FXML
        private TextField textFieldPatientName;
        @FXML
        private TextField textFieldPatientSurname;
        @FXML
        private DatePicker datePickerBDate;
        @FXML
        private ComboBox<String> comboBoxBloodType;

        @FXML
        private Button buttonCreatePatient;
        @FXML
        private Button buttonEditPatient;


        @FXML
        private String[] bloodTypeStrings = {"A+", "A-", "B+", "B-", "AB+", "AB-", "0+", "0-"};

        public void setUpCreatePatientPane(){

            buttonEditPatient.setDisable(true);
            buttonEditPatient.setVisible(false);

            buttonCreatePatient.setVisible(true);
            buttonCreatePatient.setDisable(false);

            textFieldPatientRole.setText("Patient");
            textFieldPatientRole.setEditable(false);
            textFieldPatientId.setText("Not Assigned Yet.");
            textFieldPatientId.setEditable(false);
        }

        public void setUpEditPatientPane(){
            buttonCreatePatient.setDisable(true);
            buttonCreatePatient.setVisible(false);

            buttonEditPatient.setVisible(true);
            buttonEditPatient.setDisable(false);
        }
        
        private void resetEditPatient(){
            textFieldPatientId.clear();
            radioButtonMale.setSelected(false);
            radioButtonFem.setSelected(false);
            textFieldPatientName.clear();
            textFieldPatientSurname.clear();
            datePickerBDate.getEditor().clear();
            comboBoxBloodType.getSelectionModel().clearSelection();
        }

        public void executePatientUpdate() throws ClassNotFoundException, IOException, JustDont{
            Patient p = new Patient(id);

            String name = textFieldPatientName.getText();
            if (name != "") {
                p.setName(name);
            }
            
            String surname = textFieldPatientSurname.getText();
            if (surname != "") {
                p.setSurname(surname);
            } 
    
            if (radioButtonMale.isSelected()) {
                p.setGender("Male");
            } else if (radioButtonFem.isSelected()) {
                p.setGender("Female");
            } 
    
            if( !comboBoxBloodType.getSelectionModel().isEmpty() ){
                p.setBloodType(comboBoxBloodType.getValue());
            } 
            
            if(datePickerBDate.getEditor().getText() != ""){
                Date bDate = Date.valueOf(datePickerBDate.getValue());
                if (!((bDate.before(Date.valueOf(LocalDate.now()))) || bDate.equals(Date.valueOf(LocalDate.now())))) {
                    ErrorPopup.errorPopup(1);
                    return;
                }  
                p.setBirthDate(bDate);
            }

            try {
                
                if(AdminLogic.editPatient(p)==null){
                    ErrorPopup.errorPopup(0);
                    return;            
                }
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }    



            SuccessPopup.successPopup(1);
            resetEditPatient();
            mainMenu();
    
        }

        public void createPatient() throws IOException, ClassNotFoundException, JustDont{

            String name = textFieldPatientName.getText();
            if (name == "") {
                ErrorPopup.errorPopup(2);
                return;
            }
            String surname = textFieldPatientSurname.getText();
            if (surname == "") {
                ErrorPopup.errorPopup(2);
                return;
            } 
            String gender = "";
            if (radioButtonFem.isSelected()) {
                gender = "Female";
            } else if (radioButtonMale.isSelected()) {
                gender = "Male";
            } else {
                ErrorPopup.errorPopup(2);
                return;
            }
            String bloodType="";
            if( !comboBoxBloodType.getSelectionModel().isEmpty() ){
                bloodType = comboBoxBloodType.getValue(); 
            } else {
                ErrorPopup.errorPopup(2);
                return;
            }
            if(datePickerBDate.getEditor().getText() == ""){
                ErrorPopup.errorPopup(2);
                return;
            }
            Date bDate=null;
            try {
                bDate = Date.valueOf(datePickerBDate.getValue());
                if (!((bDate.before(Date.valueOf(LocalDate.now()))) || bDate.equals(Date.valueOf(LocalDate.now())))) {
                    ErrorPopup.errorPopup(1);
                    return;
                }
            } catch (Exception e) {
                ErrorPopup.errorPopup(2);
                return;            
            }

            Patient p = new Patient(id,name,surname,bloodType,gender,bDate);      
            
            try {
                
                if(AdminLogic.createPatient(p)==null){
                    ErrorPopup.errorPopup(0);
                    creatingUser=false;
                    return;            
                }
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }


            creatingUser=false;

            SuccessPopup.successPopup(4);
            resetEditPatient();
            mainMenu();
        }


    @FXML
    Pane paneEditDoctor;

        @FXML
        private TextField textFieldWorkerRole;
        @FXML
        private TextField textFieldWorkerId;
        @FXML
        private TextField textFieldWorkerName;
        @FXML
        private TextField textFieldWorkerSurname;    

        @FXML
        private Button buttonEditDoctor;    
        @FXML
        private Button buttonCreateDoctor;    

        private void resetEditDoct(){
            textFieldWorkerName.clear();
            textFieldWorkerSurname.clear();
        }

        private void setUpCreateDoctorPane(){
            resetEditDoct();
            buttonEditDoctor.setDisable(true);
            buttonEditDoctor.setVisible(false);

            buttonCreateDoctor.setVisible(true);
            buttonCreateDoctor.setDisable(false);

            textFieldWorkerRole.setText("Worker");
            textFieldWorkerRole.setEditable(false);
            textFieldWorkerId.setText("Not Assigned Yet.");
            textFieldWorkerId.setEditable(false);
        }        

        private void setUpEditDoctorPane(){
            resetEditDoct();
            buttonCreateDoctor.setDisable(true);
            buttonCreateDoctor.setVisible(false);

            buttonEditDoctor.setVisible(true);
            buttonEditDoctor.setDisable(false);
        }        

        @FXML
        private void executeDoctorUpdate() throws ClassNotFoundException, IOException, JustDont{

            String name = textFieldWorkerName.getText();
            if(name==""){name=null;}
            String surname = textFieldWorkerSurname.getText();
            if(surname==""){surname=null;}
            Worker w = new Worker(id,null,name,surname);
            
            try {
                
                if(AdminLogic.editDoctor(w)==null){
                    ErrorPopup.errorPopup(0);
                    return;
                }        
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }



            SuccessPopup.successPopup(2);
            resetEditDoct();
            mainMenu();
        }

        @FXML
        private void createDoctor() throws ClassNotFoundException, IOException, JustDont{

            String name = textFieldWorkerName.getText();
            if(name==""){ErrorPopup.errorPopup(2);return;}
            String surname = textFieldWorkerSurname.getText();
            if(surname==""){ErrorPopup.errorPopup(2);return;}
            
            try {
                
                if(AdminLogic.createWorker(id,name,surname)==null){
                    ErrorPopup.errorPopup(0);
                    creatingUser=false;
                    return;            
                }        
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }


     
            creatingUser=false;
            SuccessPopup.successPopup(5);
            resetEditDoct();
            mainMenu();
        }
                

    @FXML
    Pane paneLinkDoctPatient;

        @FXML
        private ComboBox<String> comboBoxLinkPatient;
        @FXML
        private ComboBox<String> comboBoxLinkDoctor;

        private void populateLinkBox() throws ClassNotFoundException, IOException{

            try {
                
                wList = AdminLogic.showAllWorkers();
                    String[] sList = new String[wList.size()];
                    int i=0;
                    for (Worker w : wList) {
                        sList[i]="Id: "+w.getWorkerID()+" - ("+w.getName()+" "+w.getSurname()+")";
                        i++;
                    }
                comboBoxLinkDoctor.getSelectionModel().clearSelection();
                comboBoxLinkDoctor.getItems().setAll(sList);
                pList = AdminLogic.showAllPatients();
                    String[] sList2 = new String[pList.size()];
                    i=0;
                    for (Patient p : pList) {
                        sList2[i]="Id: "+p.getPatientID()+" - ("+p.getName()+" "+p.getSurname()+")";
                        i++;
                    }
                comboBoxLinkPatient.getSelectionModel().clearSelection();
                comboBoxLinkPatient.getItems().setAll(sList2);        
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }

        }

        @FXML
        private void showLinkUsers() throws IOException, ClassNotFoundException, JustDont{
            mainMenu();
            hideAll();

            buttonMenuLinkUsers_L.setVisible(false);
            buttonMenuLinkUsers_L.setDisable(true);
            buttonMenuLinkUsers_D.setVisible(true);
            buttonMenuLinkUsers_D.setDisable(false);

            populateLinkBox();

            paneLinkDoctPatient.setDisable(false);
            paneLinkDoctPatient.setVisible(true);
        }

        @FXML
        private void createLink() throws IOException, ClassNotFoundException, JustDont{
            if(comboBoxLinkPatient.getSelectionModel().isEmpty() || comboBoxLinkDoctor.getSelectionModel().isEmpty()){
                ErrorPopup.errorPopup(2);
                return;
            }

            Integer dId=wList.get(comboBoxLinkDoctor.getSelectionModel().getSelectedIndex()).getWorkerID();
            Integer pId=pList.get(comboBoxLinkPatient.getSelectionModel().getSelectedIndex()).getPatientID();

            try {
                
                if(AdminLogic.createLink(pId,dId)==null){
                    ErrorPopup.errorPopup(22);
                    return;
                }        
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }



            SuccessPopup.successPopup(3);
            mainMenu();
        }

    /**
     * hide+disable every pane
     */
    private void hideAll(){

        paneMainMenu.setVisible(false);
        paneMainMenu.setDisable(true);

        paneCreateUser.setVisible(false);
        paneCreateUser.setDisable(true);

        paneSelectUser.setVisible(false);
        paneSelectUser.setDisable(true);

        paneEditDoctor.setVisible(false);
        paneEditDoctor.setDisable(true);

        paneEditPatient.setVisible(false);
        paneEditPatient.setDisable(true);


        paneLinkDoctPatient.setVisible(false);
        paneLinkDoctPatient.setDisable(true);
    }

    private void mainMenu() throws IOException, JustDont{
                
        hideAll();

        buttonMenuCreateUser_L.setDisable(false);
        buttonMenuCreateUser_L.setVisible(true);
        buttonMenuCreateUser_D.setDisable(true);
        buttonMenuCreateUser_D.setVisible(false);

        buttonMenuEditUser_L.setDisable(false);
        buttonMenuEditUser_L.setVisible(true);
        buttonMenuEditUser_D.setDisable(true);
        buttonMenuEditUser_D.setVisible(false);

        buttonMenuDeleteUser_L.setDisable(false);
        buttonMenuDeleteUser_L.setVisible(true);
        buttonMenuDeleteUser_D.setDisable(true);
        buttonMenuDeleteUser_D.setVisible(false);
        
        buttonMenuLinkUsers_L.setDisable(false);
        buttonMenuLinkUsers_L.setVisible(true);
        buttonMenuLinkUsers_D.setDisable(true);
        buttonMenuLinkUsers_D.setVisible(false);        

        paneMainMenu.setVisible(true);
        paneMainMenu.setDisable(false);

        if(creatingUser){
            ErrorPopup.errorPopup(23);
            forceBackToCreate();
            throw new JustDont();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBoxRole.getItems().setAll(roles);
        comboBoxBloodType.getItems().setAll(bloodTypeStrings);

        columnUserName.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnUserRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoleString()));
        columnUserId.setCellValueFactory(new PropertyValueFactory<>("userID"));

    }     
        
    
    public void logOut(){
        ClientLogic.closeAll();
        System.exit(0);
    }


}