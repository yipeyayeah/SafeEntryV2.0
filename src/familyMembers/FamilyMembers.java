package familyMembers;

import org.bson.types.ObjectId;

/**
 * Represents Family Members of an User. Each object contains id(ObjectId),
 * name(String), NRIC(String), relatedTo(String)
 */

public class FamilyMembers {

	private ObjectId id;
	private String name, nric, relatedTo;

	public FamilyMembers(String name, String nric, String relatedTo) {
		this.name = name;
		this.nric = nric;
		this.relatedTo = relatedTo;
	}

	public FamilyMembers() {
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

	public String getRelatedTo() {
		return relatedTo;
	}

	public void setRelatedTo(String relatedTo) {
		this.relatedTo = relatedTo;
	}
}
