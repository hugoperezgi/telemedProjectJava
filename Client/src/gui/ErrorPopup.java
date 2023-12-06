package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ErrorPopup {

	/**
	 * When called it displays a new window with an error msg in function of the int
	 * passed to the function
	 * <p> {@code 0} General error (unspecified)
	 * <p> {@code 1} Not a valid birhtdate (future birthdate)
	 * <p> {@code 2} Please fill all the options
	 * <p> {@code 4} Id doesnt exist (no match found/not valid format)
	 * <p> {@code 5} Wrong username or password.
	 * <p> {@code 7} No treatment to display
	 * <p> {@code 9} No patients to display
	 * <p> {@code 11} Could not retrieve your profile
	 * <p> {@code 12} Server error
	 * <p> {@code 13} No medical tests for the selected patient
	 * <p> {@code 14} Date cannot be on the past
	 * <p> {@code 17} New password == Old password
	 * <p> {@code 21} Admins cannot be edited.
	 * <p> {@code 22} Patient and doctor already linked
	 * <p> {@code 23} User creation process not finished
	 * <p> {@code 24} Bitalino error
	 * <p> {@code X} idk keep adding stuff here...
	 * 
	 * @param errorType - int
	 * @throws IOException
	 */
	public void errorPopup(int errorType) throws IOException {
		FXMLLoader loaderError;
		Parent rootError;
		Scene sceneError;
		Stage stageError;
		Image icon;
		ErrorPopupController errorPopupController;
		switch (errorType) {
		case 0:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Something went wrong, please check everything and try again.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error 0");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 1:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Please, use a correct date.\nYour birthdate cant be on the future.");
			sceneError = new Scene(rootError);
			stageError = new Stage();

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setScene(sceneError);
			stageError.setTitle("Error: Wrong date");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 2:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Please, fill all the options.");
			sceneError = new Scene(rootError);
			stageError = new Stage();

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setScene(sceneError);
			stageError.setTitle("Error: Fill all the options");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 4:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("No match found.\nPlease try again.");
			sceneError = new Scene(rootError);
			stageError = new Stage();

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);
			stageError.setScene(sceneError);
			stageError.setTitle("Error: Wrong Id");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 5:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Wrong username or password.\nPlease try again.");
			sceneError = new Scene(rootError);
			stageError = new Stage();

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setScene(sceneError);
			stageError.setTitle("Error: Wrong username or password");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 7:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("There are no reports to display");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error: No treatments");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 9:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("No current patients for you");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Patient error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 11:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Could not retrieve your profile");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 12:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Server Error.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 13:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("No medical tests available \nfor the selected patient");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 14:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Date must be today or in the future.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Wrong date");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 17:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("New password and old password are the same");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Password error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 21:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Admins cannot be edited.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Wrong Date");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 22:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("They're already linked.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 23:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Please finish creating the user.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error");
			stageError.setResizable(false);
			stageError.show();
			break;
		case 24:
			loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
			rootError = loaderError.load();
			errorPopupController = loaderError.getController();
			errorPopupController.displayErrorText("Error with Bitalino.");
			sceneError = new Scene(rootError);
			stageError = new Stage();
			stageError.setScene(sceneError);

			icon = new Image("img/errorIcon.png");
			stageError.getIcons().add(icon);

			stageError.setTitle("Error");
			stageError.setResizable(false);
			stageError.show();
			break;
		default:
			break;
		}
	}


	public void errorPopup(String errMsg) throws IOException{
		FXMLLoader loaderError;
		Parent rootError;
		Scene sceneError;
		Stage stageError;
		Image icon;
		ErrorPopupController errorPopupController;

		loaderError = new FXMLLoader(getClass().getResource("errorPopup.fxml"));
		rootError = loaderError.load();
		errorPopupController = loaderError.getController();
		errorPopupController.displayErrorText("Error with Bitalino:\n"+errMsg);
		sceneError = new Scene(rootError);
		stageError = new Stage();
		stageError.setScene(sceneError);

		icon = new Image("img/errorIcon.png");
		stageError.getIcons().add(icon);

		stageError.setTitle("Error");
		stageError.setResizable(false);
		stageError.show();
	}
}
