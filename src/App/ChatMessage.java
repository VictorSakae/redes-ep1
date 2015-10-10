package App;



import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/*O chat ao invés de mandar String, irá manda objetos ChatMessage*/
public class ChatMessage implements Serializable{
    
    private String name;
    private String text; //mensagem
    private String nameReserved; //armazena nome do cliente do tipo msg reservada
    private Set<String> setOnlines = new HashSet<String>(); //armazena os contatos
    
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

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
        
}
