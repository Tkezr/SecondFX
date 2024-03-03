package application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UserHomeController {
	
	@FXML
	Label userInfo,errorLabel;
	@FXML
	ImageView userIcon;
	@FXML
	Button nextMonth,addSub,subDetails;
	@FXML
	Button prevMonth,markAtnd,confirmHoliday;
	@FXML
	StackPane stackPane;
	@FXML
	GridPane calendarPane;
	@FXML
	GridPane holCalendarPane;
	@FXML
	Label monthYear;
	@FXML
	ScrollPane notificationBar;
	@FXML
	TextField reasonField;

	String tempS;
	Organization Org;
	YearMonth currentYearMonth;
	String dateToMarkHoliday = "",reasonForHoliday = "Hell";
	
	
	public Document user;
	
	public void init(Organization Org) {
		this.Org = Org;
		userInfo.setText("User: " + Org.userInfo.getString("user") + "     " + "User Id: " + Org.userInfo.getInteger("userid"));
		userIcon.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(Org.userInfo.getString("image")))));
		loadNotifications();
		currentYearMonth = YearMonth.now();
        updateCalendar(calendarPane, currentYearMonth);
        updateMonthYear();
        boolean shouldPerms = !(Org.userInfo.getString("status").equals("subordinate"));
        addSub.setVisible(shouldPerms);
        subDetails.setVisible(shouldPerms);
        
		}
	
	private void loadNotifications() {
		VBox tempBox = new VBox(5);
		for(int i = 1; i <= Org.notifications.size(); i++)
		{
			Label label = new Label(i+ ". " + Org.notifications.get(i-1));
			tempBox.getChildren().add(label);
		}
		notificationBar.setContent(tempBox);
		
	}
	
	public void markHoliday(ActionEvent e) throws InterruptedException, ExecutionException {
		System.out.println(dateToMarkHoliday);
		if(dateToMarkHoliday != "") {
			MongoConnection.markHoliday(dateToMarkHoliday, reasonForHoliday, Org.userInfo.getInteger("userid",0));
		}
	}
	
	public void openHoliday(ActionEvent e) throws IOException {
		Node source = (Node) e.getSource();
	    Stage primaryStage = (Stage) source.getScene().getWindow();
	    primaryStage.getScene().getRoot().setDisable(true);
		FXMLLoader level = new FXMLLoader(getClass().getResource("MarkHoliday.fxml"));
		Parent root = level.load();
		holCalendarPane = (GridPane) level.getNamespace().get("holCalendarPane");
		confirmHoliday = (Button)level.getNamespace().get("confirmHoliday");
		reasonField = (TextField)level.getNamespace().get("reasonForHoliday");
		Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Popup Window");
   	 	Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		confirmHoliday.setOnAction(ev -> {
			reasonForHoliday = reasonField.getText();
			try {
				markHoliday(ev);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		LocalDate today = LocalDate.now();
		LocalDate thisdate = today; int i = 0;
		while(!thisdate.equals(today.plusWeeks(1))) {
		if(thisdate.getDayOfWeek()!= DayOfWeek.SUNDAY && !(Org.holidays.contains(thisdate.toString()))) {
			Button day = new Button(String.valueOf(thisdate));
        	day.setOnAction(ev -> {
        		Button thisbutton = (Button) ev.getSource();
        		dateToMarkHoliday = (thisbutton).getText();
        		thisbutton.setStyle("-fx-background-color: green");
        	});
           day.setAlignment(Pos.CENTER);
           day.setPadding(new Insets(5));
			GridPane.setConstraints(day,i,0);
			holCalendarPane.getChildren().add(day);
			i++;
		}
		thisdate = thisdate.plusDays(1);
		}
		 stage.setOnCloseRequest(ev -> {
	            primaryStage.getScene().getRoot().setDisable(false);
	        });
//		 calendarPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null || GridPane.getColumnIndex(node) != null);
//		 LocalDate today = LocalDate.now();
//		 boolean leak = false;
//	        if(today.plusDays(7).getMonthValue() != today.getMonthValue()) {
//	        	leak = true;
//	        }
//	        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
//	        int daysInMonth = yearMonth.lengthOfMonth();
//	        LocalDate firstOfMonth = yearMonth.atDay(1);
//	        
//	        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//	        for (int i = 0; i < 7; i++) {
//	            Label dayLabel = new Label(daysOfWeek[i]);
//	            dayLabel.setAlignment(Pos.CENTER);
//	            dayLabel.setPadding(new Insets(5));
//	            GridPane.setConstraints(dayLabel, i, 0);
//	            calendarPane.getChildren().add(dayLabel);
//	        }
//	        
//	        int row = 1;
//	        int col = firstOfMonth.getDayOfWeek().getValue() % 7;
//	        for (int i = 1; i <= daysInMonth; i++) {
//	            
//	            String thisdate = today.getYear() + "-" + oneTo2digit(today.getMonthValue()) + "-" + oneTo2digit(i);
//	            int diff = (int)ChronoUnit.DAYS.between(LocalDate.parse(thisdate), today);
//	            if(diff <= 5 || diff >= -5) {
//	            	Button day = new Button(String.valueOf(i));
//	            	day.setOnAction(ev -> {
//	            		markHoliday(ev);
//	            	});
//		            day.setAlignment(Pos.CENTER);
//		            day.setPadding(new Insets(5));
//		            GridPane.setConstraints(day, col, row);
//		            calendarPane.getChildren().add(day);
//	            }
//	            col = (col + 1) % 7;
//	            if (col == 0)
//	                row++;
//	        }
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
	
	private void updateMonthYear() {
		monthYear.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
	}
	
	private void updateCalendar(GridPane calendarPane, YearMonth yearMonth) {
        calendarPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null || GridPane.getColumnIndex(node) != null);
        
        if((currentYearMonth.getYear() + "-" + oneTo2digit(currentYearMonth.getMonthValue())).equals("2024-12")) {
        	nextMonth.setDisable(true);
        }else {
        	nextMonth.setDisable(false);
        }
        if((currentYearMonth.getYear() + "-" + oneTo2digit(currentYearMonth.getMonthValue())).equals("2024-01")) {
        	prevMonth.setDisable(true);
        }else {
        	prevMonth.setDisable(false);
        }
        
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = yearMonth.atDay(1);
        
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPadding(new Insets(5));
            GridPane.setConstraints(dayLabel, i, 0);
            calendarPane.getChildren().add(dayLabel);
        }
        
        int row = 1;
        int col = firstOfMonth.getDayOfWeek().getValue() % 7;
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= daysInMonth; i++) {
            Label day = new Label(String.valueOf(i));
            day.setAlignment(Pos.CENTER);
            day.setPadding(new Insets(5));
            GridPane.setConstraints(day, col, row);
            calendarPane.getChildren().add(day);
            
            String thisdate = currentYearMonth.getYear() + "-" + oneTo2digit(currentYearMonth.getMonthValue()) + "-" + oneTo2digit(i);
            System.out.println("i = "+i+" col = "+col+ " day = ");
            if(col != 0 && !(Org.holidays.contains(thisdate)) && (today.isAfter(LocalDate.parse(thisdate).minusDays(1)))) {
            if(Org.attendance.contains(thisdate))
            {
            	day.setStyle("-fx-background-color: green");
            }else {
            	day.setStyle("-fx-background-color: red");
            }
            }
            col = (col + 1) % 7;
            if (col == 0)
                row++;
        }
    }
	
	private String oneTo2digit(int val) {
		if(val < 10) return ("0" + val);
		return String.valueOf(val);
	}
	
	public void nextMonth(ActionEvent e) {
		currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar(calendarPane, currentYearMonth);
        updateMonthYear();
	}
	
	public void prevMonth(ActionEvent e) {
		currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar(calendarPane, currentYearMonth);
        updateMonthYear();
	}
	
	public void logout(ActionEvent e) throws IOException {
		FXMLLoader level = new FXMLLoader(getClass().getResource("Main.fxml"));
		Parent root = level.load();
		
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		System.out.println("Logging In");
	}
	
	public void MarkPresent(ActionEvent e) throws InterruptedException, ExecutionException {
		String today = (LocalDate.now()).toString();
		if(!(Org.attendance.contains(today)) && !Org.holidays.contains(today) && LocalDate.now().getDayOfWeek() != DayOfWeek.SUNDAY)
		{
			CompletableFuture<String> ver = CompletableFuture.supplyAsync(() ->  
			MongoConnection.markAttendance(Org.userInfo.getInteger("userid"), today));
			ver.get();
			Org.attendance.add(today);
			updateCalendar(calendarPane,currentYearMonth);
			System.out.println("Added");	
		}else {
			System.out.println("Teri maa ka bhosda");
		}
	}
	
	public void DetailsPage(ActionEvent e) throws IOException {
		FXMLLoader level = new FXMLLoader(getClass().getResource("Details.fxml"));
		Parent root = level.load();
		DetailsController d = level.getController();
		d.init(Org);
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void ReportPage(ActionEvent e) throws IOException {
		FXMLLoader level = new FXMLLoader(getClass().getResource("Report.fxml"));
		Parent root = level.load();
		ReportController r = level.getController();
		r.init(Org);
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void SLPage(ActionEvent e) throws IOException, InterruptedException, ExecutionException {
		FXMLLoader level = new FXMLLoader(getClass().getResource("SubordinateList.fxml"));
		Parent root = level.load();
		SubordinateListController sl = level.getController();
		sl.init(Org);
		Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
