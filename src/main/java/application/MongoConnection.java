package application;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

class UserData{
	public Document userInfo;
	
	
}

public class MongoConnection {
	
	private static String uri = "mongodb+srv://scep:R0K57a9W2VY5MJzw@data.o09hwlq.mongodb.net/?retryWrites=true&w=majority";
    public static String conn() {

        // Replace the placeholder with your MongoDB deployment's connection string
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("db");
            MongoCollection<Document> collection = database.getCollection("DataSet");
            
            
            Document doc = collection.find(eq("user", "admin")).first();
            if (doc != null) {
                return (doc.toJson());
            }
            return "notnull";
            
        }}
        
    public static Document searchUserPass(String username, String password) {
    	try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("db");
            MongoCollection<Document> collection = database.getCollection("DataSet");
            
    	Document query = new Document("user",  username);
		Document result = collection.find(query).first();
		if(result!= null) {
			if((result.getString("password")).equals(password) )
			{
				result.remove("password");
				result.remove("_id");
				result.remove("token");
				return result;
			}
		}
		
    	}catch(Error e) {
    		System.out.println(e);
    	}
    	
    	
    	return null;
    }
    
}
