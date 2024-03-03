package application;

import java.io.IOException;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SubordinateListController {
 private Organization Org;
 @FXML
 ScrollPane scrollPane;
 
 public void init(Organization Org) throws InterruptedException, ExecutionException {
	 this.Org = Org;
	 CompletableFuture<ArrayList<ArrayList<String>>> att = CompletableFuture.supplyAsync(() ->  MongoConnection.getSubAttendance(Org.userInfo.getInteger("userid")));
	 ArrayList<ArrayList<String>> a = att.get();
	 System.out.println(a);
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
		gp.add(det, 0, i);
		gp.add(percentage, 1, i);
		i++;
	 }
	 scrollPane.setContent(gp);
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
