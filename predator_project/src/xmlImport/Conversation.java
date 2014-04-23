package xmlImport;
import java.util.ArrayList;
import java.util.List;



public class Conversation {
	private String id; 
	private String author; 
	private boolean predator;
	public boolean isPredator() {
		return predator;
	}

	public void setPredator(boolean predator) {
		this.predator = predator;
	}

	private int number_of_lines;
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
	
	/**
	 * @return the number_of_lines
	 */
	public int getNumber_of_lines() {
		return number_of_lines;
	}

	/**
	 * @param number_of_lines the number_of_lines to set
	 */
	public void setNumber_of_lines(int number_of_lines) {
		this.number_of_lines = number_of_lines;
	}

	@Override
	public String toString() {
		return "Conversation [Id =" + id + "] " + messages.size();
	}
	
}
