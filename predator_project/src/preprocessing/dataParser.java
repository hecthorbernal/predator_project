package preprocessing;

import java.util.ArrayList;
import java.util.List;


/**
 * Imports XML file and converts it to objects
 * pre-processes data and extract additional features
 * Exports file in csv-format for use in RapidMiner
 *
 */

public class dataParser {

	
	private List<Conversation> conversations; // list of conversations imported from XML
	
	public dataParser(String file) {
		
		this.conversations = new StaXParser().readConfig(file);
		
	}

	public static void main(String[] args) {

		// create dataParser from xml-file 
		dataParser myDataParser = new dataParser("data/pan12-training.xml");

		System.out.println(myDataParser.conversations.size() + " Conversations imported from XML");

		
		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		//TODO create (different) subsets from conversations

		//TODO extract and add features 

		//TODO Convert to csv format and export

	}

	/*
	 * Generates subset from list of conversations
	 */
	private static List<String> createSubSet(List<Conversation> conversations) {

		// TODO create subset from conversations
		return null;
	}

	

}
