package xmlImport;
import java.util.ArrayList;
import java.util.List;



public class Conversation {
	private String id; 
	private String author; 
	public List<ConversationMessage> messages = new ArrayList<ConversationMessage>();
	
	
	public Conversation() {
		super();
	}

	public Conversation(String id, String author) {
		this.id = id;
		this.author = author;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	public void addC_Message(ConversationMessage new_m){
		messages.add(new_m);
	}
	
	@Override
	public String toString() {
		return "Conversation [Id =" + id + "] " + messages.size();
	}
	
}
