import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import infectedLocations.InfectedLocations;
import transactions.Transactions;
import transactions.TransactionsDataController;
import users.Users;
import users.UsersDataController;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


	public static void main(String[] args) {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.
	

		// Create connection string and configurations for MongoDB
		ConnectionString connectionString = new ConnectionString("mongodb+srv://root:gideon97@mycluster2.j0hmg.mongodb.net/test?retryWrites=true&w=majority");
		CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
		MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).codecRegistry(codecRegistry).build();

		// Connect to the database
		com.mongodb.client.MongoClient mongoClient = MongoClients.create(clientSettings);
		MongoDatabase db = mongoClient.getDatabase("SafeEntry");
		MongoCollection<Users> usersCollection = db.getCollection("Users", Users.class);
		MongoCollection<Transactions> TransactionsCollection = db.getCollection("Transactions", Transactions.class);
		MongoCollection<InfectedLocations> infectedLocationCollection = db.getCollection("InfectedLocations",
				InfectedLocations.class);
		
		Users n = new Users("Gideon Yip","S9711228B","password");
		UsersDataController userController = new UsersDataController();
		userController.notifyUser(n, TransactionsCollection, infectedLocationCollection);

		
	}

}
