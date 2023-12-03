package gui;

import java.io.IOException;

import controllers.ClientLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;

public class LoginController {

    @FXML
    TextField username;
    @FXML
    PasswordField psw;

    private ErrorPopup ErrorPopup = new ErrorPopup();


    public void logIn(ActionEvent a) throws IOException, ClassNotFoundException{
        Integer control = ClientLogic.logIn(username.getText(),psw.getText());
        switch (control) {
            case -2:
                ErrorPopup.errorPopup(5);
                break;
            case 0:
                loadAdminUI(a);
                break;
            case 1:
                loadDoctUI(a);
                break;
            case 2:
                loadPatientUI(a);
                break;
        
            default:
                ErrorPopup.errorPopup(0);
                break;
        }
    }
    

    private void loadPatientUI(ActionEvent aEvent) throws IOException, ClassNotFoundException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PatientController.fxml"));
		Parent root = loader.load();

        PatientController pc = loader.getController();
        pc.setSelf();
        if(pc.myself==null){
            ErrorPopup.errorPopup(11); //TODO
        } else {
            Stage stage = (Stage) ((Node) aEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }

    private void loadAdminUI(ActionEvent aEvent) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminController.fxml"));
		Parent root = loader.load();

        Stage stage = (Stage) ((Node) aEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    private void loadDoctUI(ActionEvent aEvent) throws IOException, ClassNotFoundException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkerController.fxml"));
		Parent root = loader.load();

        WorkerController wc = loader.getController();
        wc.setSelf();
        if(wc.myself==null){
            ErrorPopup.errorPopup(11); //TODO "COuldnt retrive your data"
        } else {
            Stage stage = (Stage) ((Node) aEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }


}
