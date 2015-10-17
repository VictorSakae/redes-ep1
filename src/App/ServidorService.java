package App;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import App.User.Status;

public class ServidorService {

	private ServerSocket serverSocket;
	private Socket socket;
	private Contacts contacts = new Contacts();
    private Map<String, List<String>> friendsLists = new HashMap();


	public ServidorService() {
		try {
			serverSocket = new ServerSocket(4545);
			System.out.println("Servidor ON"); 
			while (true) { 
				socket = serverSocket.accept();

				new Thread(new ListenerSocket(socket)).start();
			}
		} catch (IOException ex) {
			Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private class ListenerSocket implements Runnable {

		private ObjectOutputStream output;
		private ObjectInputStream input;
		private InetAddress IPAddress;
		private int port;

		public ListenerSocket(Socket socket) {
			try {
				this.output = new ObjectOutputStream(socket.getOutputStream());
				this.input = new ObjectInputStream(socket.getInputStream());
				this.IPAddress = socket.getInetAddress();
				this.port = socket.getPort();
			} catch (IOException ex) {
				Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		public void run() {

			ChatMessage message = null;

			try {
				while ((message = (ChatMessage) input.readObject()) != null) {
					Action action = message.getAction();
					if (action.equals(Action.CONNECT)) {
						connect(message, output);
					} else if (action.equals(Action.NEW_FRIEND)) {
						if(addNewFriend(message)){
							System.out.println(message.getText()+" foi adicionado");
						} else {
							System.out.println("Não foi possível adicionar");
						}
					} else if (action.equals(Action.FRIEND_LIST)) {
						sendFriendList(message);
					} else if (action.equals(Action.DISCONNECT)) {
						disconnect(message);
						return;
					}
				}
			} catch (IOException ex) {
				disconnect(message);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private void connect(ChatMessage message, ObjectOutputStream output) {
			String userID = message.getName();
			
			contacts.addContact(userID);
			contacts.addOnline(userID, IPAddress, port, output);
			System.out.println(userID+" se conectou");
		}

		private void disconnect(ChatMessage message) {
			String userID = message.getName();
			
			contacts.getContact(userID).setStatus(Status.OFFLINE);
			contacts.removeOnline(userID);
			
			ChatMessage msgDisconnect = new ChatMessage();
			msgDisconnect.setAction(Action.DISCONNECT);
			send(msgDisconnect);
			System.out.println(userID + " desconectou-se"); //para debug
		}

		private boolean addFriend(String userID, String friendID) {
			List<String> userFriendList = new ArrayList<String>();
			System.out.println("Friend: "+friendID);
			if((contacts.getContact(friendID)) != null) {
				if(friendsLists.containsKey(userID)) {
					userFriendList = friendsLists.get(userID);
					if(userFriendList.contains(friendID)) {
						return false;
					}
					userFriendList.add(friendID);
					return true;
				} else {
					userFriendList = new ArrayList<String>();
					userFriendList.add(friendID);
					friendsLists.put(userID, userFriendList);
					System.out.println("put "+userID);
					return true;	
				}
			}
			return false;
		}
		
		private boolean addNewFriend(ChatMessage message) {
			String userID = message.getName();
			String friendID = message.getText();
			if((addFriend(userID, friendID)) && (addFriend(friendID, userID))) {
				return true;
			}
			return false;
		}
		
	    public void send(ChatMessage message){
	    	try {
	    		output.writeObject(message);
	    		System.out.println("Server enviou");
	    	} catch (IOException ex) {
	    		Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
	    	}
	    }
		
		private void sendFriendList(ChatMessage message) {
			List<User> friendList = new ArrayList<User>();
			Iterator<String> itFriendList = friendsLists.get(message.getName()).listIterator();
			while(itFriendList.hasNext()) {
				friendList.add(contacts.getContact(itFriendList.next()));
			}
			ChatMessage msgFriendList = new ChatMessage();
			msgFriendList.setFriendList(friendList);
			send(msgFriendList);
		}

	}

}
