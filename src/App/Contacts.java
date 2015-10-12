package App;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Contacts {

	private List<User> listUsers = new LinkedList<User>();
	private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
	
	public void addContact (String newUserID) {
		boolean registeredUser = false;
		Iterator<User> itListContacts = listUsers.listIterator();
		while(itListContacts.hasNext()) {
			User element = itListContacts.next();
			registeredUser = element.getUserID().equals(newUserID);
		}
	
		if(!registeredUser) {
			User newUser = new User(newUserID);
			listUsers.add(newUser);
		}
	}

	public User getContact (String userID) {
		Iterator<User> itListContacts = listUsers.listIterator();
		while(itListContacts.hasNext()) {
			User element = itListContacts.next();
			if(element.getUserID().equals(userID)) return element;
		}
		return null;
	}
	
	public void addOnline(String userID, ObjectOutputStream output) {
		mapOnlines.put(userID, output);
	}
	
	public Map<String, ObjectOutputStream> getMapOnlines( ){
		return mapOnlines;
	}
	
	public void removeOnline(String userID) {
		mapOnlines.remove(userID);
	}
	
}
