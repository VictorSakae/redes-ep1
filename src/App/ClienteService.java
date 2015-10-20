package App;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteService {

	private Socket socket;
	private Socket socketServer;
	private ObjectOutputStream outputServer;

	private final int portServer = 4545;
	private String userID;
	private Contacts clientContacts = new Contacts();

	private ObjectOutputStream outputClient;
	private int portClientServer;
	
	boolean friendListFlag = false;
	
	public ClienteService(String userID, int portClient) {
		this.userID = userID;
		this.portClientServer = portClient;
	}

	/* CLIENTE - CLIENTE */
	private class ClientServer implements Runnable {

		private ServerSocket serverSocket;


		public void run() {

			try {
				serverSocket = new ServerSocket(portClientServer);
				System.out.println("ClienteServidor ON"); 
				while (true) { 
					socket = serverSocket.accept();

					new Thread(new ListenerSocket(socket)).start();
				}
			} catch (IOException ex) {
				Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
			}	
		}
	}

	private class ListenerSocket implements Runnable {

		private ObjectOutputStream output;
		private ObjectInputStream input;

		public ListenerSocket(Socket socket) {
			try {
				this.output = new ObjectOutputStream(socket.getOutputStream());
				this.input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException ex) {
				Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

		public void run() {

			ChatMessage message = null;

			try {
				while ((message = (ChatMessage) input.readObject()) != null) {
					Action action = message.getAction();
					if (action.equals(Action.DISCONNECT)) {

					} else if (action.equals(Action.MESSAGE)) {
						System.out.println(message.getText());
					}
				}
			} catch (IOException ex) {
				System.out.println(message.getName()+" saiu.");
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}


	}

	/* CLIENTE - SERVIDOR */
	private class ListenerSocketServer implements Runnable {
		private ObjectInputStream input;

		public ListenerSocketServer() {
			try {
				input = new ObjectInputStream(socketServer.getInputStream());
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
//						showFriendList();
					} else if (action.equals(Action.DISCONNECT)) {
						socketServer.close();
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
			clientContacts.setListUsers(message.getFriendList());
			friendListFlag = true;
		}

		private void showFriendList() {
			if (clientContacts.getListUsers().isEmpty()) {
				System.out.println("Nenhum contato na lista");
			} else {
				System.out.println("FriendList:");
				Iterator<User> itFriendList = clientContacts.getListUsers().listIterator();
				while(itFriendList.hasNext()) {
					System.out.println(itFriendList.next().getUserID());
				}
			}
		}
	}

	/* PRIVATE METHODS */
	private void sendToServer(ChatMessage message){
		try {
			outputServer.writeObject(message);
		} catch (IOException ex) {
			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private Socket connectTo(InetAddress ip, int port){
		Socket newSocket = null;
		try {
			newSocket = new Socket(ip, port);
			System.out.println("Connected to "+ip.toString()+":"+String.valueOf(port));

		} catch (IOException ex) {
			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return newSocket;
	}

	private void send(ChatMessage message, ObjectOutputStream output) {
		try {
			//envia a menssagem
			output.writeObject(message);
		} catch (IOException ex) {
			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/* PUBLIC METHODS */

	public void connectToServer(InetAddress ipServer){
		socketServer = connectTo(ipServer, portServer);

		try {
			outputServer = new ObjectOutputStream(socketServer.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
		}

		ChatMessage message = new ChatMessage();
		message.setAction(Action.CONNECT);
		message.setName(userID);
		message.setText(String.valueOf(portClientServer));

		sendToServer(message);

		new Thread(new ListenerSocketServer()).start();
		new Thread(new ClientServer()).start();	

	}
	
	public void disconnectFromServer() {
		ChatMessage msgDisconnect = new ChatMessage();
		msgDisconnect.setAction(Action.DISCONNECT);
		msgDisconnect.setName(userID);
		sendToServer(msgDisconnect);
	}

	public void requestFriendList() {
		ChatMessage msgFriendList = new ChatMessage();
		msgFriendList.setAction(Action.FRIEND_LIST);
		msgFriendList.setName(userID);
		sendToServer(msgFriendList);
	}

	public void sendTo(String id, String text) {
		if(!friendListFlag) {
			requestFriendList();
			while(!friendListFlag) {
				System.out.println("Wait...");
			}
		}
		User userFriend = clientContacts.getContact(id);
//		System.out.println("IP : " + userFriend.getUserIP().toString());
//		System.out.println("Porta : " + String.valueOf(userFriend.getPort()));
		
		socket = connectTo(userFriend.getUserIP(), userFriend.getPort());
		try {
			outputClient = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
		}
		ChatMessage message = new ChatMessage();
		message.setAction(Action.MESSAGE);
		message.setText(text);
		message.setName(id);
		send(message, outputClient);
	}

}
