package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xmlImport.Conversation;
import xmlImport.ConversationMessage;
import xmlImport.StaXParser;
import featureExtractors.EmoticonAnalyzer;
import featureExtractors.LinguisticFeaturesDetector;
import featureExtractors.BlackListWordsDetector;
import featureExtractors.JazzySpellChecker;
import featureExtractors.LinguisticFeaturesDetectorTrieST;
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
		List<Message> mySubSetL15 = myDataParser.generateL15(false);
		List<Message> mySubSetW15 = myDataParser.generateW15(false);
		System.out.println(mySubSetL15.size());
		generateRawCsvFile(mySubSetL15, "data/L15_raw.csv");

		//TEST CSV export
		System.out.println(mySubSetW15.size());
		generateRawCsvFile(mySubSetW15, "data/W15_raw.csv");

		// TEST import from raw CSV
		List<Message> L15 = readRawSubset("data/L15_raw.csv");

		// Add features to L15
		addFeaturesToSubset(L15);

		generateCsvFile(L15, "data/L15.csv");

		System.out.println(L15.size());




	}


	/**
	 * Create set L15
	 * @param print_output TODO
	 */
	private List<Message> generateL15(boolean print_output) {
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
			int line_number = 0;
			if(duration <= 15){
				finalList.add(c);
				line_number++;
			}else{
				//If the conversation lasted more than 15 min discard all messages that occurred 
				//outside the time lapse required i.e. L15
				Conversation tmpConversation = new Conversation(c.getId(), c.getAuthor());
				for(ConversationMessage cm: c.messages) {
					if(withinLast15Mins(cm.getNormalized_time(), lastMessageTime)){
						tmpConversation.addC_Message(cm);
						if(print_output){
							System.out.println("Splitting conversation: " + c.getId() + "\nduration: " + duration + "minutes \nfirst: " +
									firstMessageTime + " last: " + lastMessageTime + "\nAdded to L15_raw.csv line: " + ++line_number);
						}
					}
				}
				finalList.add(tmpConversation);
			}


		}
		// create subset from conversations
		System.out.println("L15 created");
		return generateSubSet(finalList);
	}	
	/**
	 * Create set W15
	 * @param print_output TODO
	 * @return
	 */
	private List<Message> generateW15(boolean print_output) {
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
			int line_number = 0;
			if(duration <= 15){
				finalList.add(c);
				line_number++;
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
				if(print_output){
					System.out.println("Spiting conversation " + c.getId()+ " author: " + c.getAuthor() + " in " + number_of_segments + "segments");
				}
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
					if(added){
						finalList.add(tmpConversation);
						if(print_output){
							System.out.println("\tSegment added to W15_raw.csv line: " + ++line_number);
						}
					}
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
	 * GCreate subset from list of conversations
	 */
	private static List<Message> generateSubSet(List<Conversation> newList){

		List<Message> subSet = new ArrayList<Message>();

		//Instantiate the predator identifier
		PredatorIdentifier predatorDetector = new PredatorIdentifier("data/pan2012-list-of-predators-id.txt");

		for(Conversation c: newList) {
			String messageText  = ""; // "\""; added as last step in feature-extraction
			int num_of_lines = 0;
			String author = c.getAuthor();
			for(ConversationMessage cm: c.messages) {
				messageText += cm.getText() + "<nl>"; // newline marker
				num_of_lines++;
			}
			//messageText += "\"";
			Message newMessage = new Message(author, messageText);
			newMessage.setPredator(predatorDetector.isAPredator(author));

			// add message to subset
			subSet.add(newMessage);

			//System.out.println(newMessage);
		}
		return subSet;
	}

	/**
	 * Adds features to a given subset
	 */
	private static void addFeaturesToSubset(List<Message> subset){


		//Instantiate sentiment analyser
		SentimentAnalyser sentiments = new SentimentAnalyser("data/AFINN-111.txt");

		//Instantiate detector of offenses and profanation .
		BlackListWordsDetector profanator = new BlackListWordsDetector("data/OffensiveProfaneWordList.txt");

		//Instantiate Spaces detector
		LinguisticFeaturesDetector linguisticDetector = new LinguisticFeaturesDetector("data/OffensiveProfaneWordList.txt");

		//Instantiate Blacklist detector
		LinguisticFeaturesDetectorTrieST linguisticDetectorTrieST = new LinguisticFeaturesDetectorTrieST("data/OffensiveProfaneWordList.txt");

		//Instantiate JazzySpellChecker
		JazzySpellChecker spellChecker = new JazzySpellChecker();

		//Instantiate EmoticonAnalyzer
		EmoticonAnalyzer emoticonAnalyzer = new EmoticonAnalyzer();


		for(Message cm: subset) {

			// add feature values to message

			//TODO implement counting forbidden phrases with one word per Line
			// cm.features[wordLines] = FeatureExtractor.wordLines(cm.message);

			cm.features[numberOfLines] = FeatureExtractor.numberOfLines(cm.message);
			cm.features[spaces] = linguisticDetectorTrieST.numberOfWordsWithSpaces(cm.message);
//			cm.features[letterLines] = linguisticDetector.numberOfOneLetterLines(cm.message);

			// remove <nl> tags before further feature extraction and lowercase string
			cm.message = cm.message.replace("<nl>", " ").toLowerCase();

//			cm.features[funkyWords] = FeatureExtractor.funkyWords(cm.message);
//			cm.features[consecutiveLetters] = FeatureExtractor.consecutiveLetters(cm.message);
			cm.features[alert] = linguisticDetectorTrieST.numberOfAlertWords(cm.message);
			cm.features[blacklist] = linguisticDetectorTrieST.numberOfBlackListWords(cm.message);
			
			cm.features[alert] = profanator.numberOfAlerts(cm.message);
			cm.features[blacklist] = profanator.numberOfOffensiveProfanes(cm.message);

			// Emoticon features
			cm.features[posEmoticons] = emoticonAnalyzer.positiveEmoticons(cm.message);
			cm.features[negEmoticons] = emoticonAnalyzer.negativeEmoticons(cm.message);
			cm.features[neuEmoticons] = emoticonAnalyzer.neutralEmoticons(cm.message);

			cm.features[misspelledWords] = spellChecker.countMisspelledWords(cm.message);
			cm.features[negativeSent] = sentiments.getNegativeSentiment(cm.message);
			cm.features[positiveSent] = sentiments.getPositiveSentiment(cm.message);

			// Correct spelling errors before export
			cm.message = "\"" + spellChecker.getCorrectedText(cm.message) + "\""; 

			System.out.println(cm.toString());

		}
	}

	private static void generateRawCsvFile(List<Message> subset, String sFileName)
	{
		try
		{
			FileWriter writer = new FileWriter(sFileName);

			// add lines
			for(Message message: subset) {

				writer.append(message.isPredator);
				writer.append(',');
				writer.append(message.senderID);
				writer.append(',');


				// add message to line
				String csvMessage = message.message;
				csvMessage = csvMessage.replace(",", " ");
				csvMessage = csvMessage.replace("\n", "");

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

	private static ArrayList<Message> readRawSubset(String file) {


		ArrayList<Message> set = new ArrayList<>();
		Message newMessage;
		FileInputStream fis;

		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {

				newMessage = new Message();
				String[] tuple = line.split(",");
				newMessage.isPredator = tuple[0];
				newMessage.senderID = tuple[1];
				newMessage.message = tuple[2];

				set.add(newMessage);

			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(file + " imported");
		return set;
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
