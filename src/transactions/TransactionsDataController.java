package transactions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;

import users.Users;

public class TransactionsDataController {

	public boolean selfCheckIn(Transactions transaction, MongoCollection<Transactions> collection) {
		try {

			// Set the Transactions Object Type and CheckInTime
			transaction.setType("Self check-in");
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			String formatDateTime = now.format(format);
			transaction.setCheckInTime(formatDateTime);
			transaction.setCheckOutTime(null);

			// Insert the object into the dB
			collection.insertOne(transaction);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean selfCheckOut(Transactions transaction, MongoCollection<Transactions> collection) {
		try {
			// Set the Transactions Object Type and CheckInTime
			transaction.setType("Self check-in");
			
			// Create 2 new filters based on the requirements
			Bson nameFilter = Filters.eq("name", transaction.getName());
			Bson locationFilter = Filters.eq("location", transaction.getLocation());
			Bson selfCheckInFilter = Filters.eq("type", transaction.getType());
			Bson checkOutTimeFilter = Filters.eq("checkOutTime", null);

			// Find the Transactions based on these filters
			Transactions userfound = collection
					.find(Filters.and(nameFilter, locationFilter, selfCheckInFilter, checkOutTimeFilter)).first();
			LocalDateTime now = LocalDateTime.now();
			// Set the checkout time
	        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
	        String formatDateTime = now.format(format);  
	        userfound.setCheckOutTime(formatDateTime); 
			
	        //Update the database
			Document filterByGradeId = new Document("_id", userfound.getId());
			FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions()
					.returnDocument(ReturnDocument.AFTER);
			collection.findOneAndReplace(filterByGradeId, userfound, returnDocAfterReplace);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean groupCheckIn(ArrayList<Transactions> transactionList, MongoCollection<Transactions> collection) {
		try {
			for (int counter = 0; counter < transactionList.size(); counter++) {

				// Set the Transactions Object Type and CheckInTime
				transactionList.get(counter).setType("Group check-in");
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				String formatDateTime = now.format(format);
				transactionList.get(counter).setCheckInTime(formatDateTime);
				transactionList.get(counter).setCheckOutTime(null);

				// Insert the object into the dB
				collection.insertOne(transactionList.get(counter));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean groupCheckOut(ArrayList<Transactions> transactionList, MongoCollection<Transactions> collection) {
		try {
			for (int counter = 0; counter < transactionList.size(); counter++) {
				// Set the Transactions Object Type 
				transactionList.get(counter).setType("Group check-in");
				// Create BSON filters based on the requirements
				Bson nameFilter = Filters.eq("name", transactionList.get(counter).getName());
				Bson locationFilter = Filters.eq("location", transactionList.get(counter).getLocation());
				Bson selfCheckInFilter = Filters.eq("type", transactionList.get(counter).getType());
				Bson checkOutTimeFilter = Filters.eq("checkOutTime", null);

				// Find the user based on these filters
				Transactions userfound = collection
						.find(Filters.and(nameFilter, locationFilter, selfCheckInFilter, checkOutTimeFilter)).first();

			
				LocalDateTime now = LocalDateTime.now();
				DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				String formatDateTime = now.format(format);
				userfound.setCheckOutTime(formatDateTime);

				// Find the previous record by ID and update the record
				Document filterByGradeId = new Document("_id", userfound.getId());
				FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions()
						.returnDocument(ReturnDocument.AFTER);
				collection.findOneAndReplace(filterByGradeId, userfound, returnDocAfterReplace);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList<Transactions> viewHistory(Users user, MongoCollection<Transactions> collection) {
		try {
			// Create BSON filters based on the requirements
			Bson nameFilter = Filters.eq("name", user.getName());
			Bson locationFilter = Filters.eq("nric", user.getNric());

			// Find the list of Transactions based on these filters
			MongoCursor<Transactions> cursor = collection.find(Filters.and(nameFilter, locationFilter)).iterator();

			// Stores list of Transactions objects
			ArrayList<Transactions> transactionsList = new ArrayList<Transactions>();
			try {
				while (cursor.hasNext()) {
					transactionsList.add(cursor.next());
				}
			} finally {
				cursor.close();
			}

			return transactionsList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
