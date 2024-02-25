package application;
	
import java.io.IOException;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class Main extends Application  {
	@Override
	public void start(Stage stage) throws IOException {
		//Group root = new Group();
		
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		
		Scene scene = new Scene(root);
		
		stage.setTitle("IDX Gen Pvt Ltd.");
		Image icon = new Image("logo.png");
		stage.getIcons().add(icon);
		stage.setResizable(false);
		

		stage.setScene(scene);
		stage.show();
		}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
