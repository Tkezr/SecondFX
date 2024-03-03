package application;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;


public class Organization {
	
	public Document userInfo;
	public ArrayList<String> attendance;
	public ArrayList<String> holidays = new ArrayList<>();
	public ArrayList<String> notifications;
	
	
	public boolean verifyLogin(int user, String pass) throws InterruptedException, ExecutionException {
		
			CompletableFuture<Document> ver = CompletableFuture.supplyAsync(() ->  MongoConnection.searchUserPass(user, pass));
			userInfo = ver.get();
			if(userInfo == null) return false;
			CompletableFuture<ArrayList<String>> att = CompletableFuture.supplyAsync(() ->  MongoConnection.getAtt(user));
			attendance = att.get();
			int s_id = userInfo.getInteger("supid",0);
			CompletableFuture<ArrayList<String>> hol = CompletableFuture.supplyAsync(() ->  MongoConnection.getHolidays(user,s_id));
			CompletableFuture<ArrayList<String>> msg = CompletableFuture.supplyAsync(() ->  MongoConnection.getNotifications(user,s_id));
			holidays = hol.get();
			notifications = msg.get();
			
			return true;
		
		
	}
}
