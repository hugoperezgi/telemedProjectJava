package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import controllers.ClientLogic;
import controllers.DeadServer;
import controllers.DoctorLogic;
import entities.MedicalTest;
import entities.Patient;
import entities.Worker;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class WorkerController implements Initializable {

    private ErrorPopup ErrorPopup = new ErrorPopup();
    private SuccessPopup SuccessPopup = new SuccessPopup();


    public Worker myself=null;
    private List<Patient> pList=null;
    private List<MedicalTest> mtList=null;
    private MedicalTest mtTest=null;
    private String sPatientName=null;
    private Integer ptId=null;
    private Integer plId=null;

    @FXML
    private Pane panePatientView;

        @FXML 
        ComboBox<String> comboBoxSelectedPatient;

        @FXML
        TableColumn<Patient,Integer> columnPatientId;
        @FXML
        TableColumn<Patient,String> columnPatientName;
        @FXML
        TableColumn<Patient,String> columnPatientSurname;

        @FXML
        TableView<Patient> tableViewPatients;

        @FXML
        private void showPatientReports() throws ClassNotFoundException, IOException{
            if(comboBoxSelectedPatient.getSelectionModel().isEmpty()){
                ErrorPopup.errorPopup(2);
                return;
            }
            try {
                plId=comboBoxSelectedPatient.getSelectionModel().getSelectedIndex();
                ptId=pList.get(plId).getPatientID();
                mtList=DoctorLogic.showClinicalHist(ptId);
                if(mtList==null){
                    ErrorPopup.errorPopup(12);
                    return;
                }
                sPatientName=pList.get(comboBoxSelectedPatient.getSelectionModel().getSelectedIndex()).getName();

            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }

            updateReportTable();
            hideAll();
            ffs_i();
            panePatientReportsView.setDisable(false);
            panePatientReportsView.setVisible(true);
        }

        @FXML
        private void showPatients() throws IOException, ClassNotFoundException{
            
            try {
                
                pList=DoctorLogic.showMyPatients(myself.getWorkerID());
                if(pList==null){
                    ErrorPopup.errorPopup(12);
                    return;
                }
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }
            
            hideAll();

            loadPatientTable();

            ffs();
            panePatientView.setVisible(true);
            panePatientView.setDisable(false);
        }

        private void loadPatientTable(){
            tableViewPatients.getItems().clear();
            tableViewPatients.getItems().addAll(pList);
            comboBoxSelectedPatient.getSelectionModel().clearSelection();
                String[] sList = new String[pList.size()];
                int i=0;
                for (Patient p : pList) {
                    sList[i]="Id: "+p.getPatientID()+" - ("+p.getName()+" "+p.getSurname()+")";
                    i++;
                }
            comboBoxSelectedPatient.getItems().setAll(sList);
        }

    @FXML
    private Pane panePatientReportsView;

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

        @FXML
        Text textPatientNameReportIntro;
    

        private void updateReportTable(){
            textPatientNameReportIntro.setText("Medical Reports of "+sPatientName);
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
            comboBoxSelectedReport.setPromptText("Select a Report.");

        }

        public void selectReport() throws IOException{
            if(comboBoxSelectedReport.getSelectionModel().isEmpty()){
                ErrorPopup.errorPopup(2);
                return;
            }
            int index = comboBoxSelectedReport.getSelectionModel().getSelectedIndex();
            updateSelectedReport(mtList.get(index));

            hideAll();
            ffs_i();
            paneReportView.setVisible(true);
            paneReportView.setDisable(false);

        }

        @FXML
        private void goBackToSelectPatient(){

            comboBoxSelectedPatient.getSelectionModel().clearSelection();
            comboBoxSelectedPatient.setPromptText("Select a Patient.");

            hideAll();
            ffs();
            panePatientView.setVisible(true);
            panePatientView.setDisable(false);
        }

    @FXML
    private Pane paneReportView;

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
        @FXML
        Button graphReport;
        @FXML
        CheckBox checkBoxFatigueSR;
        @FXML
        CheckBox checkBoxNauseaSR;
        @FXML
        CheckBox checkBoxHeadacheSR;
        @FXML
        CheckBox checkBoxDizzinessSR;

        private void updateSelectedReport(MedicalTest m){
            textPatientName.setText("Patient ID: "+ m.getPatientID());
            textReportDate.setText("Report date: "+m.getReportDate().toString());
            String t = m.bitalinoDataAttached();
            mtTest=m;
            if(t.contains("No")){
                downloadReport.setDisable(true);
                graphReport.setDisable(true);
            } else {
                downloadReport.setDisable(false);
                graphReport.setDisable(false);
            }

            if((m.getSympByte() & 0b00000001) == 0b00000001){checkBoxHeadacheSR.setSelected(true);}else{checkBoxHeadacheSR.setSelected(false);}
            checkBoxHeadacheSR.setDisable(true);
            if((m.getSympByte() & 0b00000010) == 0b00000010){checkBoxNauseaSR.setSelected(true);}else{checkBoxNauseaSR.setSelected(false);}
            checkBoxNauseaSR.setDisable(true);
            if((m.getSympByte() & 0b00000100) == 0b00000100){checkBoxDizzinessSR.setSelected(true);}else{checkBoxDizzinessSR.setSelected(false);}
            checkBoxDizzinessSR.setDisable(true);
            if((m.getSympByte() & 0b00001000) == 0b00001000){checkBoxFatigueSR.setSelected(true);}else{checkBoxFatigueSR.setSelected(false);}
            checkBoxFatigueSR.setDisable(true);

            textBitalinoSignalAvailable.setText(t);
            textAreaDocComments.setText(m.getDoctorComments());
            textAreaDocComments.setEditable(true);
            textAreaDocComments.setPromptText("Add a comment here.");
            textAreaPatientReport.setText(m.getPatientComments());
            textAreaPatientReport.setEditable(false);
        }

        public void downloadReportData() throws IOException{
            mtTest.getReportFile();
        }

        public void goBackToSelectReport() throws ClassNotFoundException, IOException{

            try {
                
                mtList=DoctorLogic.showClinicalHist(ptId);
                if(mtList==null){
                    ErrorPopup.errorPopup(12);
                    hideAll();
                    ffs_i();
                    return;
                }
                sPatientName=pList.get(plId).getName();

            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }

            updateReportTable();

            hideAll();

            panePatientReportsView.setVisible(true);
            panePatientReportsView.setDisable(false);
        }

        @FXML
        private void addComment() throws ClassNotFoundException, IOException{
            String txt=textAreaDocComments.getText();
            if(txt.equalsIgnoreCase(mtTest.getDoctorComments())){
                ErrorPopup.errorPopup(2);
                return;
            }
            try {
                
                if(DoctorLogic.editReport(mtTest.getTestID(),txt)==-1){
                    ErrorPopup.errorPopup(12);
                    return;
                }
                
            } catch (DeadServer e) {
                e.printStackTrace();
                logOut();
            }
            SuccessPopup.successPopup(0);
            goBackToSelectReport();

        }

    @FXML
    Pane paneGraph;

        @FXML
        LineChart bitalinoChart;
        @FXML
        NumberAxis axisX;
        @FXML
        NumberAxis axisY;

        @FXML
        private void checkDataGraph() throws IOException{
            
            hideAll();

            String data=mtTest.bitalinoParams();
            if(data==null){ErrorPopup.errorPopup(0);return;}
            String[] dataArray = data.split(" ");
            Integer[] dataValues = new Integer[dataArray.length];
            int i=0;
            for (String d : dataArray) {
                dataValues[i]=Integer.parseInt(d);
                i++;
            }
            double maxV=dataValues[0], minV=dataValues[0];
            for (Integer integer : dataValues) {
                if(integer>maxV){maxV=integer;}
                if(integer<minV){minV=integer;}
            }

            axisY.setAutoRanging(true);
            axisX.setAutoRanging(true);
            axisX.setLowerBound(0);
            axisX.setUpperBound(dataArray.length);
            axisY.setLowerBound(minV);
            axisY.setUpperBound(maxV);
            // axisY.setTickUnit((maxV-minV)/dataValues.length);
            
            XYChart.Series<Number,Number> s = new XYChart.Series<Number,Number>();
            i=0;
            for (Integer integer : dataValues) {
                s.getData().add(new Data<Number,Number>(i,integer));
                i++;
            }
            bitalinoChart.getData().clear();
            bitalinoChart.getData().add(s);
            bitalinoChart.setLegendVisible(false);
            paneGraph.setDisable(false);
            paneGraph.setVisible(true);
        }

        public void backToCheckReport(){
            hideAll();
            paneReportView.setVisible(true);
            paneReportView.setDisable(false);
        }



    @FXML
    Button buttonCheckLight;    
    @FXML
    Button buttonCheckDrk;    

        private void ffs(){
            buttonCheckDrk.setVisible(true);
            buttonCheckDrk.setDisable(false);
            buttonCheckLight.setVisible(false);;
            buttonCheckLight.setDisable(true);
        }
        private void ffs_i(){
            buttonCheckLight.setVisible(true);
            buttonCheckLight.setDisable(false);
            buttonCheckDrk.setVisible(false);
            buttonCheckDrk.setDisable(true);
        }


    @FXML
    Text randomBSGO;

        String[] ffs={"(づ｡◕‿‿◕｡)づ","(◕‿◕✿)","(ᵔᴥᵔ)","♥‿♥","~(˘▾˘~)","(~˘▾˘)~","(｡◕‿‿◕｡)","(｡◕‿◕｡)","(¬_¬)","ʕ•ᴥ•ʔ"};
    
    public void logOut(){
        ClientLogic.closeAll();
        System.exit(0);
    }

    private void hideAll(){
        Random rnd = new Random();
        randomBSGO.setText(ffs[rnd.nextInt(ffs.length)]);
        paneReportView.setVisible(false);
        paneReportView.setDisable(true);
        panePatientReportsView.setVisible(false);
        panePatientReportsView.setDisable(true);
        panePatientView.setVisible(false);
        panePatientView.setDisable(true);
        paneGraph.setDisable(true);
        paneGraph.setVisible(false);
        comboBoxSelectedPatient.setPromptText("Select a Patient.");
        comboBoxSelectedReport.setPromptText("Select a Report.");
        comboBoxSelectedPatient.getSelectionModel().clearSelection();
        comboBoxSelectedReport.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnMedTestBitalinoData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().bitalinoDataAttached()));
        columnMedTestDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReportDate().toString()));
        columnMedTestId.setCellValueFactory(new PropertyValueFactory<>("testID"));
        columnPatientId.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        columnPatientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnPatientSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));

        hideAll();

        Random rnd = new Random();
        randomBSGO.setText(ffs[rnd.nextInt(ffs.length)]);
    }

    public void setSelf() throws ClassNotFoundException{
        try{
            myself=DoctorLogic.getMyself();
        } catch (DeadServer e) {
            e.printStackTrace();
            logOut();
        }
    }

}
