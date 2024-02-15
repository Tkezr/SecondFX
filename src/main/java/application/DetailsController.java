package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DetailsController {
	
	@FXML
	ImageView userIcon;
	@FXML
	Label imagePath;
	@FXML
	Button imgUpload;
	@FXML
	Label userInfo;
	
	String filePath;
	Organization Org;
	boolean ready;
	
	public void init(Organization Org) {
		this.Org = Org;
		userInfo.setText("User: " + Org.userInfo.getString("user") + "     " + "User Id: " + Org.userInfo.getInteger("userid"));
		userIcon.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(Org.userInfo.getString("image")))));
		ready = false;
	}
	
	public void UploadImage(ActionEvent e) throws IOException, InterruptedException, ExecutionException {
		if(!ready) {
		JFileChooser file = new JFileChooser();
		int res = file.showOpenDialog(null);
		if(res == JFileChooser.APPROVE_OPTION) {
		filePath = file.getSelectedFile().toString();
		if(!(filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg"))){
			imagePath.setText("Current Selected image : Not a png or jpg try again");
			ready = false;
		}
		else {
			imagePath.setText("Current Selected image : '" + filePath + "'");
			ready = true;
		}
		}
		}else {
		byte[] rdimg = Files.readAllBytes(Paths.get(filePath));
		 String b64 = Base64.getEncoder().encodeToString(rdimg);
		 CompletableFuture<Void> task = CompletableFuture.runAsync(() -> MongoConnection.sendimg(b64,Org.userInfo.getInteger("userid", 0)));
		 
		 task.get();
		 imgUpload.setDisable(true);
		 imagePath.setText("Image Changed restart to see changes");
		}
	}
	
	public void back(ActionEvent e) throws IOException {
		FXMLLoader level = new FXMLLoader(getClass().getResource("UserHome.fxml"));
		Parent root = level.load();
		
        
        UserHomeController u = level.getController();
        u.init(Org);
        
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
	}
}
