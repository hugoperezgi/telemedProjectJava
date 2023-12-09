import java.io.IOException;

public class Main2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Runtime.getRuntime().exec("java --module-path ./lib/javafx/lib/ --add-modules javafx.controls,javafx.fxml -jar ServerCore.jar");
    }
}
