package App;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClienteTeste2 {

	public static void main(String[] args) {

		String userID = "Cliente2";

		ClienteService service = new ClienteService(userID, 6001);
		try{
			InetAddress localhost = InetAddress.getLocalHost();
			service.connectToServer(localhost);
		} catch (UnknownHostException unknownHostException) {
			System.err.println(unknownHostException);
		}

		
	}

}
