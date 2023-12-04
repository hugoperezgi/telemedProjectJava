package gui;

import java.net.URL;
import java.util.ResourceBundle;

import controllers.BIT;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;

public class ProgressBarController implements Initializable{
    
    @FXML
    private ProgressBar pb;

    public void updrateProgressTo(double v){
        pb.progressProperty().set(v);
    }

    @FXML
    private void stop() throws InterruptedException{
        BitProgress.pleaseStopIt=true;
        BIT.pleaseStopit=true;
        Thread.sleep(2000);
        BitProgress.killTheShow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pb.progressProperty().set(0);
    }

}
