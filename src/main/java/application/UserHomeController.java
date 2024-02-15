package application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UserHomeController {
	
	@FXML
	Label userInfo;
	@FXML
	ImageView userIcon;
	@FXML
	Button nextMonth;
	@FXML
	Button prevMonth;
	@FXML
	Button markAtnd;
	@FXML
	GridPane calendarPane;
	@FXML
	Label monthYear;
	
	Organization Org;
	YearMonth currentYearMonth;
	
	public Document user;
	
	public void init(Organization Org) {
		this.Org = Org;
		userInfo.setText("User: " + Org.userInfo.getString("user") + "     " + "User Id: " + Org.userInfo.getInteger("userid"));
		userIcon.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(Org.userInfo.getString("image")))));
		currentYearMonth = YearMonth.now();
        updateCalendar(calendarPane, currentYearMonth);
        updateMonthYear();
		}
	
	private void updateMonthYear() {
		monthYear.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());
	}
	
	private void updateCalendar(GridPane calendarPane, YearMonth yearMonth) {
        calendarPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null || GridPane.getColumnIndex(node) != null);

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
        for (int i = 1; i <= daysInMonth; i++) {
            Label day = new Label(String.valueOf(i));
            day.setAlignment(Pos.CENTER);
            day.setPadding(new Insets(5));
            GridPane.setConstraints(day, col, row);
            calendarPane.getChildren().add(day);
            
            String thisdate = currentYearMonth.getYear() + "-" + oneTo2digit(currentYearMonth.getMonthValue()) + "-" + oneTo2digit(i);
            if(Org.attendance.contains(thisdate))
            {
            	day.setStyle("-fx-background-color: green");
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
		if(!(Org.attendance.contains(today)))
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
	
}
