package application;

import java.io.IOException;

import org.bson.Document;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserHomeController {
	
	@FXML
	Label userInfo;
	
	public Document user;
	
	public void init(Organization Org) {
		userInfo.setText("User: " + Org.userInfo.getString("user") + "     " + "User Id: " + Org.userInfo.getInteger("userid"));
	}
	
	public void logout(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		System.out.println("Logging In");
	}
}
