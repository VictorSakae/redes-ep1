package App;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {
	
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