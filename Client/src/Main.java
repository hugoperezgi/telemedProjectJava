import controllers.*;
import gui.*;
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

    private static ClientThread mainThread;

    @Override
    public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/servergui.fxml"));
		Parent root = loader.load();

        // ServerGUIController servControl = loader.getController();
		// servControl.setMainThread(mainThread);

        // Image windowIcon = new Image("img/logo.png");
        // primaryStage.getIcons().add(windowIcon);	

        primaryStage.setTitle("Server"); 
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        
        //Creating the main Client thread prior to launching the GUI
        mainThread = new ClientThread();
        mainThread.setDaemon(true);
        mainThread.start();

        //Start the GUI
        launch(args);

    }
    
}
