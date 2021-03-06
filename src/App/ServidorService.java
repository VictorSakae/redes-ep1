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
    private Map<String, List<String>> friendsLists = new HashMap<String, List<String>>();

	public ServidorService() {
		
		String cliente1 = "Cliente1";
		String cliente2 = "Cliente2";
		
		contacts.addContact(cliente1);
		contacts.addContact(cliente2);
		
		List<String> listUser1 = new ArrayList<String>();
		listUser1.add(cliente2);
		friendsLists.put(cliente1, listUser1);
		
		List<String> listUser2 = new ArrayList<String>();
		listUser2.add(cliente1);
		friendsLists.put(cliente2, listUser2);		
		
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

		public ListenerSocket(Socket socket) {
			try {
				this.output = new ObjectOutputStream(socket.getOutputStream());
				this.input = new ObjectInputStream(socket.getInputStream());
				this.IPAddress = socket.getInetAddress();
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
					} else if (action.equals(Action.FRIEND_LIST)) {
						sendFriendList(message);
					} else if (action.equals(Action.DISCONNECT)) {
						disconnect(message);
						return;
					}
				}
			} catch (IOException ex) {
				disconnect(message);
				System.out.println(message.getName()+" saiu.");
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private void connect(ChatMessage message, ObjectOutputStream output) {
			String userID = message.getName();
			int port = Integer.parseInt(message.getText());
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
			msgDisconnect.setText("OK");
			send(msgDisconnect);
			
//			System.out.println(userID + " desconectou-se"); //para debug
		}

		
	    private void send(ChatMessage message){
	    	try {
	    		output.writeObject(message);
	    	} catch (IOException ex) {
	    		Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
	    	}
	    }
		
		private void sendFriendList(ChatMessage message) {
			List<User> friendList = new ArrayList<User>();
			Iterator<String> itFriendList = friendsLists.get(message.getName()).listIterator();
			while(itFriendList.hasNext()) {
				String friend = itFriendList.next();
				friendList.add(contacts.getContact(friend));
			}
			ChatMessage msgFriendList = new ChatMessage();
			msgFriendList.setName(message.getName());
			msgFriendList.setAction(Action.FRIEND_LIST);
			msgFriendList.setFriendList(friendList);
			send(msgFriendList);
		}

	}

}
