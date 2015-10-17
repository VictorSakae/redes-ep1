package App;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClienteService {
    
	private Socket socket;
    private ObjectOutputStream outputServer;
    private String userID;

    
    private Contacts clientContacts = new Contacts();
  
    public ClienteService(String userID) {
    	this.userID = userID;
    	try {
			InetAddress localhost = InetAddress.getLocalHost();

			socket = connectTo(userID, localhost, 4545);
		} catch (UnknownHostException unknownHostException) {
			System.err.println(unknownHostException);
		}

        new Thread(new ListenerSocket(this.socket)).start();
    }
    
    private class ListenerSocket implements Runnable {
    	private ObjectInputStream input;
    	
    	public ListenerSocket(Socket socket) {
    		try {
    			input = new ObjectInputStream(socket.getInputStream());
    		} catch (IOException ex) {
            	Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
    		}
    	}
    	
    	public void run() {
    		ChatMessage message = null;
    		try {
    			while ((message = (ChatMessage) input.readObject()) != null) {
    				Action action = message.getAction();

    				if (action.equals(Action.FRIEND_LIST)) {
    					getFriendList(message);
    					showFriendList();
    				} else if (action.equals(Action.DISCONNECT)) {
    					socket.close();
    					return;
    				}
    			}
    		} catch (IOException ex) {
    			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
            }
    	}
    	
    	private void getFriendList(ChatMessage message) {
    		List<User> friendList = message.getFriendList();
    		clientContacts.setListUsers(message.getFriendList());
    	}
    }
    
    public Socket connectTo(String userID, InetAddress ip, int port){
        try {
        	this.socket = new Socket(ip, port);
        	this.outputServer = new ObjectOutputStream(socket.getOutputStream());
        	
        	System.out.println("Connected to "+ip.toString()+":"+String.valueOf(port));
			
        	ChatMessage message = new ChatMessage();
			message.setAction(Action.CONNECT);
			message.setName(userID);
			
        	send(message);
        } catch (IOException ex) {
        	Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return socket;
    }
    
    public void disconnectTo() {
    	ChatMessage msgDisconnect = new ChatMessage();
    	msgDisconnect.setName(userID);
    	msgDisconnect.setAction(Action.DISCONNECT);
    	send(msgDisconnect);
    }
    
    
    public void send(ChatMessage message){
    	try {
    		outputServer.writeObject(message);
    	} catch (IOException ex) {
    		Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
    	}
    }

    public void addFriend(String newFriendID) {
    	ChatMessage msgNewFriend = new ChatMessage();
    	msgNewFriend.setName(userID);
    	msgNewFriend.setAction(Action.NEW_FRIEND);
    	msgNewFriend.setText(newFriendID);
    	send(msgNewFriend);
    }
    
    public void requestFriendList() {
    	ChatMessage msgFriendList = new ChatMessage();
    	msgFriendList.setName(userID);
    	msgFriendList.setAction(Action.FRIEND_LIST);
    	send(msgFriendList);
    }
    
    public void showFriendList() {
    	if (clientContacts.getListUsers().isEmpty()) {
    		System.out.println("FriendList vazia");
    	} else {
    		System.out.println("FriendList:");
    		Iterator<User> itFriendList = clientContacts.getListUsers().listIterator();
    		while(itFriendList.hasNext()) {
    			System.out.println(itFriendList.next().getUserID());
    		}
    	}

    }
    
}
