package application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	@FXML
	ImageView img;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	private Organization O;
	
	public void assignO(Organization O) {
		this.O = O;
	}
	
	public void signin(ActionEvent e) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Signin.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return true; 
	    } catch(NullPointerException e) {
	        return true;
	    }
	    return false;
	}
	
	public void validateLogin(ActionEvent e) throws IOException, InterruptedException, ExecutionException {
		
//		if(isInteger(idInput.getText())) return;
		int id = Integer.parseInt(idInput.getText());
		String pw = pwInput.getText();
		
		Login.setDisable(true);
		Error.setTextFill(Color.BLACK);
		Error.setText("Verifying Credentials please wait...");
		
		O = new Organization();	
		 CompletableFuture.supplyAsync(() ->  {
			try {
				return O.verifyLogin(id, pw);
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
			return null;
		}).orTimeout(12, TimeUnit.SECONDS)
		 .thenAcceptAsync(res -> {
			    if (res) {
			        Platform.runLater(() -> {
			                FXMLLoader level = new FXMLLoader(getClass().getResource("UserHome.fxml"));
			                try {
								root = level.load();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			                
			                UserHomeController u = level.getController();
			                u.init(O);
			                
			                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			                scene = new Scene(root);
			                stage.setScene(scene);
			                stage.show();
			            
			        });
			    } else {
			        Platform.runLater(() -> {
			            Error.setText("Invalid credentials or error occurred during data loading");
			            Error.setTextFill(Color.DARKRED);
			            Login.setDisable(false);
			        });
			    }
			}, Platform::runLater)
		 .exceptionallyAsync(throwable -> {
				Platform.runLater(() -> {
					Error.setTextFill(Color.DARKRED);
					Error.setText("Connection Error Ocuured Try Again");
					Login.setDisable(false);
				});
				return null;
			}, Platform::runLater);
	}
}
