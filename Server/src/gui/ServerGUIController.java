package gui;

import java.io.IOException;

import controllers.ClientHandler;
import controllers.ServerThread;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerGUIController {

    private static ServerThread mainThread;

    public void setMainThread(ServerThread mainThread) {
        ServerGUIController.mainThread = mainThread;
    }

    public void shutServerDown() throws Exception{
        int conusr = ClientHandler.currentConnectedUsers;
        if(conusr==10){
            mainThread.serverShutdown();
        }else{
            confirmShutdown(conusr);
        }
    }

    public void confirmShutdown(int conusr) throws IOException{

		FXMLLoader loader;
		Parent rootPop;
		Scene scenePop;
		Stage stagePop;
		ServerShutdownController confirmationController;

        loader = new FXMLLoader(getClass().getResource("confirmation.fxml"));
        rootPop = loader.load();
        confirmationController = loader.getController();
        confirmationController.setMainThread(mainThread);
        if(conusr==1){
            confirmationController.displayErrorText("Are you sure, at least 1 user is still online.");
        }else{confirmationController.displayErrorText("Are you sure, at least "+conusr+" users are still online.");}
        scenePop = new Scene(rootPop);
        stagePop = new Stage();
        stagePop.setScene(scenePop);

        stagePop.setTitle("Confirm shutdown");
        stagePop.setResizable(false);
        stagePop.show();

    }

}
