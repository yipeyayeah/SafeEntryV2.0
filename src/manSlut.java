import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import transactions.Transactions;
import users.Users;
import users.UsersDataController;

public class manSlut {
	static Scanner cc = new Scanner(System.in);
	
	public static Users createNewUserDialogue() {
		System.out.println("Registration selected");
		String name = cc.nextLine();
		System.out.println("Enter Name: ");
		name = cc.nextLine();
		System.out.println("Enter NRIC: ");
		String nric = cc.nextLine();
		System.out.println("Enter Password: ");
		String password = cc.nextLine();
		System.out.println("Officer (Y/N): ");
		String officer = cc.nextLine();
		Users newUser = new Users();
		if(officer.equals("Y")) {
			newUser = new Users(name, nric, "Officer", password);
		}else {
			newUser = new Users(name, nric, password);
		}
		return newUser;
	}
	
	public static Users loginDialogue() {
		String nric = cc.nextLine();
		System.out.println("Login selected");
		System.out.println("Enter NRIC:");
		nric = cc.nextLine();
		System.out.print("Enter Password:");
		String password = cc.nextLine();
		Users loginUser = new Users(nric, password);
		return loginUser;
	}
	
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
		
		UsersDataController userController = new UsersDataController();
		
		System.out.print("Select action:\n1 for Registration\n2 for Login\n");
		int choice = cc.nextInt();
		
		switch (choice) {
		case 1:
			Users newUser = createNewUserDialogue();
			System.out.println("Creating new user!");
			System.out.println("Results: "+ userController.createAccount(newUser, usersCollection));
			break;
		case 2:
			Users loginUser = loginDialogue();
//			if(userController.login(loginUser, usersCollection)) {
//				System.out.println("Welcome "+ loginUser.getName());
//			}else {
//				System.out.println("Try again!");
//			}
			break;
		default:
			System.out.println("Invalid choice");
		}
	}

}
