package infectedLocations;

import java.util.ArrayList;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class InfectedLocationsDataController {
	
	public boolean declareInfectedLocation(InfectedLocations location, MongoCollection<InfectedLocations> collection) {
		try {
			// Insert the object into the dB
			collection.insertOne(location);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<InfectedLocations> retrieveAllInfectedLocations(MongoCollection<InfectedLocations> collection) {
		try {
			// Find all InfectedLocations database records 
			MongoCursor<InfectedLocations> cursor = collection.find().iterator();
			
			// Stores list of InfectedLocations objects
			ArrayList<InfectedLocations> infectedList = new ArrayList<InfectedLocations>();
			try {
				while (cursor.hasNext()) {
					infectedList.add(cursor.next());
				}
			} finally {
				cursor.close();
			}

			return infectedList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
