package preprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

	// constants used for addressing variables in array of features
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
		//I use this line to test in my computer through CLI hdeb
		//dataParser myDataParser = new dataParser("/home/hector/git/predator_project/predator_project/data/pan12-training.xml");

		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		//TODO create (different) subsets from conversations
		//TODO extract and add features 

		//TEST create subset with messages concatenated per author per conversation
		//List<Message> mySubSet = myDataParser.createSubsetByDistinctAuthorsAndConverversations();
		//System.out.println(mySubSet.size());

		//TEST - create subset containing all message lines
		List<Message> mySubSetL15 = myDataParser.generateL15();
		List<Message> mySubSetW15 = myDataParser.generateW15();
		System.out.println(mySubSetL15.size());
		generateCsvFile(mySubSetL15, "data/L15.csv");

		//TEST CSV export
		System.out.println(mySubSetW15.size());
		generateCsvFile(mySubSetW15, "data/W15.csv");
		//I use this line to test in my computer through CLI hdeb
		//generateCsvFile(mySubSet, "/home/hector/Dropbox/ITU/DataMining/exercises/Group_project/test_W15.csv");


	}

	private List<Message> createSubsetByDistinctAuthorsAndConverversations(){

		// create subset for conversations
		List<Message> subSet = new ArrayList<Message>();

		// for each conversation
		for(Conversation c: this.conversations) {

			// Hashmap holds all authors and their concateneated message
			HashMap<String,String> distinctAuthors = new HashMap<>();

			for(ConversationMessage cm: c.messages) {

				String authorKey = cm.getAuthor();
				// add author to hashmap if not in it
				if(distinctAuthors.containsKey(authorKey)) {

					//concatenate text
					String authorText = distinctAuthors.get(authorKey) + " " + cm.getText();
					distinctAuthors.put(authorKey, authorText);

				} else //add new author to hashmap

					distinctAuthors.put(cm.getAuthor(), cm.getText());

			}

			// add each distinct authors messages to subset
			for(String sender: distinctAuthors.keySet()) {
				
				subSet.add(new Message(sender, distinctAuthors.get(sender)));
				
			}
			
		}
		
		//return list of messages
		return subSet;

	}

	/*
	 * Generates subset from list of conversations
	 * THIS IS JUST A TEST EXAMPLE!!
	 * it just picks all messages and add them to a subset
	 * :-)
	 */
	private List<Message> createSubSetExample() {

		// create subset from conversations
		List<Message> subSet = new ArrayList<Message>();

		// For each Conversation - add each message line to subset
		// TODO change this into concatenating lines according to different subsets
		for(Conversation c: this.conversations) {

			for(ConversationMessage cm: c.messages) {

				String messageText = cm.getText();
				Message newMessage = new Message(cm.getAuthor(),cm.getText());

				// add feature values to message
				newMessage.features[letterLines] = FeatureExtractor.letterLines(messageText);
				newMessage.features[wordLines] = FeatureExtractor.wordLines(messageText);
				newMessage.features[numberOfLines] = FeatureExtractor.numberOfLines(messageText);
				newMessage.features[spaces] = FeatureExtractor.spaces(messageText);
				newMessage.features[funkyWords] = FeatureExtractor.funkyWords(messageText);
				newMessage.features[posEmoticons] = FeatureExtractor.posEmoticons(messageText);
				newMessage.features[neuEmoticons] = FeatureExtractor.neuEmoticons(messageText);
				newMessage.features[consecutiveLetters] = FeatureExtractor.consecutiveLetters(messageText);
				newMessage.features[alert] = FeatureExtractor.alert(messageText);
				newMessage.features[blacklist] = FeatureExtractor.blackList(messageText);
				newMessage.features[misspelledWords] = FeatureExtractor.misspelledWords(messageText);
				newMessage.features[negativeSent] = FeatureExtractor.negativeSent(messageText);
				newMessage.features[positiveSent] = FeatureExtractor.PositiveSent(messageText);

				// add message to subset
				subSet.add(newMessage);
			}

		}

		return subSet;
	}


	/*
	 *Create set L15
	 */
	private List<Message> generateL15() {
		//The list new_list contains now coversation where only one author is present.
		List<Conversation> newList = splitConversatitionsByAuthor(conversations);

		//Iterate through the messages to get:
		//1. The lenght of each conversation
		//Discard all messages thart havent been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> finalList = new ArrayList<Conversation>();
		for(Conversation c: newList) {
			//instantiate to something big.
			int firstMessageTime = 90000;
			int lastMessageTime = 0;
			for(ConversationMessage cm: c.messages) {
				//The timestamps will be parsed to minutes for an easier calculation of the duartion.
				String c_time = cm.getTime();
				int time = timeToInt(c_time);
				if(time < firstMessageTime){
					firstMessageTime = time;
				}
				if(time > lastMessageTime){
					lastMessageTime = time;
				}
			}
			int duration = getDuration(firstMessageTime, lastMessageTime);
			if(duration <= 15){
				finalList.add(c);
			}else{
				System.out.println(c.getId() + " duration: " + duration + "minutes \nfirst: " + firstMessageTime + " last: " + lastMessageTime + "\n");
				//If the conversation lasted more than 15 min discard all messages that occurred 
				//outside the time lapse requied i.e. L15
				Conversation tmpConversation = new Conversation(c.getId(), c.getAuthor());
				for(ConversationMessage cm: c.messages) {
					if(getOffset(timeToInt(cm.getTime()), lastMessageTime) <= 15){
						tmpConversation.addC_Message(cm);
					}
				}
				finalList.add(tmpConversation);
			}

					
		}
		// create subset from conversations
		return generateSubSet(finalList);
	}	
	private List<Message> generateW15() {
		//The list new_list contains now coversation where only one author is present.
		List<Conversation> newList = splitConversatitionsByAuthor(conversations);

		//Iterate through the messages to get:
		//1. The length of each conversation
		//Discard all messages that haven't been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> finalList = new ArrayList<Conversation>();
		for(Conversation c: newList) {
			//instantiate to something big.
			int firstMessageTime = 90000;
			int lastMessageTime = 0;
			for(ConversationMessage cm: c.messages) {
				//The timestamps will be parsed to minutes for an easier calculation of the duartion.
				String c_time = cm.getTime();
				int time = timeToInt(c_time);
				if(time < firstMessageTime){
					firstMessageTime = time;
				}
				if(time > lastMessageTime){
					lastMessageTime = time;
				}
			}
			int duration = getDuration(firstMessageTime, lastMessageTime);
			if(duration <= 15){
				finalList.add(c);
			}else{
				//If the conversation lasted more than 15 min split it in segments of 15 mins.
				Conversation tmpConversation = new Conversation(c.getId(), c.getAuthor());
				int limit = 15;
				int number_of_segments = duration/limit;
				System.out.println("Duration: " + duration + "minutes \nSpiting conversation in " + number_of_segments + "segments...\n");
				for(int i=0; i < number_of_segments; i++){
					for(ConversationMessage cm: c.messages) {
						if(timeToInt(cm.getTime()) < (firstMessageTime + (limit - 15))){
							continue;
						}
						if(isWithinLimit(timeToInt(cm.getTime()), firstMessageTime, limit)){
							tmpConversation.addC_Message(cm);
						}else{
							finalList.add(tmpConversation);
							limit += 15;
						}
					}
					finalList.add(tmpConversation);
					limit += limit;
				}
			}

					
		}
		// create subset from conversations
		return generateSubSet(finalList);
	}
	/*
	 * Generate features
	 */
	private static List<Message> generateSubSet(List<Conversation> newList){
		// create subset from conversations
		List<Message> subSet = new ArrayList<Message>();
		for(Conversation c: newList) {
			String messageText  = "\"";
			String author = c.getAuthor();
			int number_of_messages = c.messages.size();
			int counter = 0;
			for(ConversationMessage cm: c.messages) {
				counter++;
				if(counter == number_of_messages){
					messageText += cm.getText();
				}else{
					messageText += cm.getText() + "\r";
				}
			}
			messageText += "\"";
			Message newMessage = new Message(author, messageText);

				// add feature values to message
				newMessage.features[letterLines] = FeatureExtractor.letterLines(messageText);
				newMessage.features[wordLines] = FeatureExtractor.wordLines(messageText);
				newMessage.features[numberOfLines] = FeatureExtractor.numberOfLines(messageText);
				newMessage.features[spaces] = FeatureExtractor.spaces(messageText);
				newMessage.features[funkyWords] = FeatureExtractor.funkyWords(messageText);
				newMessage.features[posEmoticons] = FeatureExtractor.posEmoticons(messageText);
				newMessage.features[neuEmoticons] = FeatureExtractor.neuEmoticons(messageText);
				newMessage.features[consecutiveLetters] = FeatureExtractor.consecutiveLetters(messageText);
				newMessage.features[alert] = FeatureExtractor.alert(messageText);
				newMessage.features[blacklist] = FeatureExtractor.blackList(messageText);
				newMessage.features[misspelledWords] = FeatureExtractor.misspelledWords(messageText);
				newMessage.features[negativeSent] = FeatureExtractor.negativeSent(messageText);
				newMessage.features[positiveSent] = FeatureExtractor.PositiveSent(messageText);

				// add message to subset
				subSet.add(newMessage);
		}
		return subSet;
	}
	
	/*
	 * Generates CSV file from a subset and save it to file
	 * 
	 */
	private static void generateCsvFile(List<Message> subset, String sFileName)
	{
		try
		{
			FileWriter writer = new FileWriter(sFileName);

			// Create headings
			writer.append("senderID,letterLines,wordLines,numberOfLines,spaces,funkyWords,");
			writer.append("posEmoticons,neuEmoticons,negEmoticons,consecutiveLetters,alert,blacklist,");
			writer.append("misspelledWords,negativeSent,positiveSent,message");
			writer.append('\n');

			// add lines

			for(Message message: subset) {

				writer.append(message.senderID);
				writer.append(',');

				// add features to line
				for (int i = 0; i < message.features.length; i++) {
					writer.append(Integer.toString(message.features[i]));
					writer.append(',');	
				}

				// add message to line
				String csvMessage = message.message;
				csvMessage = csvMessage.replace("", "");
				csvMessage = csvMessage.replace("\n", " ");
				
				writer.append(csvMessage);
				writer.append('\n');

			}

			writer.flush();
			writer.close();
		}

		catch(IOException e)

		{
			e.printStackTrace();
		}

		System.out.println("Exported csv file: " + sFileName);

	}

	/*
	 *Convert the time of the conversations to minutes
	 */
	private static int timeToInt(String time){
		String[] values = time.split(":");
		return ( Integer.parseInt(values[0]) * 60) + Integer.parseInt(values[1]);
	}

	/*
	 * Get the duration of a conversation
	*/
	private static int getDuration(int start, int end){
		if((end-start) < 0){
			//The conversation must have gone from one day to the next, i.e. they reached 00:00 while chatting
			//a day has 1440 minutes
			return end +  (1440-start);
		}else{
			return end-start;
		}
	}
	/*
	 * This is the same method as getDuration with another name for better code readability.
	*/
	private static int getOffset(int toCheck, int timeOfLast){
		if((timeOfLast-toCheck) < 0){
			//The conversation must have gone from one day to the next, i.e. they reached 00:00 while chating
			//a day has 1440 minutes
			return timeOfLast +  (1440-toCheck);
		}else{
			return timeOfLast-toCheck;
		}
	}
