package application;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ReportController {
	
	@FXML
	StackPane stackPane;
	@FXML
	Circle clip;
	@FXML
	Label attDays,percentDays,totalDays,elig;
	@FXML
	PieChart pie;
	
	private int tDays;
	private Organization Org;
	
	public void init(Organization Org) {
		this.Org = Org;
		 
		 LocalDate today = LocalDate.now();
		 LocalDate date = LocalDate.of(2024, 1,1);
		 int sundays = 0;
		 
		 while (!date.isAfter(today)) {
	            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
	                if(!Org.holidays.contains(date.toString())) sundays++;
	                date = date.plusDays(6);
	            }
	            date = date.plusDays(1);
	        }
		 
		 tDays = (int) ((ChronoUnit.DAYS.between(LocalDate.parse("2024-01-01"), today)) - (Org.holidays.size()+sundays));
		 System.out.println("Total = "+ tDays);
		 
		 attDays.setText("" + Org.attendance.size());
		 totalDays.setText("" + tDays);
		 float perc = (float)Org.attendance.size()/(float)tDays;
		 percentDays.setText(""+ perc * 100 + "%");
		 if(perc > 50) elig.setText("Eligible");else elig.setText("Not Eligible");
		 
		 pie.getData().addAll(new PieChart.Data("There", Org.attendance.size()),new PieChart.Data("Not There", tDays - Org.attendance.size()));
	}
	
	public static float calcAtt(ArrayList<String> holidays , int attSize) {
		LocalDate today = LocalDate.now();
		 LocalDate date = LocalDate.of(2024, 1,1);
		 int sundays = 0;
		 
		 while (!date.isAfter(today)) {
	            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
	                if(!holidays.contains(date.toString())) sundays++;
	                date = date.plusDays(6);
	            }
	            date = date.plusDays(1);
	        }
		 int total = (int) ((ChronoUnit.DAYS.between(LocalDate.parse("2024-01-01"), today)) - (holidays.size()+sundays));
		 return (float)attSize/(float)total;
		 
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
