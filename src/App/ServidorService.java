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

    private List<String> getFriendList(String userID) {
    	return friendsLists.get(userID);
    }
    
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
                   		ChatMessage newMessage = new ChatMessage();
                   		newMessage.setAction(Action.NEW_USER_ONLINE);
                   		message.setNewUserOnline(message.getName());
                   		sendAllOnlines(message.getName(), newMessage);
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(message, output);
                        ChatMessage newMessage = new ChatMessage();
                   		newMessage.setAction(Action.NEW_USER_OFFLINE);
                   		message.setNewUserOffline(message.getName());
                   		sendAllOnlines(message.getName(), newMessage);
                        return; //for√ßa a sa√≠da do while para n√£o cair no catch

                    } else if (action.equals(Action.SEND_ONE)) {
                        sendOne(message);
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAllOnlines(message.getName(), message);
                    }
                }
            } catch (IOException ex) {
                disconnect(message, output); //tmb desconecta quando aperta no bot√£o 'X' da janela
                ChatMessage newMessage = new ChatMessage();
           		newMessage.setAction(Action.NEW_USER_OFFLINE);
           		message.setNewUserOffline(message.getName());
           		sendAllOnlines(message.getName(), newMessage);
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
        	contacts.getContact(message.getName()).setStatus(Status.ONLINE);
        	contacts.getContact(message.getName()).setUserIP(IPAddress);
        	contacts.getContact(message.getName()).setPort(port);
        	contacts.addOnline(message.getName(), output);
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
         
        
        private void sendFriendList(String userID) {
        	ChatMessage message = new ChatMessage();
        	//message.setFriendList(contacts.getContact(userID).getFriendList());
        	List<User> friendList = new ArrayList<User>();
        	ListIterator<String> itFriendList = getFriendList(userID).listIterator();
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
/*        private void sendOnlines() {
            Set<String> setNames = new HashSet<String>();
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                setNames.add(kv.getKey());
            }

            ChatMessage message = new ChatMessage();
            message.setAction(Action.USERS_ONLINE);
            message.setSetOnlines(setNames);

            //envia para todos os usu√°rios a lista
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                message.setName(kv.getKey());
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }*/
        
        /**
         * envia mensagem para todos os amigos online
         * @param userID
         * @param message
         */
        private void sendAllOnlines(String userID, ChatMessage message) {
        	   	
            //Set<String> friendsOnline = contacts.getContact(userID).getFriendsOnline();
        	Set<String> friendsOnline = new HashSet<String>();
        	ListIterator<String> itFriendList = getFriendList(userID).listIterator();
        	while(itFriendList.hasNext()) {
        		User userFriendOnline = contacts.getContact(itFriendList.next());
        		if(userFriendOnline.getStatus().equals(Status.ONLINE)) {
        			friendsOnline.add(userFriendOnline.getUserID());
        		}
        	}
            
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
    }
}
