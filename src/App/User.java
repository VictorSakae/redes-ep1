package App;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class User {
	
	public enum Status { ONLINE, OFFLINE };
	
	private String userID;
	private Status currentStatus;
	private List<User> friendList;
	private InetAddress userIP;
	private int port;

	public User(String userID) {
		this.userID = userID;
		friendList = new ArrayList<User>();
		currentStatus = Status.OFFLINE;
	}
	
	/*	GETs & SETs */
	public String getUserID() {
		return userID;
	}
	public void setUserID(String iD) {
		userID = iD;
	}
	
	public List<User> getFriendList() {
		return friendList;
	}
	public void setFriendList(List<User> friendList) {
		this.friendList = friendList;
	}
	
	private void addFriend(User newFriend) {
		this.friendList.add(newFriend);
	}
	
	public Set<String> getFriendsOnline() {
		Set<String> friendsOnline = null;
		User auxUser = null;
		Iterator<User> itFriendList = getFriendList().listIterator();
		while(itFriendList.hasNext()) {
			auxUser = itFriendList.next();
			if(auxUser.getStatus().equals(Status.ONLINE)) {
				friendsOnline.add(auxUser.getUserID());
			}
		}
		return friendsOnline;
	}
	
	public Status getStatus() {
		return currentStatus;
	}
	public void setStatus(Status status) {
		currentStatus = status;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetAddress getUserIP() {
		return userIP;
	}

	public void setUserIP(InetAddress userIP) {
		this.userIP = userIP;
	}
}