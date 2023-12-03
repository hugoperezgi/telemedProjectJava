import controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/LoginController.fxml"));
		Parent root = loader.load();

        Image windowIcon = new Image("img/logo.png");
        primaryStage.getIcons().add(windowIcon);	

        primaryStage.setTitle("Client"); 
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        
        ClientLogic.setup();

        //Start the GUI
        launch(args);

    }

}
//Fuck Java.