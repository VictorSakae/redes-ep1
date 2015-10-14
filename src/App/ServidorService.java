package App;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import App.User.Status;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    
    private Contacts contacts = new Contacts(); // lista de Usu·rios 
    private Map<String, List<String>> friendsLists = new HashMap();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555); //inicializa o objeto na porta 5555
            System.out.println("Servidor ON"); //para debug
            while (true) { //sempre executa √† espera dos clientes
                //pega o cliente e o libera pegando o socket do cliente para a vari√°vel socket
                socket = serverSocket.accept();

                //a cada cliente que se conectar, cria-se uma nova thread
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

        //inicializa as vari√°veis atrav√©s do socket
        public ListenerSocket(Socket socket) {
            try {
                //exclusivo para cada cliente
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
                //message recebe as mensagens enviadas pelo cliente a partir do objeto input
                //necess√°rio convert√™-lo ao tipo ChatMessage pois retorna um tipo object
                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) {
                    	connect(message, output);
                    	sendAllOnlines(message.getName(), Action.ALTER_STATUS);
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(message, output);
                        sendAllOnlines(message.getName(), Action.ALTER_STATUS);
                        return; //for√ßa a sa√≠da do while para n√£o cair no catch

                    } else if (action.equals(Action.SEND_ONE)) {
                        sendOne(message);
                    } else if (action.equals(Action.NEW_FRIEND)) {
                    	ChatMessage newMessage = new ChatMessage();
                    	if(addFriend(message)){
                    		newMessage.setText("YES");
                    		
                    		/* Se NewFriend est· online, envia msg para ele */
                        	if(contacts.isOnline(message.getName())) {
                        		ChatMessage msgNewFriend = new ChatMessage();
                        		msgNewFriend.setAction(Action.NEW_FRIEND);
                        		msgNewFriend.setName(message.getName());
                        		ObjectOutputStream outputNewFriend = contacts.getMapOnlines().get(message.getName()); 
                        		send(msgNewFriend, outputNewFriend);
                        	}
                        	
                    	} else {
                    		newMessage.setText("NO");
                    	}
                    	
                    	send(newMessage, output); // resposta para o user 
                    } else if (action.equals(Action.FRIEND_LIST)) {
                    	sendFriendList(message.getName());
                    }
                }
            } catch (IOException ex) {
                disconnect(message, output); //tmb desconecta quando aperta no bot√£o 'X' da janela
                sendAllOnlines(message.getName(), Action.ALTER_STATUS);
                //System.out.println(message.getName() + " saiu"); //para debug
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * Add na lista de usu·rios do Servidor, atualiza infos do usu·rio e envia resposta de confirmaÁ„o p/ o cliente
         * 
         * @param message - RequisiÁ„o do cliente para se conectar
         * @param output - Stream de dados de saÌda 
         */
        private void connect(ChatMessage message, ObjectOutputStream output) {

        	contacts.addContact(message.getName());
        	contacts.addOnline(message.getName(), IPAddress, port, output);
        	message.setText("YES");
            try {
                //envia a menssagem
                output.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            };
        }

        /**
         * Atualiza status do usu·rio e envia resposta de confirmaÁ„o p/ cliente
         * @param message - RequisiÁ„o do cliente para se desconectar
         * @param output - Stream de dados de saÌda
         */
        private void disconnect(ChatMessage message, ObjectOutputStream output) {
            contacts.getContact(message.getName()).setStatus(Status.OFFLINE);
            contacts.removeOnline(message.getName());
            
            message.setText("deixou o chat");
            try {
                output.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            };
            
            System.out.println(message.getName() + " desconectou-se"); //para debug
        }

        //msg reservada
        private void sendOne(ChatMessage message) {
        	try {
        		//envia a menssagem
        		contacts.getMapOnlines().get(message.getNameReserved()).writeObject(message);
        	} catch (IOException ex) {
        		Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        	}
        }
         
        /**
         * Envia a FriendList para o userID
         * @param userID
         */
        private void sendFriendList(String userID) {
        	ChatMessage message = new ChatMessage();
        	//message.setFriendList(contacts.getContact(userID).getFriendList());
        	List<User> friendList = new ArrayList<User>();
        	ListIterator<String> itFriendList = friendsLists.get(userID).listIterator();
        	while(itFriendList.hasNext()) {
        		friendList.add(contacts.getContact(itFriendList.next()));
        	}
        	message.setFriendList(friendList);
        	message.setAction(Action.USERS_ONLINE);
        	try {
        		contacts.getMapOnlines().get(userID).writeObject(message);
        	} catch (IOException ex) {
        		Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        	}
        	
        	
        }
        
        private void send(ChatMessage message, ObjectOutputStream output) {
            try {
                //envia a menssagem
                output.writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Envia mensagem para todos os amigos online
         * @param userID
         * @param action
         */
        private void sendAllOnlines(String userID, Action action) {
        	   	
            //Set<String> friendsOnline = contacts.getContact(userID).getFriendsOnline();
        	Set<String> friendsOnline = new HashSet<String>();
        	ListIterator<String> itFriendList = friendsLists.get(userID).listIterator();
        	while(itFriendList.hasNext()) {
        		User userFriendOnline = contacts.getContact(itFriendList.next());
        		if(userFriendOnline.getStatus().equals(Status.ONLINE)) {
        			friendsOnline.add(userFriendOnline.getUserID());
        		}
        	}
            
        	ChatMessage message = new ChatMessage();
        	message.setAction(action);
        	
            for (Map.Entry<String, ObjectOutputStream> kv : contacts.getMapOnlines().entrySet()) {
            	if(friendsOnline.contains(kv.getKey())) {
            		message.setName(userID);
            		try {
            			kv.getValue().writeObject(message);
            		} catch (IOException ex) {
            			Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            		}
            	}
            }
        }
        
        private boolean addFriend(ChatMessage message) {
        	String friendID = message.getName();
        	User newFriend = contacts.getContact(friendID);
        	if(newFriend != null) {
        		List<String> friendList = friendsLists.get(message.getName());        		
        		if(!friendList.contains(friendID)) {
        			friendList.add(friendID);
        			friendsLists.get(friendID).add(message.getName());
        			return true;
        		}
        	}
        	return false;
        }
    }
}
