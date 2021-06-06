package familyMembers;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import transactions.Transactions;
import users.Users;

public class FamilyMembersDataController {

	public boolean addFamilyMembers(FamilyMembers familyMember, MongoCollection<FamilyMembers> collection) {
		try {
			// Insert the object into the dB
			collection.insertOne(familyMember);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkExistingFamilyMember(FamilyMembers familyMember, MongoCollection<FamilyMembers> collection) {
		// Create 2 new filters based on the name and location
		Bson nameFilter = Filters.eq("name", familyMember.getName());
		Bson nricFilter = Filters.eq("nric", familyMember.getNric());
		Bson relatedToFilter = Filters.eq("relatedTo", familyMember.getRelatedTo());

		// Find the user based on these filters
		FamilyMembers famMember = collection.find(Filters.and(nameFilter, nricFilter, relatedToFilter)).first();
		if (famMember == null) {
			return false;
		} else {
			return true;
		}

	}

	public boolean removeFamilyMembers(FamilyMembers familyMember, MongoCollection<FamilyMembers> collection) {
		try {
			// Insert the object into the dB
			Bson nameFilter = Filters.eq("name", familyMember.getName());
			Bson nricFilter = Filters.eq("nric", familyMember.getNric());

			collection.deleteOne(Filters.and(nameFilter, nricFilter));

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList<FamilyMembers> retrieveAllFamilyMembers(Users user, MongoCollection<FamilyMembers> collection) {
		try {
			// Create 2 new filters based on the name and location
			Bson nameFilter = Filters.eq("relatedTo", user.getName());
			ArrayList<FamilyMembers> famList = new ArrayList<FamilyMembers>();

			// Find the user based on these filters
			MongoCursor<FamilyMembers> cursor = collection.find(Filters.and(nameFilter)).iterator();
			try {
				while (cursor.hasNext()) {
					famList.add(cursor.next());
				}
			} finally {
				cursor.close();
			}

			return famList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
