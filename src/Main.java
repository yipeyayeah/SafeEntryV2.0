import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import familyMembers.FamilyMembers;
import familyMembers.FamilyMembersDataController;
import infectedLocations.InfectedLocations;
import infectedLocations.InfectedLocationsDataController;
import transactions.Transactions;
import transactions.TransactionsDataController;
import users.Users;
import users.UsersDataController;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	// Scanner for user inputs
	static Scanner cc = new Scanner(System.in);

	// Display create new user dialogue with options
	public static Users createNewUserDialogue() {
		System.out.println("~~~~~~~~~~~~~~~~ Registration selected ~~~~~~~~~~~~~~~~ ");
		String name = cc.nextLine();
		System.out.print("Enter Name: ");
		name = cc.nextLine();
		System.out.print("Enter NRIC: ");
		String nric = cc.nextLine();
		System.out.print("Enter Password: ");
		String password = cc.nextLine();
		System.out.print("Officer (Y/N): ");
		String officer = cc.nextLine();
		Users newUser = new Users();
		if (officer.equals("Y")) {
			newUser = new Users(name, nric, "Officer", password);
		} else {
			newUser = new Users(name, nric, password);
		}
		return newUser;
	}

	// Display login dialogue
	public static Users loginDialogue() {
		String nric = cc.nextLine();
		System.out.println("~~~~~~~~~~~~~~~~ Login selected ~~~~~~~~~~~~~~~~ ");
		System.out.print("Enter NRIC: ");
		nric = cc.nextLine();
		System.out.print("Enter Password: ");
		String password = cc.nextLine();
		Users loginUser = new Users(nric, password);
		return loginUser;
	}

	public static void main(String[] args) {
		// Disable logging for MongoDB, except for SEVERE warnings
		Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
		mongoLogger.setLevel(Level.SEVERE);

		// Create connection string and configurations for MongoDB
		ConnectionString connectionString = new ConnectionString(
				"mongodb+srv://root:gideon97@mycluster2.j0hmg.mongodb.net/test?retryWrites=true&w=majority");
		CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
		MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
				.codecRegistry(codecRegistry).build();

		// Connect to the database
		com.mongodb.client.MongoClient mongoClient = MongoClients.create(clientSettings);
		MongoDatabase db = mongoClient.getDatabase("SafeEntry");
		MongoCollection<Users> usersCollection = db.getCollection("Users", Users.class);
		MongoCollection<Transactions> TransactionsCollection = db.getCollection("Transactions", Transactions.class);
		MongoCollection<InfectedLocations> infectedLocationsCollection = db.getCollection("InfectedLocations",
				InfectedLocations.class);
		MongoCollection<FamilyMembers> FamilyMembersCollection = db.getCollection("FamilyMembers", FamilyMembers.class);

		// Creation of Controllers
		UsersDataController userController = new UsersDataController();
		TransactionsDataController transactionController = new TransactionsDataController();
		FamilyMembersDataController familyMemberController = new FamilyMembersDataController();
		InfectedLocationsDataController infectedLocationsDataController = new InfectedLocationsDataController();

		// Creation of objects
		Transactions newTransaction = new Transactions();
		boolean logout = false;
		String message;
		FamilyMembers familyMember = new FamilyMembers();
		ArrayList<Transactions> transactionList = new ArrayList<Transactions>();
		ArrayList<FamilyMembers> familyMembersList = new ArrayList<FamilyMembers>();
		ArrayList<Transactions> familyTransList = new ArrayList<Transactions>();


		System.out.print("~~~~~~~~~~~~~~~~ Starting TraceTogether ~~~~~~~~~~~~~~~~ ");

		while (true) {
			System.out.println("\n\nSelect 1 for registration\nSelect 2 for login\nSelect 3 to exit\n");
			int choice = cc.nextInt();
			switch (choice) {
			case 1:
				Users newUser = createNewUserDialogue();
				message = userController.createAccount(newUser, usersCollection) ? "User registration success."
						: "User registration failure. Please try again";
				System.out.println(message);
				break;
			case 2:
				Users loginUser = loginDialogue();
				loginUser = userController.login(loginUser, usersCollection);
				if (loginUser != null) {
					System.out.println("~~~~~~~~~~~~~~~~ Welcome " + loginUser.getName() + " ~~~~~~~~~~~~~~~~ ");

					while (true) {

						if (loginUser.getUserType().equals("Officer")) {
							message = "\nEnter 1 to view infected locations\nEnter 2 to declare infected COVID location\nEnter 3 to log out";
						} else {
							message = "\nEnter 1 for Self Check-in\nEnter 2 for Self Check-out\nEnter 3 for Group Check-in\nEnter 4 for Group Check-out\nEnter 5 to view history\nEnter 6 to view possible exposure\nEnter 7 to add new family member\nEnter 8 to delete existing family member\nEnter 9 to log out";
						}
						System.out.println(message);
						choice = cc.nextInt();
						switch (choice) {
						case 1:
							System.out.println("\nSelf Check-in selected! Processing.....");
							newTransaction = new Transactions(loginUser.getName(), loginUser.getNric(), "Compass One");

							message = transactionController.selfCheckIn(newTransaction, TransactionsCollection)
									? "Self Check-in success!"
									: "Self Check-in failure!";
							System.out.println(message);
							break;

						case 2:
							System.out.println("\nSelf Check-out selected! Processing.....");
							newTransaction = new Transactions(loginUser.getName(), loginUser.getNric(), "Compass One");

							message = transactionController.selfCheckOut(newTransaction, TransactionsCollection)
									? "Self Check-out success!"
									: "Self Check-out failure!";
							System.out.println(message);
							break;

						case 3:
							System.out.println("\nGroup Check-in selected! Processing.....");
					
							familyMembersList = familyMemberController.retrieveAllFamilyMembers(loginUser,
									FamilyMembersCollection);
							if (familyMembersList.isEmpty()) {
								System.out.println("No family member found!");
							} else {
								familyTransList.removeAll(familyTransList);
								for (int counter = 0; counter < familyMembersList.size(); counter++) {
									familyTransList.add(new Transactions(familyMembersList.get(counter).getName(), familyMembersList.get(counter).getNric(), "Compass One"));
								}
								
								message = transactionController.groupCheckIn(familyTransList, TransactionsCollection)
										? "Group Check-in success!"
										: "Group Check-in failure!";
								System.out.println(message);
								System.out.println(message);
							
							}
							break;

						case 4:
							System.out.println("\nGroup Check-out selected! Processing.....");
							familyMembersList = familyMemberController.retrieveAllFamilyMembers(loginUser,
									FamilyMembersCollection);
							if (familyMembersList.isEmpty()) {
								System.out.println("No family member found!");
							} else {
								familyTransList.removeAll(familyTransList);
								for (int counter = 0; counter < familyMembersList.size(); counter++) {
									familyTransList.add(new Transactions(familyMembersList.get(counter).getName(), familyMembersList.get(counter).getNric(), "Compass One"));
								}
								
								message = transactionController.groupCheckOut(familyTransList, TransactionsCollection)
										? "Group Check-in success!"
										: "Group Check-in failure!";
								System.out.println(message);
								System.out.println(message);
								
							}
							break;
						case 5:
							System.out.println("\nView history selected!\n");
							transactionList = transactionController.viewHistory(loginUser, TransactionsCollection);
							for (int counter = 0; counter < transactionList.size(); counter++) {
								System.out.println("----------------Record " + (counter + 1) + "------------------");
								System.out.println("Location: " + transactionList.get(counter).getLocation());
								System.out.println("CheckInTime: " + transactionList.get(counter).getCheckInTime());
								System.out.println(
										"CheckOutTime: " + transactionList.get(counter).getCheckOutTime() + "\n");
							}
							break;
						case 6:
							System.out.println("\nView possible exposure selected!\n");
							message = userController.notifyUser(loginUser, TransactionsCollection,
									infectedLocationsCollection);
							if (message == null) {
								System.out.print("No possible exposures.");
							} else {
								System.out.println( "\n------------- Possible exposure -------------\n"+message);
							}
							break;

						case 7:

							if (loginUser.getUserType().equals("Officer")) {
								System.out.println("Declare COVID location");
								cc.nextLine();
								System.out.print("\nEnter location visited by COVID-19 patient: ");
								String location = cc.nextLine();

								String checkInTime = "";
								String checkInOut = "";
								String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4} (2[0-3]|[01]?[0-9]):([0-5]?[0-9]):([0-5]?[0-9])$";
								while (!checkInTime.matches(regex)) {
									System.out.print("Enter check-in time(dd/MM/yyyy HH:mm:ss): ");
									checkInTime = cc.nextLine();
								}
								while (!checkInOut.matches(regex)) {
									System.out.print("Enter check-out time(dd/MM/yyyy HH:mm:ss): ");
									checkInOut = cc.nextLine();
								}
								InfectedLocations infectedLocation = new InfectedLocations(location, checkInTime,
										checkInOut);

								message = infectedLocationsDataController.declareInfectedLocation(infectedLocation,
										infectedLocationsCollection) ? "Record saved."
												: "Record not saved successfully. Please try again!";
								System.out.println(message);
								break;
							} else {
								cc.nextLine();
								String nric = "";
								System.out.print("\nAdd new family member\nEnter name: ");
								String name = cc.nextLine();
								String regex = "^[STFG]\\d{7}[A-Z]$";
								while (!nric.matches(regex)) {
									System.out.print("Enter NRIC: ");
									nric = cc.nextLine();
								}
								familyMember = new FamilyMembers(name, nric, loginUser.getName());
								if (familyMemberController.checkExistingFamilyMember(familyMember,
										FamilyMembersCollection)) {
									System.out.println("Record exist, please try again.");
								} else {
									message = familyMemberController.addFamilyMembers(familyMember,
											FamilyMembersCollection) ? "Family member saved."
													: "Existing family member detected. Please try again!";
									System.out.println(message);
								}
								break;
							}

						case 8:

							if (loginUser.getUserType().equals("Officer")) {
								break;
							} else {
								System.out.println("\nDelete family member selected");

								familyMembersList = familyMemberController.retrieveAllFamilyMembers(loginUser,
										FamilyMembersCollection);
								if (familyMembersList.isEmpty()) {
									System.out.println("No family member found!");
								} else {
									for (int counter = 0; counter < familyMembersList.size(); counter++) {
										System.out.println(
												"\n----------------Record " + (counter + 1) + "------------------");
										System.out.println("Name: " + familyMembersList.get(counter).getName());
										System.out.println("NRIC: " + familyMembersList.get(counter).getNric());
									}
									System.out.print("Select record to delete: ");
									int record = cc.nextInt();

									message = familyMemberController.removeFamilyMembers(
											familyMembersList.get(record - 1), FamilyMembersCollection)
													? "Record deleted."
													: "Record not deleted successfully. Please try again!";
									System.out.println(message);
								}

								break;
							}
						case 9:
							if (loginUser.getUserType().equals("Officer")) {
								break;
							} else {
								System.out.println("Logging out!");
								logout = true;
								break;
							}
						default:
							System.out.println("Invalid choice");
						}
						if (logout) {
							break;
						}
					}
				} else {
					System.out.println("Wrong credentials, please try again!");
					break;
				}
			case 3:
				if (logout) {
					logout = false;
					break;
				} else {
					System.out.println("Exiting");
					System.exit(0);
				}

			default:
				System.out.println("Invalid choice");
			}

		}
	}
}
