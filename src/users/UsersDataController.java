package users;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bson.conversions.Bson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import infectedLocations.InfectedLocations;
import transactions.Transactions;

public class UsersDataController {

	public boolean createAccount(Users user, MongoCollection<Users> collection) {
		try {
			// Insert the object into the dB
			collection.insertOne(user);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Users login(Users user, MongoCollection<Users> collection) {
		try {
			Bson nricFilter = Filters.eq("nric", user.getNric());
			Bson passwordFilter = Filters.eq("password", user.getPassword());
			// Find the user based on these filters
			Users userfound = collection.find(Filters.and(nricFilter, passwordFilter)).first();

			if (userfound != null) {
				return userfound;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String notifyUser(Users user, MongoCollection<Transactions> transactionCollection,
			MongoCollection<InfectedLocations> infectedCollection) {

		try {
			// Create 2 new filters based on the name and location
			Bson nameFilter = Filters.eq("name", user.getName());
			Bson locationFilter = Filters.eq("nric", user.getNric());

			ArrayList<Transactions> transactionsList = new ArrayList<Transactions>();

			// Find the user based on these filters
			MongoCursor<Transactions> transCursor = transactionCollection.find(Filters.and(nameFilter, locationFilter))
					.iterator();
			try {
				while (transCursor.hasNext()) {
					transactionsList.add(transCursor.next());
				}
			} finally {
				transCursor.close();
			}

			ArrayList<InfectedLocations> infectedList = new ArrayList<InfectedLocations>();

	
			MongoCursor<InfectedLocations> infectedCursor = infectedCollection.find().iterator();
			try {
				while (infectedCursor.hasNext()) {
					infectedList.add(infectedCursor.next());
				}
			} finally {
				infectedCursor.close();
			}


			
			String message = null;
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			
			
			for (int infectedCounter = 0; infectedCounter < infectedList.size(); infectedCounter++) {
				
				for(int transCounter = 0; transCounter < transactionsList.size(); transCounter++) {
					
					if(transactionsList.get(transCounter).getLocation().equals(infectedList.get(infectedCounter).getLocation())) {
						
						LocalTime start = LocalDateTime.parse(infectedList.get(infectedCounter).getCheckInTime(), fmt).toLocalTime();
						LocalTime stop = LocalDateTime.parse(infectedList.get(infectedCounter).getCheckOutTime(), fmt).toLocalTime();
						LocalTime target = LocalDateTime.parse(transactionsList.get(transCounter).getCheckInTime(), fmt).toLocalTime();

						if(target.isAfter(start) && target.isBefore(stop)) {
							message += "\n\nLocation: "+ infectedList.get(infectedCounter).getLocation();
							message += "\nCheck-in Time: "+ infectedList.get(infectedCounter).getCheckInTime();
							message += "\nCheck-out Time: "+ infectedList.get(infectedCounter).getCheckOutTime();
							
						}
						

						
					}
				}
			
	
			
			}
			return message;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
