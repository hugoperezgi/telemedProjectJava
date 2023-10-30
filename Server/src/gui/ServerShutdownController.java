package gui;

import controllers.SQL;
import controllers.ServerThread;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerShutdownController {
    
    private static ServerThread mainThread;

    public void setMainThread(ServerThread mainThread) {
        ServerShutdownController.mainThread = mainThread;
    }    

    @FXML
    Text conUsers;

    @FXML
	TextField usernameTextfieldLogIn;
	@FXML
	TextField passwordTextFieldLogIn;


	public void displayErrorText(String text) {
		conUsers.setText(text);
	}    

    public void shutdown(){

        String u = usernameTextfieldLogIn.getText();
        String p = passwordTextFieldLogIn.getText();

        try {
            User user = ServerThread.sql.checkPassword(u, SQL.hashPassword(p)); 
            if(user==null){
                errorPopup();
            } else if (user.getRole()==0){
                mainThread.serverShutdown();
            } else {
                errorPopup();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void errorPopup(){

        try {
            FXMLLoader loaderError;
            Parent rootError;
            Scene sceneError;
            Stage stageError;
            loaderError = new FXMLLoader(getClass().getResource("error.fxml"));
            rootError = loaderError.load();
            sceneError = new Scene(rootError);
            stageError = new Stage();
            stageError.setScene(sceneError);

            stageError.setTitle("Error");
            stageError.setResizable(false);
            stageError.show();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
