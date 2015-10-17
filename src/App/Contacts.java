package App;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import App.User.Status;

public class Contacts {

	private List<User> listUsers = new LinkedList<User>();
	private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
	

	public List<User> getListUsers() {
		return listUsers;
	}

	public void setListUsers(List<User> listUsers) {
		this.listUsers.addAll(listUsers);
	}

	
	public void addContact (String newUserID) {

		Iterator<User> itListContacts = listUsers.listIterator();
		while(itListContacts.hasNext()) {
			User element = itListContacts.next();
			if(element.getUserID().equals(newUserID)) {
				return;
			}
		}
		User newUser = new User(newUserID);
		listUsers.add(newUser);

	}

	public User getContact (String userID) {
		Iterator<User> itListContacts = listUsers.listIterator();
		while(itListContacts.hasNext()) {
			User element = itListContacts.next();
			if(element.getUserID().equals(userID)) return element;
		}
		return null;
	}
	
	public void addOnline(String userID, InetAddress ip, int port, ObjectOutputStream output) {
		Iterator<User> itListContacts = listUsers.listIterator();
		while(itListContacts.hasNext()) {
			User element = itListContacts.next();
			if(element.getUserID().equals(userID)) {
				element.setStatus(Status.ONLINE);
				element.setUserIP(ip);
				element.setPort(port);
				break;
			}
		}
		mapOnlines.put(userID, output);
	}
	
	public Map<String, ObjectOutputStream> getMapOnlines( ){
		return mapOnlines;
	}
	
	public void removeOnline(String userID) {
		mapOnlines.remove(userID);
	}
	
	public boolean isOnline(String userID) {
		return getMapOnlines().containsKey(userID);
	}
	
}
