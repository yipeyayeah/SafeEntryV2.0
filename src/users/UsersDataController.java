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

	public boolean checkExistingUser(Users user, MongoCollection<Users> collection) {

		// Create BSON filters based on the requirements
		Bson nricFilter = Filters.eq("nric", user.getNric());

		// Find the user based on these filters
		Users userFound = collection.find(Filters.and(nricFilter)).first();
		if (userFound == null) {
			return false;
		} else {
			return true;
		}
	}

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
			// Create BSON filters based on the requirements
			Bson nricFilter = Filters.eq("nric", user.getNric());
			Bson passwordFilter = Filters.eq("password", user.getPassword());

			// Find User based on these filters
			Users userfound = collection.find(Filters.and(nricFilter, passwordFilter)).first();

			if (userfound != null) {
				return userfound;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String notifyUser(Users user, MongoCollection<Transactions> transactionCollection,
			MongoCollection<InfectedLocations> infectedCollection) {

		try {
			/// Create BSON filters based on the requirements
			Bson nameFilter = Filters.eq("name", user.getName());
			Bson locationFilter = Filters.eq("nric", user.getNric());

			// Find the list of Transactions based on these filters
			MongoCursor<Transactions> transCursor = transactionCollection.find(Filters.and(nameFilter, locationFilter))
					.iterator();

			// Stores list of Transactions objects
			ArrayList<Transactions> transactionsList = new ArrayList<Transactions>();
			try {
				while (transCursor.hasNext()) {
					transactionsList.add(transCursor.next());
				}
			} finally {
				transCursor.close();
			}

			// Find the full list of InfectedLocations
			MongoCursor<InfectedLocations> infectedCursor = infectedCollection.find().iterator();

			// Stores list of InfectedLocations objects
			ArrayList<InfectedLocations> infectedList = new ArrayList<InfectedLocations>();
			try {
				while (infectedCursor.hasNext()) {
					infectedList.add(infectedCursor.next());
				}
			} finally {
				infectedCursor.close();
			}

			String message = "";
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

			// Checks if the Transaction's check in time is within the Infected Location's
			// Check-in and Check-out time
			for (int infectedCounter = 0; infectedCounter < infectedList.size(); infectedCounter++) {

				for (int transCounter = 0; transCounter < transactionsList.size(); transCounter++) {

					if (transactionsList.get(transCounter).getLocation()
							.equals(infectedList.get(infectedCounter).getLocation())) {

						LocalTime locationCheckInTime = LocalDateTime
								.parse(infectedList.get(infectedCounter).getCheckInTime(), fmt).toLocalTime();
						LocalTime locationCheckOutTime = LocalDateTime
								.parse(infectedList.get(infectedCounter).getCheckOutTime(), fmt).toLocalTime();
						LocalTime transactionTime = LocalDateTime
								.parse(transactionsList.get(transCounter).getCheckInTime(), fmt).toLocalTime();

						if (transactionTime.isAfter(locationCheckInTime)
								&& transactionTime.isBefore(locationCheckOutTime)) {
							// Craft the notification message
							message += "\n\nLocation: " + infectedList.get(infectedCounter).getLocation();
							message += "\nCheck-in Time: " + infectedList.get(infectedCounter).getCheckInTime();
							message += "\nCheck-out Time: " + infectedList.get(infectedCounter).getCheckOutTime();
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
