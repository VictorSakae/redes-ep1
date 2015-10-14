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
	private InetAddress userIP;
	private int port;

	public User(String userID) {
		this.userID = userID;
		currentStatus = Status.OFFLINE;
		userIP = null;
		port = -1;
	}
	
	/*	GETs & SETs */
	public String getUserID() {
		return userID;
	}
	public void setUserID(String iD) {
		userID = iD;
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