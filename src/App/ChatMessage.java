package App;



import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*O chat ao invés de mandar String, irá manda objetos ChatMessage*/
public class ChatMessage implements Serializable{
    
    private String name;
    private String text; //mensagem
    private String nameReserved; //armazena nome do cliente do tipo msg reservada
//    private Set<String> setOnlines = new HashSet<String>(); //armazena os contatos
    private User user;
    private String newUserOnline;
    private String newUserOffline;
    private List<User> friendList;
    private String newFriendID;

	
	//para cada msg que o cliente enviar ao servidor ou quando o servidor tiver
    //que responder, vai dizer qual ação quer executar 
    private Action action; 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameReserved() {
        return nameReserved;
    }

    public void setNameReserved(String nameReserved) {
        this.nameReserved = nameReserved;
    }
/*
    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }
*/
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public void setNewUserOnline(String newUserOnline) {
		this.newUserOnline = newUserOnline;
	}
	
	public void setNewUserOffline(String newUserOffline) {
		this.newUserOffline = newUserOffline;
	}
	
	public List<User> getFriendList() {
		return friendList;
	}
	public void setFriendList(List<User> friendList) {
		this.friendList = friendList;
	}
	
	/**
	 * @return the newFriendID
	 */
	public String getNewFriendID() {
		return newFriendID;
	}

	/**
	 * @param newFriendID the newFriendID to set
	 */
	public void setNewFriendID(String newFriendID) {
		this.newFriendID = newFriendID;
	}
}
