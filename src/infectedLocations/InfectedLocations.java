package infectedLocations;

import org.bson.types.ObjectId;

public class InfectedLocations {
	private ObjectId id;
	private String location, checkInTime, checkOutTime;
	
	public InfectedLocations(String location, String checkInTime, String checkOutTime) {
		this.location = location;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
	}
	public InfectedLocations() {
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCheckInTime() {
		return checkInTime;
	}
	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}
	public String getCheckOutTime() {
		return checkOutTime;
	}
	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	
}
