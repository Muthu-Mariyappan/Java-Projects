package gmm.SimpleJsoupReader;

import java.io.IOException;
import java.net.UnknownHostException;


import org.jsoup.Jsoup;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;


public class App 
{
	private static final String CSS_SELECTOR = ".summarycount.al";
	private static final String[] tags = {"spring","struts"};
	private static long getCount(String URI){
		long count=0;
		try {
       	 count = Long.parseLong(Jsoup.connect(URI).get().select(CSS_SELECTOR).text().replaceAll(",",""));
		} catch (IOException e) {
			System.out.println("Bad URL or invalid data for the css selector");
		}
		return count;
	}
	
	private static DBCollection getMongoCollection(){
		MongoClient mongoClient=null;
		try {
			mongoClient = new MongoClient("localhost",27017);
		} catch (UnknownHostException e) {
			System.out.println("Error connecting to the database!");
		}
		DB database = mongoClient.getDB("javaClub");
		return database.getCollection("tagcount");
		
	}
	
	private static void copyToMongo(DBCollection collection, BasicDBObject obj){
		collection.insert(obj);
	}
	
	private static void printDocumentsFromMongo(DBCollection collection){
		System.out.println("\t\t\t\tSpring VS struts\n\n");
		for(int i=0;i<2;i++)
			System.out.println("Number of questions tagged as "+tags[i]+" is "+
						collection.find(new BasicDBObject("_id",tags[i])).next().get(tags[i]));
	}

	public static void main( String[] args )
    {
    	long springCount= getCount("https://stackoverflow.com/questions/tagged/spring");
    	long strutsCount = getCount("https://stackoverflow.com/questions/tagged/struts");
    	DBCollection collection  = getMongoCollection();
    	copyToMongo(collection,new BasicDBObject("_id",tags[0]).append(tags[0], springCount));
    	copyToMongo(collection,new BasicDBObject("_id",tags[1]).append(tags[1], strutsCount));
    	printDocumentsFromMongo(collection);
    }
	
}