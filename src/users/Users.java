package users;

import org.bson.types.ObjectId;

public class Users {

	private ObjectId id;
	private String name, nric, userType ,password, healthStatus;
	
	public Users(String name, String nric, String password) {
		this.name = name;
		this.nric = nric;
		this.userType = "User";
		this.password = password;
		this.healthStatus = "Healthy";
	}
	
	public Users(String name, String nric, String userType, String password) {
		this.name = name;
		this.nric = nric;
		this.userType = userType;
		this.password = password;
		this.healthStatus = "Healthy";
	}
	
	public Users(String nric, String password) {
		this.nric = nric;
		this.password = password;
	}
	
	public Users() {		
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNric() {
		return nric;
	}
	public void setNric(String nric) {
		this.nric = nric;
	}
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getHealthStatus() {
		return healthStatus;
	}
	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}
	
}
