package application;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;


public class Organization {
	
	public Document userInfo;
	public ArrayList<String> attendance;
	
	
	public boolean verifyLogin(int user, String pass) throws InterruptedException, ExecutionException {
		
			CompletableFuture<Document> ver = CompletableFuture.supplyAsync(() ->  MongoConnection.searchUserPass(user, pass));
			CompletableFuture<ArrayList<String>> att = CompletableFuture.supplyAsync(() ->  MongoConnection.getAtt(user));
			userInfo = ver.get();
			attendance = att.get();
			
			if(userInfo != null)
			{
				
				System.out.println(userInfo.toJson());
				return true;
			}
			return false;
		
		
	}
}
