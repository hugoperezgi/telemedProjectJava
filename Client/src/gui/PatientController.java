package gui;

import controllers.ClientLogic;
import controllers.PatientLogic;
import entities.MedicalTest;
import entities.Patient;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;


public class PatientController implements Initializable{
    
    private ErrorPopup ErrorPopup = new ErrorPopup();
    private SuccessPopup SuccessPopup = new SuccessPopup();

    public Stage stage=null;

    public Patient myself=null;
    private List<MedicalTest> mtList=null;
    
    @FXML
    Pane paneShowReport;

        @FXML
        Text textPatientName;
        @FXML
        Text textReportDate;
        @FXML
        Text textBitalinoSignalAvailable;
        @FXML
        TextArea textAreaPatientReport;
        @FXML
        TextArea textAreaDocComments;
        @FXML
        Button downloadReport;

        private void updateSelectedReport(MedicalTest m){
            textPatientName.setText("Patient ID: "+ m.getPatientID());
            textReportDate.setText("Report date: "+m.getReportDate().toString());
            String t = m.bitalinoDataAttached();
            if(t.contains("No")){
                downloadReport.setDisable(true);
            } else {
                downloadReport.setDisable(false);
            }
            textBitalinoSignalAvailable.setText(t);
            textAreaDocComments.setText(m.getDoctorComments());
            textAreaDocComments.setEditable(false);
            textAreaPatientReport.setText(m.getPatientComments());
            textAreaPatientReport.setEditable(false);
        }

        public void downloadReportData(){
            //TODO ffs why do I do dis
        }

        public void goBackToSelectReport(){

            comboBoxSelectedReport.getSelectionModel().clearSelection();

            hideAll();

            buttonCheckLight.setDisable(true);
            buttonCheckLight.setVisible(false);
            buttonCheckDrk.setDisable(false);
            buttonCheckDrk.setVisible(true);

            paneShowCliHist.setVisible(true);
            paneShowCliHist.setDisable(false);
        }
    
    @FXML
    Pane paneShowCliHist;

        @FXML 
        ComboBox<String> comboBoxSelectedReport;

        @FXML
        TableColumn<MedicalTest,Integer> columnMedTestId;
        @FXML
        TableColumn<MedicalTest,String> columnMedTestDate;
        @FXML
        TableColumn<MedicalTest,String> columnMedTestBitalinoData;

        @FXML
        TableView<MedicalTest> tableViewMedTests;
    
        private void updateReportTable(List<MedicalTest> mtList){
            tableViewMedTests.getItems().clear();
            tableViewMedTests.getItems().addAll(mtList);
            comboBoxSelectedReport.getSelectionModel().clearSelection();
                String[] sList = new String[mtList.size()];
                int i=0;
                for (MedicalTest medicalTest : mtList) {
                    sList[i]="Id: "+medicalTest.getTestID()+" - ("+medicalTest.getReportDate().toString()+")";
                    i++;
                }
            comboBoxSelectedReport.getItems().setAll(sList);
        }

        public void selectReport(){
            int index = comboBoxSelectedReport.getSelectionModel().getSelectedIndex();
            updateSelectedReport(mtList.get(index));

            hideAll();
            paneShowReport.setVisible(true);
            paneShowReport.setDisable(false);

        }

    @FXML
    Pane paneCreateReport;

        @FXML
        Text textReportDateCreate;
        @FXML
        TextArea textAreaPatientReportCreate;
        @FXML
        CheckBox checkBoxBitalinoData;
        @FXML
        TextField textFieldBitalinoMAC;

        public void bitalinoCheckBox(){
            if(checkBoxBitalinoData.isSelected()){
                textFieldBitalinoMAC.clear();
                textFieldBitalinoMAC.setEditable(true);
            }else{
                textFieldBitalinoMAC.setEditable(false);
                textFieldBitalinoMAC.clear();
                textFieldBitalinoMAC.setText("BITalino MAC Address");
                textFieldBitalinoMAC.setPromptText("BITalino MAC Address");
            }
        }

