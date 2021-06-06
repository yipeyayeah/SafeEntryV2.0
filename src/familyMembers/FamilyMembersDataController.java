package familyMembers;

import java.util.ArrayList;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import users.Users;

public class FamilyMembersDataController {

	public boolean addFamilyMembers(FamilyMembers familyMember, MongoCollection<FamilyMembers> collection) {
		try {
			// Insert the object into dB
			collection.insertOne(familyMember);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkExistingFamilyMember(FamilyMembers familyMember, MongoCollection<FamilyMembers> collection) {
		// Create BSON filters based on the requirements
		Bson nameFilter = Filters.eq("name", familyMember.getName());
		Bson nricFilter = Filters.eq("nric", familyMember.getNric());
		Bson relatedToFilter = Filters.eq("relatedTo", familyMember.getRelatedTo());

		// Find FamilyMember based on these filters
		FamilyMembers famMember = collection.find(Filters.and(nameFilter, nricFilter, relatedToFilter)).first();

		if (famMember == null) {
			return false;
		} else {
			return true;
		}

	}

	public boolean removeFamilyMembers(FamilyMembers familyMember, MongoCollection<FamilyMembers> collection) {
		try {
			// Create BSON filters based on the requirements
			Bson nameFilter = Filters.eq("name", familyMember.getName());
			Bson nricFilter = Filters.eq("nric", familyMember.getNric());

			// Delete record based on filters
			collection.deleteOne(Filters.and(nameFilter, nricFilter));

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<FamilyMembers> retrieveAllFamilyMembers(Users user, MongoCollection<FamilyMembers> collection) {
		try {
			// Create BSON filters based on the requirements
			Bson nameFilter = Filters.eq("relatedTo", user.getName());

			// Find the list of Family Members based on these filters
			MongoCursor<FamilyMembers> cursor = collection.find(Filters.and(nameFilter)).iterator();

			// Stores list of Family Members objects
			ArrayList<FamilyMembers> famList = new ArrayList<FamilyMembers>();

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
