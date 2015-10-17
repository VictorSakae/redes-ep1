package App;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage implements Serializable{

	private String name;
	private Action action; 
	private String text;
	private List<User> friendList = new ArrayList<User>();

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
    
	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}

	public List<User> getFriendList() {
		return friendList;
	}
	
	public void setFriendList(List<User> friendList) {
		this.friendList = friendList;
	}

}
