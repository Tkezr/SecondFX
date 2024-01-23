package application;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

public class MainController {
	
	@FXML
	TextField idInput;
	@FXML
	PasswordField pwInput;
	@FXML
	Label Error;
	@FXML
	Button Login;
	
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	
	public void signin(ActionEvent e) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Signin.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
	public void validateLogin(ActionEvent e) throws IOException, InterruptedException, ExecutionException {
		
		String id = idInput.getText();
		String pw = pwInput.getText();
		
		Login.setDisable(true);
		Error.setText("Verifying Credentials please wait...");
		
		Organization O = new Organization();
			
		 CompletableFuture.supplyAsync(() ->  {
			try {
				return O.verifyLogin(id, pw);
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
			return null;
		}).thenAcceptAsync(res -> {
			    if (res) {
			        Platform.runLater(() -> {
			            try {
			                FXMLLoader l = new FXMLLoader(getClass().getResource("UserHome.fxml"));
			                root = l.load();

			                UserHomeController u = l.getController();
			                u.init(O);

			                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			                scene = new Scene(root);
			                stage.setScene(scene);
			                stage.show();
			            } catch (IOException ex) {
			                ex.printStackTrace();
			            }
			        });
			    } else {
			        Platform.runLater(() -> {
			            Error.setText("Invalid data or error occurred during data loading");
			            Error.setTextFill(Color.DARKRED);
			            Login.setDisable(false);
			        });
			    }
			}, Platform::runLater);
		 
	}
}
