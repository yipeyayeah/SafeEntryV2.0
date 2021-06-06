import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import familyMembers.FamilyMembers;
import familyMembers.FamilyMembersDataController;
import infectedLocations.InfectedLocations;
import transactions.Transactions;
import users.Users;

public class newMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Disable logging for MongoDB, except for SEVERE warnings
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.SEVERE);

		// Create connection string and configurations for MongoDB
		ConnectionString connectionString = new ConnectionString(
				"mongodb+srv://root:gideon97@mycluster2.j0hmg.mongodb.net/test?retryWrites=true&w=majority");
		CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
		MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
				.codecRegistry(codecRegistry).build();

		// Connect to the database
		com.mongodb.client.MongoClient mongoClient = MongoClients.create(clientSettings);
		MongoDatabase db = mongoClient.getDatabase("SafeEntry");
		MongoCollection<FamilyMembers> famCollection = db.getCollection("FamilyMembers", FamilyMembers.class);
		FamilyMembersDataController controller = new FamilyMembersDataController();
		FamilyMembers n = new FamilyMembers("Gideon Fake Sis", "T011234C", "Gideon");
		System.out.println(controller.addFamilyMembers(n, famCollection));
//		System.out.println(controller.removeFamilyMembers(n, famCollection));
		Users me= new Users("Gideon", "S9711228B", "password");
		ArrayList<FamilyMembers> famList = controller.viewFamilyMembers(me, famCollection);
		
		for (int infectedCounter = 0; infectedCounter < famList.size(); infectedCounter++) {
			System.out.println("\n\n\nName: "+famList.get(infectedCounter).getName());
			System.out.println("NRIC: "+famList.get(infectedCounter).getNric());
			System.out.println("Related to: "+famList.get(infectedCounter).getRelatedTo());
		}
		
	}

}
