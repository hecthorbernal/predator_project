package preprocessing;

import java.util.ArrayList;
import java.util.List;

import xmlImport.Conversation;
import xmlImport.ConversationMessage;
import xmlImport.StaXParser;


/**
 * Imports XML file and converts it to objects
 * pre-processes data and extract additional features
 * Exports file in csv-format for use in RapidMiner
 *
 */

public class dataParser {

	// list for conversations imported from XML
	private List<Conversation> conversations;
	
	// constants used for array of features
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
	
	
	public dataParser(String file) {
		
		this.conversations = new StaXParser().readConfig(file);
		
	}

	public static void main(String[] args) {

		// create dataParser from xml-file 
		dataParser myDataParser = new dataParser("data/pan12-training.xml");
		
		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		// TEST - try to create subset containing all message lines
		List<Message> mySubSet = myDataParser.createSubSetExample();
		System.out.println(mySubSet.size());
		
		
		//TODO create (different) subsets from conversations

		//TODO extract and add features 

		//TODO Convert to csv format and export

	}

	/*
	 * Generates subset from list of conversations
	 * THIS IS JUST A TEST EXAMPLE!!
	 * it just picks all messages and add them to a subset
	 * :-)
	 */
	private List<Message> createSubSetExample() {

		// TODO create subset from conversations
		List<Message> subSet = new ArrayList<Message>();
		
		// For each Conversation - sort messages by
		for(Conversation c: this.conversations) {
			
			for(ConversationMessage cm: c.messages) {
				Message newMessage = new Message(cm.getAuthor(),cm.getLine());
				subSet.add(newMessage);
			}
			
		}
		
		return subSet;
	}

	

}
