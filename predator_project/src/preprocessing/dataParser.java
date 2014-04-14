package preprocessing;

import java.util.List;


/**
 * Imports XML file and converts it to objects
 * pre-processes data and extract additional features
 * Exports file in csv-format for use in RapidMiner
 *
 */
public class dataParser {


	public static void main(String[] args) {

		// Import xml data and convert it to list of Conversation objects using STAX API
		StaXParser read = new StaXParser();
		List<Conversation> readConversations = read.readConfig("data/pan12-training.xml");

		System.out.println(readConversations.size() + " Conversations imported from XML");
		
		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		//TODO split each conversation into separate message per author
		
		//TODO concatenate authors messages into one string with max. 15 min. timespan
		
		//TODO extract and add features 
		
		//TODO Convert to csv format and export

	}


}
