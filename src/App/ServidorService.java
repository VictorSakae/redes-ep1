package App;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;

    //todo usuario que se conectar ao servidor, é add nessa lista
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555); //inicializa o objeto na porta 5555
            System.out.println("Servidor ON"); //para debug
            while (true) { //sempre executa à espera dos clientes
                //pega o cliente e o libera pegando o socket do cliente para a variável socket
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

        //inicializa as variáveis através do socket
        public ListenerSocket(Socket socket) {
            try {
                //exclusivo para cada cliente
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        public void run() {

            ChatMessage message = null;

            try {
                //message recebe as mensagens enviadas pelo cliente a partir do objeto input
                //necessário convertê-lo ao tipo ChatMessage pois retorna um tipo object
                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) {
                        boolean isConnect = connect(message, output);
                        if (isConnect) {
                            mapOnlines.put(message.getName(), output);
                            sendOnlines();
                        }
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(message, output);
                        sendOnlines();
                        return; //força a saída do while para não cair no catch

                    } else if (action.equals(Action.SEND_ONE)) {
                        sendOne(message);
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(message);
                    }
                }
            } catch (IOException ex) {
                disconnect(message, output); //tmb desconecta quando aperta no botão 'X' da janela
                sendOnlines();
                //System.out.println(message.getName() + " saiu"); //para debug
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private boolean connect(ChatMessage message, ObjectOutputStream output) {

            /*INICIO: evita usuários com o mesmo nome ou username*/
            if (mapOnlines.size() == 0) { //nenhum cliente se conectou ainda
                message.setText("YES");
                send(message, output);
                return true;
            }

            //a lista já possui pelo menos um cliente conectado
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                if (kv.getKey().equals(message.getName())) { //a chave do objeto é o nome do cliente
                    message.setText("NO");
                    send(message, output);
                    return false;
                } else {
                    message.setText("YES");
                    send(message, output);
                    return true;
                }
            }
            /*FIM: evita usuários com o mesmo nome ou username*/

            return false;
        }

        private void disconnect(ChatMessage message, ObjectOutputStream output) {
            mapOnlines.remove(message.getName());

            message.setText("deixou o chat");

            //envia mensagem informando que o usuário desconectou-se
            message.setAction(Action.SEND_ONE);
            sendAll(message);

            System.out.println(message.getName() + " desconectou-se"); //para debug
        }

        //mnda msg pra todo mundo da lista
        private void sendAll(ChatMessage message) {
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                //evita que uma msg seja enviada para o próprio cliente que está enviando a msg
                if (!kv.getKey().equals(message.getName())) {
                    message.setAction(Action.SEND_ONE);
                    try {
                        kv.getValue().writeObject(message);
                    } catch (IOException ex) {
                        Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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

        //msg reservada
        private void sendOne(ChatMessage message) {
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                if (kv.getKey().equals(message.getNameReserved())) {
                    try {
                        //envia a menssagem
                        kv.getValue().writeObject(message);
                    } catch (IOException ex) {
                        Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        private void sendOnlines() {
            Set<String> setNames = new HashSet<String>();
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                setNames.add(kv.getKey());
            }

            ChatMessage message = new ChatMessage();
            message.setAction(Action.USERS_ONLINE);
            message.setSetOnlines(setNames);

            //envia para todos os usuários a lista
            for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
                message.setName(kv.getKey());
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }
}
