package preprocessing;
import java.util.ArrayList;
import java.util.List;



public class Conversation {
	private String id; 
	public List<ConversationMessage> messages = new ArrayList<ConversationMessage>();
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Conversation [Id =" + id + "] " + messages.size();
	}
	
}
