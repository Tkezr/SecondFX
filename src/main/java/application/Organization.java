package application;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bson.Document;


public class Organization {
	
	private static String[] Tokens = {"a1234","b1234","c1234","d1234"};
	private static HashMap<String, String> members = new HashMap<String, String>();
	public Document userInfo;
	
	Organization(){
		if(!members.containsKey("Admin")) members.put("Admin", "pass@123");
	}
	private static void addUser(String user, String pass) {
        members.put(user,pass);
    }
	
	public boolean verifyLogin(String user, String pass) throws InterruptedException, ExecutionException {
		
			CompletableFuture<Document> ver = CompletableFuture.supplyAsync(() ->  MongoConnection.searchUserPass(user, pass));
			userInfo = ver.get();
			
			if(userInfo != null)
			{
				
				System.out.println(userInfo.toJson());
				return true;
			}
			return false;
		
		
	}
	
	public boolean createUser(String authToken, String user, String password) {
		boolean bToken = false;int i = 0;
		for(i = 0; i < Tokens.length; i++) {
			if(authToken.equals(Tokens[i]))
			{
				bToken = true;break;
			}
		}
		if(!bToken) return false;
		if(members.containsKey(user)) return false;
		String[] newTokens = new String[Tokens.length - 1];
		for(int j = 0; j < Tokens.length - 1; j++)
		{
			if(j!=i) {
				newTokens[j] = Tokens[j];
			}
		}
		
		Tokens = newTokens;
		addUser(user,password);
		return true;
	}
}
