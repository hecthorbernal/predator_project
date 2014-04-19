package preprocessing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xmlImport.Conversation;
import xmlImport.ConversationMessage;
import xmlImport.StaXParser;
import featureExtractors.BlackListSpacesDetector;
import featureExtractors.BlackListWordsDetector;
import featureExtractors.JazzySpellChecker;
import featureExtractors.SentimentAnalyser;


/**
 * Imports XML file and converts it to objects
 * pre-processes data and extract additional features
 * Exports file in csv-format for use in RapidMiner
 *
 */

public class dataParser {

	// list for conversations imported from XML
	private List<Conversation> conversations;
	private JazzySpellChecker jazzySpellChecker;

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
		this.jazzySpellChecker = new JazzySpellChecker();
	}

	public static void main(String[] args) {
		
		// Instantiate spellChecker for counting misspelled words

		// create dataParser from xml-file 
		dataParser myDataParser = new dataParser("data/pan12-training.xml");

		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		//TODO create (different) subsets from conversations
		//TODO extract and add features 

		//TEST create subset with messages concatenated per author per conversation
//		List<Message> mySubSet = myDataParser.createSubSetExample();
//		
//		System.out.println(mySubSet.size());

		//TEST - create subset containing all message lines
		List<Message> mySubSetL15 = myDataParser.generateL15();
		List<Message> mySubSetW15 = myDataParser.generateW15();
		System.out.println(mySubSetL15.size());
		generateCsvFile(mySubSetL15, "data/L15.csv");

		//TEST CSV export
		System.out.println(mySubSetW15.size());
		generateCsvFile(mySubSetW15, "data/W15.csv");


	}


	/**
	 * Create set L15
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
			int firstMessageTime = 0;
			int lastMessageTime = 0;
			int firsNotNormalized = 0;
			boolean isFirst = false;	
			for(ConversationMessage cm: c.messages) {
				//The timestamps will be parsed to minutes for an easier calculation of the duartion.
				//The normalized time of the message added to the object cm for much easier processing.
				String c_time = cm.getTime();
				int time = timeToInt(c_time);
				if(!isFirst){
					firsNotNormalized = time;
					int normalized = timeNormalizer(firsNotNormalized, time);
					firstMessageTime = normalized;
					cm.setNormalized_time(normalized);
					isFirst = true;
				}else{
					int normalized = timeNormalizer(firsNotNormalized, time);
					lastMessageTime = normalized;
					cm.setNormalized_time(normalized);
					isFirst = true;
				}
			}
			int duration = getDuration(firstMessageTime, lastMessageTime);
			if(duration <= 15){
				finalList.add(c);
			}else{
				//System.out.println(c.getId() + " duration: " + duration + "minutes \nfirst: " + firstMessageTime + " last: " + lastMessageTime + "\n");
				//If the conversation lasted more than 15 min discard all messages that occurred 
				//outside the time lapse required i.e. L15
				Conversation tmpConversation = new Conversation(c.getId(), c.getAuthor());
				for(ConversationMessage cm: c.messages) {
					if(withinLast15Mins(cm.getNormalized_time(), lastMessageTime)){
						tmpConversation.addC_Message(cm);
					}
				}
				finalList.add(tmpConversation);
			}

					
		}
		// create subset from conversations
		return generateSubSet(finalList);
	}	
	/**
	 * Create set W15
	 * @return
	 */
	private List<Message> generateW15() {
		//The list new_list contains now conversation where only one author is present.
		List<Conversation> newList = splitConversatitionsByAuthor(conversations);

		//Iterate through the messages to get:
		//1. The length of each conversation
		//Discard all messages that haven't been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> finalList = new ArrayList<Conversation>();
		for(Conversation c: newList) {
			//instantiate to something big.
			int firsNotNormalized = 0;
			int firstMessageTime = 0;
			int lastMessageTime = 0;
			boolean isFirst = false;
			for(ConversationMessage cm: c.messages) {
				//The timestamps will be parsed to minutes for an easier calculation of the duartion.
				//The normalized time of the message added to the object cm for much easier processing.
				String c_time = cm.getTime();
				int time = timeToInt(c_time);
				if(!isFirst){
					firsNotNormalized = time;
					int normalized = timeNormalizer(firsNotNormalized, time);
					firstMessageTime = normalized;
					cm.setNormalized_time(normalized);
					isFirst = true;
				}else{
					int normalized = timeNormalizer(firsNotNormalized, time);
					lastMessageTime = normalized;
					cm.setNormalized_time(normalized);
					isFirst = true;
				}
			}
			int duration = getDuration(firstMessageTime, lastMessageTime);
			if(duration <= 15){
				finalList.add(c);
			}else{
				//If the conversation lasted more than 15 min split it in segments of 15 mins.
				//Pieces of conversation under 15 min get thrown out. This might requiere changes after we
					isFirst = true;
				//talk to Yun next time.
				int limit = 15;
				int number_of_segments = duration/limit;
				//take the last segment even though it is shorter than 15 min.
				if(duration%limit > 0){
					number_of_segments++;
				}
//				System.out.println("Duration: " + duration + "minutes \nSpiting conversation " + c.getId()+ " author: " + c.getAuthor() + " in " + number_of_segments + "segments...");
				for(int i=0; i < number_of_segments; i++){
					int start = i * limit;
					int end = start + limit;
					Conversation tmpConversation = new Conversation(c.getId(), c.getAuthor());
					boolean added = false;
					for(ConversationMessage cm: c.messages) {
							if(isWithinLimit(cm.getNormalized_time(), start, end)){
								tmpConversation.addC_Message(cm);
								added = true;
							}
					}
					if(added){finalList.add(tmpConversation);}
					limit += 15;
				}
			}
		}
		// create subset from conversations
		return generateSubSet(finalList);
	}
	private int timeNormalizer(int first, int current){
		if(current >= first){
			return current - first;
		}
		int offset = 1440 -first;
		return offset + current;
	}
	
	/**
	 * Generate features
	 */
	private static List<Message> generateSubSet(List<Conversation> newList){
		// create subset from conversations
		List<Message> subSet = new ArrayList<Message>();
		//Instantiate sentiment analyser
		SentimentAnalyser sentiments = new SentimentAnalyser("data/AFINN-111.txt");
		//Instantiate detector of offenses and profanation .
		BlackListWordsDetector profanator = new BlackListWordsDetector("data/OffensiveProfaneWordList.txt");
		//Instantiate the predator identifier
		PredatorIdentifier predatorDetector = new PredatorIdentifier("data/pan2012-list-of-predators-id.txt");
		//Instantiate Spaces detector
		BlackListSpacesDetector spacesDetector = new BlackListSpacesDetector("data/OffensiveProfaneWordList_with_spaces.txt");
		//Instantiate JazzySpellChecker
		JazzySpellChecker spellChecker = new JazzySpellChecker();
		for(Conversation c: newList) {
			String messageText  = "\"";
			int num_of_lines = 0;
			String author = c.getAuthor();
			for(ConversationMessage cm: c.messages) {
					messageText += cm.getText() + " ";
					num_of_lines++;
			}
			messageText += "\"";
			Message newMessage = new Message(author, messageText);
			newMessage.setPredator(predatorDetector.isAPredator(author));

				// add feature values to message
				newMessage.features[letterLines] = FeatureExtractor.letterLines(messageText);
				newMessage.features[wordLines] = FeatureExtractor.wordLines(messageText);
				//newMessage.features[numberOfLines] = FeatureExtractor.numberOfLines(messageText);
				newMessage.features[numberOfLines] = num_of_lines;
				newMessage.features[spaces] = spacesDetector.numberOfOffensiveProfanes(messageText);
				newMessage.features[funkyWords] = FeatureExtractor.funkyWords(messageText);
				newMessage.features[posEmoticons] = FeatureExtractor.posEmoticons(messageText);
				newMessage.features[neuEmoticons] = FeatureExtractor.neuEmoticons(messageText);
				newMessage.features[consecutiveLetters] = FeatureExtractor.consecutiveLetters(messageText);
				newMessage.features[alert] = FeatureExtractor.alert(messageText);
				newMessage.features[blacklist] = profanator.numberOfOffensiveProfanes(messageText);
				//newMessage.features[blacklist] = FeatureExtractor.blackList(messageText);
				newMessage.features[misspelledWords] = spellChecker.countMisspelledWords(messageText);
				newMessage.features[negativeSent] = sentiments.getNegativeSentiment(messageText);
				newMessage.features[positiveSent] = sentiments.getPositiveSentiment(messageText);
//				newMessage.features[negativeSent] = FeatureExtractor.negativeSent(messageText);
//				newMessage.features[positiveSent] = FeatureExtractor.PositiveSent(messageText);
				

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
			writer.append("predator,senderID,letterLines,wordLines,numberOfLines,spaces,funkyWords,");
			writer.append("posEmoticons,neuEmoticons,negEmoticons,consecutiveLetters,alert,blacklist,");
			writer.append("misspelledWords,negativeSent,positiveSent,message");
			writer.append('\n');

			// add lines

			for(Message message: subset) {

				writer.append(message.isPredator);
				writer.append(',');
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
			return end-start;
	}
	/*
	 * This is the same method as getDuration with another name for better code readability.
	*/
	private static boolean withinLast15Mins(int toCheck, int timeOfLast){
			return timeOfLast-toCheck <= 15;
	}
/*
	 * This is the same method as getDuration with another name for better code readability.
	*/
	private static boolean isWithinLimit(int toCheck, int start, int end){
			if(toCheck >= start && toCheck < end){
				return true;
			}
			return false;
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