        private void resetCreateReport(){
            checkBoxBitalinoData.setSelected(false);
            textFieldBitalinoMAC.setEditable(false);
            textFieldBitalinoMAC.clear();
            textFieldBitalinoMAC.setText("BITalino MAC Address");
            textFieldBitalinoMAC.setPromptText("BITalino MAC Address");
            textAreaPatientReportCreate.clear();        
            textReportDateCreate.setText("Current date: "+Date.valueOf(LocalDate.now()).toString());
        }

        public void executeCreateReport(ActionEvent aEvent) throws IOException{
            stage = (Stage) ((Node) aEvent.getSource()).getScene().getWindow();
            String t= textAreaPatientReportCreate.getText();
            Integer id=-1;
            try {
                id = PatientLogic.createReport(myself.getPatientID(),t,Date.valueOf(LocalDate.now()));
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(id<0){
                ErrorPopup.errorPopup(0);
            }else{
                if(checkBoxBitalinoData.isSelected()){
                    int[] i = {0,};
                    stage.getScene().setCursor(Cursor.WAIT);
                    int errCode = PatientLogic.sendBitalinoData(textFieldBitalinoMAC.getText(),i,id);
                    stage.getScene().setCursor(Cursor.DEFAULT);
                    if(errCode!=0){
                    ErrorPopup.errorPopup(24);
                    //TODO polish this turd
                    }
                }
                SuccessPopup.successPopup(0);
            }
            resetCreateReport();
        }

    @FXML
    Button buttonCheckLight;    
    @FXML
    Button buttonCheckDrk;    
    
    public void changeToCheckReports() throws IOException{

        try {
            mtList=PatientLogic.checkMyReports(myself.getPatientID());
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(mtList==null){ErrorPopup.errorPopup(7);}
        else{
            updateReportTable(mtList);
        }

        hideAll();

        buttonCheckLight.setDisable(true);
        buttonCheckLight.setVisible(false);
        buttonCheckDrk.setDisable(false);
        buttonCheckDrk.setVisible(true);

        paneShowCliHist.setVisible(true);
        paneShowCliHist.setDisable(false);
    }

    
    @FXML
    Button buttonCreateLight;    
    @FXML
    Button buttonCreateDrk;     

    public void changeToCreateReport(){

        hideAll();
        resetCreateReport();

        buttonCreateLight.setDisable(true);
        buttonCreateLight.setVisible(false);
        buttonCreateDrk.setDisable(false);
        buttonCreateDrk.setVisible(true);

        paneCreateReport.setVisible(true);
        paneCreateReport.setDisable(false);
    }

    @FXML
    Text randomBSGO;

    private void hideAll(){

        buttonCheckDrk.setDisable(true);
        buttonCheckDrk.setVisible(false);
        buttonCreateDrk.setDisable(true);
        buttonCreateDrk.setVisible(false);

        buttonCheckLight.setDisable(false);
        buttonCheckLight.setVisible(true);
        buttonCreateLight.setDisable(false);
        buttonCreateLight.setVisible(true);
        
        paneCreateReport.setDisable(true);
        paneCreateReport.setVisible(false);
        paneShowCliHist.setDisable(true);
        paneShowCliHist.setVisible(false);
        paneShowReport.setDisable(true);
        paneShowReport.setVisible(false);

    }

    private void resetShowReport(){
        textAreaDocComments.clear();
        textAreaPatientReport.clear();
    }

    String[] ffs={"The first step towards loyalty is trust","Great hope can come from small sacrifices","Trust is the greatest of gifts, but it must be earned","Who we are never changes, who we think we are does","To seek something is to believe in its possibility","Humility is the only defense against humiliation","Trust in your friends, and they’ll have reason to trust in you"};
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnMedTestBitalinoData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().bitalinoDataAttached()));
        columnMedTestDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReportDate().toString()));
        columnMedTestId.setCellValueFactory(new PropertyValueFactory<>("testID"));

        resetCreateReport();
        resetShowReport();
        hideAll();

        Random rnd = new Random();
        randomBSGO.setText(ffs[rnd.nextInt(ffs.length)]);

    };
    
    public void logOut(){
        ClientLogic.closeAll();
        System.exit(0);
    }

    public void setSelf(){
        try {
            myself=PatientLogic.getMyself();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //TODO EDIT DATA?


}
