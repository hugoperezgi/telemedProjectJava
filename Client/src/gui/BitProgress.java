package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class BitProgress {

    public static boolean pleaseStopIt=false;

    private static ProgressBarController pc;
    private static FXMLLoader loaderPBC;
    private static Parent rootPBC;
    private static Scene scenePBC;
    private static Stage stagePBC;
    
    public void createWindow() throws IOException{
		Image icon; 

        loaderPBC = new FXMLLoader(getClass().getResource("BitProgress.fxml"));
        rootPBC = loaderPBC.load();
        pc = loaderPBC.getController();

        scenePBC = new Scene(rootPBC);
        stagePBC = new Stage();
        stagePBC.setScene(scenePBC);

        icon = new Image("img/logo.png");
        stagePBC.getIcons().add(icon);

        stagePBC.setTitle("Getting Data from BITalino");
        stagePBC.setResizable(false);
        stagePBC.show();

    }

    public static void updateProgress(double v){
        pc.updrateProgressTo(v);
    }

    public static void killTheShow(){
        stagePBC.close();
    }

}
