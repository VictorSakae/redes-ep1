package App;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClienteTeste {

	public static void main(String[] args) {

		String userID = "Cliente1";
		String newFriendID = "Cliente2";
		ClienteService service = new ClienteService(userID, 6002);
		
		
		try{
			InetAddress localhost = InetAddress.getLocalHost();
			service.connectToServer(localhost);
			service.sendTo(newFriendID, "OLAR ABIGOOO");			
		} catch (UnknownHostException unknownHostException) {
			System.err.println(unknownHostException);
		}

		service.disconnectFromServer();;
	}

}
