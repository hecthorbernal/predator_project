/**
 * 
 */
package preprocessing;

/**
 * A message contains senderID, extracted features and a message string
 *
 */
public class Message {
	
	public String senderID;
	public String message;
	public String isPredator;
	public int[] features = new int[14];
	
	final static int letterLines = 0;
	final static int wordLines = 1;
	final static int numberOfLines = 2;
	final static int spaces = 3;
	final static int funkyWords = 4;
	final static int posEmoticons = 5;
	final static int neuEmoticons = 6;
	final static int negEmoticons = 7;
	final static int consecutiveLetters = 8;
	final static int alert = 9;
	final static int blacklist = 10;
	final static int misspelledWords = 11; 
	final static int negativeSent = 12; 
	final static int positiveSent = 13;
	
	public Message() {
		
		this.senderID = "";
		this.message = "";
		initializeFeatures(); 
		
		
	}
	
	public Message(String senderID, String message) {
		
		this.senderID = senderID;
		this.message = message;
		initializeFeatures();
	}
	public void setPredator(String p){
		this.isPredator = p;
		
	}
	// Initializez all features to -1
	private void initializeFeatures(){
		for (int i = 0; i < this.features.length; i++) {
			this.features[i] = -1;
		}
	}
}
