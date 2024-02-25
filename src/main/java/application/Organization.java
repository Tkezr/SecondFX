package application;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;


public class Organization {
	
	public Document userInfo;
	public ArrayList<String> attendance;
	public ArrayList<String> holidays = new ArrayList<>();
	
	
	public boolean verifyLogin(int user, String pass) throws InterruptedException, ExecutionException {
		
			CompletableFuture<Document> ver = CompletableFuture.supplyAsync(() ->  MongoConnection.searchUserPass(user, pass));
			CompletableFuture<ArrayList<String>> att = CompletableFuture.supplyAsync(() ->  MongoConnection.getAtt(user));
			userInfo = ver.get();
			attendance = att.get();
			int s_id = userInfo.getInteger("supid",0);
			CompletableFuture<ArrayList<String>> hol = CompletableFuture.supplyAsync(() ->  MongoConnection.getHolidays(user,s_id));
			holidays = hol.get();
			if(userInfo != null)
			{
				
				System.out.println(userInfo.toJson());
				return true;
			}
			return false;
		
		
	}
}
