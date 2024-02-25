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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DetailsController {
	
	@FXML
	ImageView userIcon;
	@FXML
	Label imagePath,userInfo,currName,newName;
	@FXML
	Button imgUpload,nameUpload;
	@FXML
	TextField TFnameChange;
	
	String name = "";
	String filePath = "";
	Organization Org;
	
	public void init(Organization Org) {
		this.Org = Org;
		userInfo.setText("User Id: " + Org.userInfo.getInteger("userid"));
		userIcon.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(Org.userInfo.getString("image")))));
		imgUpload.setText("Select Image");
		imagePath.setText("");
		TFnameChange.setVisible(false);
		newName.setVisible(false);
		currName.setText(Org.userInfo.getString("user"));
	}
	
	public void UploadImage(ActionEvent e) throws IOException, InterruptedException, ExecutionException {
		if(filePath.equals("")) {
		JFileChooser file = new JFileChooser();
		int res = file.showOpenDialog(null);
		if(res == JFileChooser.APPROVE_OPTION) {
			filePath = file.getSelectedFile().toString();
			if(!(filePath.toLowerCase().endsWith(".png") || filePath.toLowerCase().endsWith(".jpg"))){
				imagePath.setText("Current Selected image : Not a png or jpg try again");
			}
			else {
				imagePath.setText("Current Selected image : '" + filePath + "'");
				imgUpload.setText("Upload Image");
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
	
	public void nameUpdate(ActionEvent e) throws InterruptedException, ExecutionException {
		if(TFnameChange.getText().equals("")) {
			TFnameChange.setVisible(true);
			newName.setVisible(true);
			nameUpload.setText("Update Name");
		}
		else {
			this.name = TFnameChange.getText();
			CompletableFuture<Void> task = CompletableFuture.runAsync(() -> MongoConnection.sendUsername(name, Org.userInfo.getInteger("userid")));
			 
			task.get();
			TFnameChange.setVisible(false);
			newName.setVisible(false);
			nameUpload.setText("Change Name");
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
