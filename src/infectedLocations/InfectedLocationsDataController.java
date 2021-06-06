package infectedLocations;

import com.mongodb.client.MongoCollection;
public class InfectedLocationsDataController {
	
	public boolean declareInfectedLocation(InfectedLocations location, MongoCollection<InfectedLocations> collection) {
		try {
			// Insert the object into the dB
			collection.insertOne(location);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
