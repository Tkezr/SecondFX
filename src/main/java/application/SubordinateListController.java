package application;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SubordinateListController {
 private Organization Org;
 @FXML
 ScrollPane scrollPane;
 @FXML
 Label userNameID;
 @FXML
 TextField messageTF;
 @FXML
 Button sendNotif;
 @FXML
 ToggleGroup daysToggle = new ToggleGroup();
 @FXML
 RadioButton r1,r2,r3,r4;
 
 
 ArrayList<Button> subButtons = new ArrayList<Button>();
 
 public void init(Organization Org) throws InterruptedException, ExecutionException {
	 this.Org = Org;
	 userNameID.setText("User: " + Org.userInfo.getString("user") + "     " + "User Id: " + Org.userInfo.getInteger("userid"));
	 CompletableFuture<ArrayList<ArrayList<String>>> att = CompletableFuture.supplyAsync(() ->  MongoConnection.getSubAttendance(Org.userInfo.getInteger("userid")));
	 ArrayList<ArrayList<String>> a = att.get();
	 GridPane gp = new GridPane();
	 int i = 0;
	 for(ArrayList<String> at : a) {
		Label det = new Label("Name : " + at.get(0) + "   UserID : " + at.get(1));
		CompletableFuture<ArrayList<String>> hol = CompletableFuture.supplyAsync(() ->  MongoConnection.getHolidays(Integer.parseInt(at.get(1)) , Org.userInfo.getInteger("userid")));
		CompletableFuture<ArrayList<String>> sAtt = CompletableFuture.supplyAsync(() ->  MongoConnection.getAtt(Integer.parseInt(at.get(1))));
		ArrayList<String> f_att = sAtt.get();
		ArrayList<String> f_hol = hol.get();
		
		float attPercentage = ReportController.calcAtt(f_hol, f_att.size());
		Label percentage = new Label("                    Percentage : " + attPercentage*100);
		
		Button notifyButton = new Button("Send Message");
		notifyButton.setOnAction(event -> {
			try {
				notifySubordinate(event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		gp.add(det, 0, i);
		gp.add(percentage, 1, i);
		gp.add(notifyButton, 3,i);
		i++;
	 }
	 scrollPane.setContent(gp);
 }
 
 public void notifySubordinate(ActionEvent e) throws IOException {
	 Node source = (Node) e.getSource();
	    Stage primaryStage = (Stage) source.getScene().getWindow();
	    primaryStage.getScene().getRoot().setDisable(true);
		FXMLLoader level = new FXMLLoader(getClass().getResource("SendMessage.fxml"));
		Parent root = level.load();
		messageTF = (TextField) level.getNamespace().get("messageTF");
		sendNotif = (Button) level.getNamespace().get("sendNotif");
		r1 = (RadioButton) level.getNamespace().get("radioButton1");
		r2 = (RadioButton) level.getNamespace().get("radioButton2");
		r3 = (RadioButton) level.getNamespace().get("radioButton3");
		r4 = (RadioButton) level.getNamespace().get("radioButton4");
		r1.setToggleGroup(daysToggle);
		r2.setToggleGroup(daysToggle);
		r3.setToggleGroup(daysToggle);
		r4.setToggleGroup(daysToggle);
		daysToggle.selectToggle(r3);
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Popup Window");
	 	Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		sendNotif.setOnAction(ev -> {
			if(messageTF.getText()!="") {
			 String selectedRadioButton = ((RadioButton) daysToggle.getSelectedToggle()).getText();
		     LocalDate endDate = LocalDate.now();
			 switch(selectedRadioButton) {
		     case "1 Day":	endDate.plusDays(1); break;
		     case "2 Days":	endDate.plusDays(3); break;
		     case "1 Week":	endDate.plusWeeks(1); break;
		     case "2 Weeks": endDate.plusWeeks(2); break;
		     }
			  MongoConnection.sendNotification(endDate.toString(),"Mr./Mrs./Ms. " + Org.userInfo.getString("user") + " - " + messageTF.getText(), Org.userInfo.getInteger("userid"));
			stage.close();
			primaryStage.getScene().getRoot().setDisable(false);
			}
			});
		
		stage.setOnCloseRequest(eve -> {
            primaryStage.getScene().getRoot().setDisable(false);
        });
 }
 
 public void sendNotification(ActionEvent e) {
	 RadioButton selectedRadioButton = (RadioButton) daysToggle.getSelectedToggle();
     if (selectedRadioButton != null) {
         String selectedOption = selectedRadioButton.getText();
         System.out.println("Selected option: " + selectedOption);
     } else {
         System.out.println("No option selected");
     }
 }
 
 public void addSubordinate(ActionEvent e) throws ExecutionException {
		Node source = (Node) e.getSource();
	    Stage primaryStage = (Stage) source.getScene().getWindow();
	    primaryStage.getScene().getRoot().setDisable(true);
	        
	    	Label popupLabel = new Label("Enter UserID of subordinate");
	        TextField popupTextField = new TextField();
	        Button submitButton = new Button("Submit");
	        Button returnHome = new Button("Return");
	        Label errLabel = new Label("");
	        VBox popupLayout = new VBox(10);
	        popupLayout.getChildren().addAll(popupLabel,popupTextField, submitButton,returnHome,errLabel);
	        
	        Stage popupStage = new Stage();
	        popupStage.initModality(Modality.APPLICATION_MODAL);
	        popupStage.setTitle("Popup Window");
	        
	        submitButton.setOnAction(ev -> {
	            String text = popupTextField.getText();
	            int uid = Integer.parseInt(text);
	            CompletableFuture<String> ans = CompletableFuture.supplyAsync(() -> MongoConnection.addSubordinate(uid, Org.userInfo.getInteger("userid")));
	            try {
					text = ans.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            errLabel.setText(text);
	            submitButton.setDisable(true);
	        });
	        
	        returnHome.setOnAction(ev -> {
	        	popupStage.close();
	            primaryStage.getScene().getRoot().setDisable(false);
	        });
	        
	        popupStage.setOnCloseRequest(ev -> {
	            primaryStage.getScene().getRoot().setDisable(false);
	        });
	        
	        Scene popupScene = new Scene(popupLayout, 300, 200); 
	        popupStage.setScene(popupScene);
	        popupStage.setResizable(false);
	        popupStage.show();
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
