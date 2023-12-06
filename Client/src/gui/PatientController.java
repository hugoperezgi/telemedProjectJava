package gui;

import controllers.BIT;
import controllers.ClientLogic;
import controllers.DeadServer;
import controllers.PatientLogic;
import entities.MedicalTest;
import entities.Patient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
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
    private MedicalTest mtTest=null;
    
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
                mtTest=m;
                downloadReport.setDisable(false);
            }
            textBitalinoSignalAvailable.setText(t);
            if(m.getDoctorComments()==null){
                textAreaDocComments.setText("No doctor comments added.");
            }else{
                textAreaDocComments.setText(m.getDoctorComments());
            }
            textAreaDocComments.setEditable(false);
            textAreaPatientReport.setText(m.getPatientComments());
            textAreaPatientReport.setEditable(false);
        }

        public void downloadReportData() throws IOException{
            mtTest.getReportFile();
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

        public void selectReport() throws IOException{
            if(comboBoxSelectedReport.getSelectionModel().isEmpty()){
                ErrorPopup.errorPopup(2);
                return;
            }
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
        @FXML
        Slider sliderRecordTime;

        public void bitalinoCheckBox(){
            if(checkBoxBitalinoData.isSelected()){
                textFieldBitalinoMAC.clear();
                textFieldBitalinoMAC.setEditable(true);
                sliderRecordTime.setDisable(false);
            }else{
                textFieldBitalinoMAC.setEditable(false);
                textFieldBitalinoMAC.clear();
                sliderRecordTime.setValue(10);
                sliderRecordTime.setDisable(false);
                textFieldBitalinoMAC.setText("XX:XX:XX:XX:XX:XX");
                textFieldBitalinoMAC.setPromptText("XX:XX:XX:XX:XX:XX");
            }
        }

        private void resetCreateReport(){
            checkBoxBitalinoData.setSelected(false);
            textFieldBitalinoMAC.setEditable(false);
            textFieldBitalinoMAC.clear();
            sliderRecordTime.setValue(10);
            sliderRecordTime.setDisable(true);
            textFieldBitalinoMAC.setText("XX:XX:XX:XX:XX:XX");
            textFieldBitalinoMAC.setPromptText("XX:XX:XX:XX:XX:XX");
            textAreaPatientReportCreate.clear();        
            textReportDateCreate.setText("Current date: "+Date.valueOf(LocalDate.now()).toString());
        }
    
        public void executeCreateReport(ActionEvent aEvent) throws IOException, ClassNotFoundException, InterruptedException{
            int time = (int) sliderRecordTime.getValue(); 
            stage = (Stage) ((Node) aEvent.getSource()).getScene().getWindow();
            String t= textAreaPatientReportCreate.getText();
            Integer id=-1;
            try {
                id = PatientLogic.createReport(myself.getPatientID(),t,Date.valueOf(LocalDate.now()));
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }
            if(id<0){
                ErrorPopup.errorPopup(0);
            }else{
                if(checkBoxBitalinoData.isSelected()){
                    int[] i = {0,};
                    // stage.getScene().setCursor(Cursor.WAIT);
                    PatientLogic.sendBitalinoData(textFieldBitalinoMAC.getText(),i,id,time);

                    //Change main ui to loading bar :)
                    swapToLoading();
                    handleLoading();

                    // stage.getScene().setCursor(Cursor.DEFAULT);
                    //Finish everything once completed :)
                }else{                    
                    resetCreateReport();
                    hideAll();
                }
            }
        }

    @FXML
    private Pane paneLoading;

        @FXML
        private ProgressBar pb;
        @FXML
        private Text textLoading;

        private void swapToLoading(){
            hideAll();
            //disableMenu();
            paneLoading.setDisable(false);
            paneLoading.setVisible(true);
            textLoading.setText("Connecting to BITalino Device");
        }

        private void handleLoading(){

            Task<Void> loadingTextTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while(!BIT.running && BIT.err==1){
                        Thread.sleep(50);
                    }
                    return null;
                }
                @Override
                protected void succeeded() {

                    if(BIT.err!=1){
                        try {
                            if(BIT.err==0){SuccessPopup.successPopup(0);}
                            else{
                                if(BIT.err==-1){
                                    ErrorPopup e = new ErrorPopup();
                                    e.errorPopup(BIT.errMsg);
                                }else{
                                    ErrorPopup e = new ErrorPopup();
                                    e.errorPopup(0);
                                }
                            }
                            hideAll();
                            //enableMenu();
                        } catch (Exception e){}                    
                    }
                    
                    Platform.runLater(()->{
                        textLoading.setText("Getting Data From BITalino Device");
                    });
                }
            };
            new Thread(loadingTextTask).start();

            Task<Void> loaderTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateProgress(-1,0);
                    while((!BIT.pleaseStopIt)||BIT.err==1){
                        if(BIT.timeElapsed!=0){updateProgress((double) (((double)BIT.timeElapsed)/BIT.time),1);}
                        Thread.sleep(100);
                    }
                    return null;
                }
                @Override
                protected void succeeded() {

                    try {
                        if(BIT.err==0){SuccessPopup.successPopup(0);}
                        else{
                            if(BIT.err==-1){
                                ErrorPopup e = new ErrorPopup();
                                e.errorPopup(BIT.errMsg);
                            }else{
                                ErrorPopup e = new ErrorPopup();
                                e.errorPopup(0);
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    hideAll();

                }
            };
            pb.progressProperty().bind(loaderTask.progressProperty());
            new Thread(loaderTask).start();

        }

        @FXML
        private void stop() throws InterruptedException{
            BIT.pleaseStopIt=true;
            Thread.sleep(1000);
            hideAll();
        }



    @FXML
    Button buttonCheckLight;    
    @FXML
    Button buttonCheckDrk;    
    
    public void changeToCheckReports() throws IOException, ClassNotFoundException{

        try {
            mtList=PatientLogic.checkMyReports(myself.getPatientID());
        } catch (DeadServer e) {
            e.printStackTrace();
            logOut();
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

        Random rnd = new Random();
        randomBSGO.setText(ffs[rnd.nextInt(ffs.length)]);

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
        paneLoading.setDisable(true);
        paneLoading.setVisible(false);

    }

    private void resetShowReport(){
        textAreaDocComments.clear();
        textAreaPatientReport.clear();
    }

    String[] ffs={"(づ｡◕‿‿◕｡)づ","(◕‿◕✿)","(ᵔᴥᵔ)","♥‿♥","~(˘▾˘~)","(~˘▾˘)~","(｡◕‿‿◕｡)","(｡◕‿◕｡)","(¬_¬)","ʕ•ᴥ•ʔ"};
    
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

    public void setSelf() throws ClassNotFoundException{
        try{
            myself=PatientLogic.getMyself();
        } catch (DeadServer e) {
            e.printStackTrace();
            logOut();
        }

    }
    //TODO EDIT DATA?


}
