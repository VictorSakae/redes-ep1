package App;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import GUI.ClienteGUI;


public class ClienteService {
    
    private Socket socket;
//    private ObjectOutputStream output;
    private ServerSocket welcomeSocket;
    
    private Contacts clientContacts = new Contacts();
    
//     public Socket connect(){
    public Socket connectTo(InetAddress ip, int port){
        try {
//            this.socketServer = new Socket("localhost", 5555);
        	this.socket = new Socket(ip, port);
//            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return socket;
    }
    
    public ClienteService() {
    	try {
    		welcomeSocket = new ServerSocket(6666);
    		System.out.println("Cliente ON");
    		while(true) {
    			socket = welcomeSocket.accept();
    			
    			new Thread(new ListenerSocket(socket)).start();
    		}
    	} catch (IOException ex) {
    		Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
    	}
    	
    }
    
    private class ListenerSocket implements Runnable {
    	private ObjectOutputStream output;
    	private ObjectInputStream input;
    	private InetAddress ipAddress;
    	private int port;
    	
    	public ListenerSocket(Socket socket) {
    		try {
    			this.output = new ObjectOutputStream(socket.getOutputStream());
    			this.input = new ObjectInputStream(socket.getInputStream());
    			this.ipAddress = socket.getLocalAddress();
    			this.port = socket.getPort();
    		} catch (IOException ex) {
    			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
    		}
    	}
    	
    	public void run() {
    		ChatMessage message = null;
    		
    		try {
    			while((message = (ChatMessage) input.readObject()) != null) {
    				Action action = message.getAction();
    				
    				if(action.equals(Action.CONNECT)) {
  					
    				} else if(action.equals(Action.DISCONNECT)) {
    					
    				} else if(action.equals(Action.ALTER_STATUS)) {
    					
    				} else if(action.equals(Action.SEND_ONE)) {
    					
    				} else if(action.equals(Action.SEND_ALL)) {
    					
    				} else if(action.equals(Action.NEW_FRIEND)) {
    					
    				} else if(action.equals(Action.FRIEND_LIST)) {
    					
    				}
    			}
    		} catch (IOException ex) {
                // disconnect
    			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
    		} catch (ClassNotFoundException ex) {
    			Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
    		}
    	}
    }
    
    public void send(ChatMessage message, ObjectOutputStream output){
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