/*
	 * This is the same method as getDuration with another name for better code readability.
	*/
	private static boolean isWithinLimit(int toCheck, int timeOfFirst, int Limit){
		if(timeOfFirst + Limit > 1440){
			//The conversation must have gone from one day to the next, i.e. they reached 00:00 while chating
			//a day has 1440 minutes
			int offset = 1440 - timeOfFirst;
			Limit = Limit - offset;
			return toCheck > (timeOfFirst +  Limit);
		}else{
			return toCheck > (timeOfFirst +  Limit);
		}
	}
	/*
	 * 
	 */
	private static List<Conversation> splitConversatitionsByAuthor(List<Conversation> conversations){
		// Split conversation so they only contain one author each
		List <Conversation> new_list = new ArrayList<Conversation>();
		for(Conversation c: conversations) {
			//Create a list of lists of conversations one per author
			ArrayList<Conversation> byAuthor = new ArrayList<Conversation>();
			for(ConversationMessage cm: c.messages) {
				//Add the author to the list if it is not already there
				String c_author = cm.getAuthor();
				boolean exists_author = false;
				Conversation tmp_c;
				for(Conversation tmp : byAuthor){
					if(tmp.getAuthor().equals(c_author)){
						exists_author = true;
						tmp.addC_Message(cm);
						break;
					}

				}
				if(!exists_author){
					tmp_c = new Conversation();
					tmp_c.setAuthor(c_author);
					tmp_c.setId(c.getId());
					tmp_c.addC_Message(cm);
					byAuthor.add(tmp_c);
				}
			}
			for(Conversation tmp_c : byAuthor){
				new_list.add(tmp_c);	
			}
		}
		return new_list;
	}

}
