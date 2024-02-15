package application;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Signin {
	@FXML
	TextField authTokenInput;
	@FXML
	TextField User;
	@FXML
	PasswordField Password;
	@FXML
	PasswordField ConfirmPassword;
	@FXML
	Label result;
	@FXML
	Button authButton;
	
	public void Authenticate() {
		String auth = authTokenInput.getText();
		String name = User.getText();
		String pass = Password.getText();
		String confirm = ConfirmPassword.getText();
		//Organization O = new Organization();
		if(auth.equals("pf0x80x08x5")) {
			CompletableFuture.supplyAsync(()-> MongoConnection.Createauth())
			.thenAcceptAsync(token -> {
				Platform.runLater(() -> {
					result.setText("Token Generated with " + token + "!");
					result.setTextFill(Color.DARKGREEN);
				});
			},Platform::runLater);
		}else {
		if(confirm.equals(pass) && name.length() < 21) {
		CompletableFuture.supplyAsync(() -> {
			return MongoConnection.insertUserPass(auth, name, pass);
		}).thenAcceptAsync(ver ->{
		if (ver != 0)
		{
			Platform.runLater(() -> {
			result.setText("User created succesfully with ID " + ver + "!");
			result.setTextFill(Color.DARKGREEN);
			authButton.setDisable(true);
			});
		}else {
			Platform.runLater(() -> {
			result.setText("Authentication Failed... Token Incorrect or User already exists");
			result.setTextFill(Color.DARKRED);
		});
			}
		},Platform::runLater);
	}else if(!(name.length() < 21)){
		result.setText("Username should be lesser than 20 characters");
		result.setTextFill(Color.DARKRED);
	}
	else{
		result.setText("Password Confirmation is not the same as Password... Try Again");
		result.setTextFill(Color.DARKRED);
	}
		}
	}
	
	public void Return(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
