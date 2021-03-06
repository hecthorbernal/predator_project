package preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import xmlImport.Conversation;
import xmlImport.ConversationMessage;
import xmlImport.StaXParser;
import featureExtractors.BlackListWordsDetector;
import featureExtractors.EmoticonAnalyzer;
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
	private static List<Conversation> np_conversations;
	private static List<Conversation> p_conversations;
	private static List<Conversation> test_conversations;

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
	private static final String nonPredatoryFile = null;


	public dataParser() {

	}

	public dataParser(String file) {

		this.conversations = new StaXParser().readConfig(file);

	}

	public static void main(String[] args) throws IOException {

		// create dataParser from xml-file 
		dataParser myDataParser = new dataParser("data/pan12-sexual-predator-identification-test-corpus-2012-05-17.xml");
		//for trainning
		//myDataParser.splitConversationListByPredatorOrNot();
		//for tests
		myDataParser.splitConversationList();
		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		//TODO create (different) subsets from conversations
		//TODO extract and add features 

		//TEST create subset with messages concatenated per author per conversation
		//		List<Message> mySubSet = myDataParser.createSubSetExample();
		//		
		//		System.out.println(mySubSet.size());

//		Process the test data
		List<Message> mySubSetTEST = myDataParser.generateTestSet(test_conversations);
		System.out.println(mySubSetTEST.size());
		generateRawCsvFile(mySubSetTEST, "data/rawFiles/test_raw_only_P.csv");
		//		List<Message> L15_P = readRawSubset("data/rawFiles/L15_predator_raw.csv");
		//		addFeaturesToSubset(L15_P);
		//		generateCsvFile(L15_P, "data/subsets/L15_P.csv");
		//		System.out.println(L15_P.size());

//		//Process the predator data
//		List<Message> mySubSetL15_P = myDataParser.generateL15(p_conversations);
//		System.out.println(mySubSetL15_P.size());
//		generateRawCsvFile(mySubSetL15_P, "data/rawFiles/L15_predator_raw.csv");
//		//		List<Message> L15_P = readRawSubset("data/rawFiles/L15_predator_raw.csv");
//		//		addFeaturesToSubset(L15_P);
//		//		generateCsvFile(L15_P, "data/subsets/L15_P.csv");
//		//		System.out.println(L15_P.size());
//
//		//Process the non-predator data
//		List<Message> mySubSetL15_NP = myDataParser.generateL15(np_conversations);
//		System.out.println(mySubSetL15_NP.size());
//		generateRawCsvFile(mySubSetL15_NP, "data/rawFiles/L15_non_predator_raw.csv");
//		//		List<Message> L15_NP = readRawSubset("data/rawFiles/L15_non_predator_raw.csv");
//		//		// Add features to L15_NP
//		//		addFeaturesToSubset(L15_NP);
//		//		generateCsvFile(L15_NP, "data/subsets/L15_NP.csv");
//		//		System.out.println(L15_NP.size());
//
//		//Generate W15 predator
//		List<Message> mySubSetW15_P = myDataParser.generateW15(p_conversations, "data/W15_P_splitted_convers_list");
//		//TEST CSV export
//		System.out.println(mySubSetW15_P.size());
//		generateRawCsvFile(mySubSetW15_P, "data/rawFiles/W15_predator_raw.csv");
//		//		List<Message> W15_P = readRawSubset("data/rawFiles/W15_predator_raw.csv");
//		//		addFeaturesToSubset(W15_P);
//		//		generateCsvFile(W15_P, "data/subsets/W15_P.csv");
//		//		System.out.println(W15_P.size());
//
//		//Generate W15 non-predator
//		List<Message> mySubSetW15_NP = myDataParser.generateW15(np_conversations, "data/W15_NP_splitted_convers_list");
//		//TEST CSV export
//		System.out.println(mySubSetW15_NP.size());
//		generateRawCsvFile(mySubSetW15_NP, "data/rawFiles/W15_non_predator_raw.csv");
//		//		List<Message> W15_NP = readRawSubset("data/rawFiles/W15_non_predator_raw.csv");
//		//		addFeaturesToSubset(W15_NP);
//		//		generateCsvFile(W15_NP, "data/subsets/W15_NP.csv");
//		//		System.out.println(W15_NP.size());
//
//		//Generate HP15
//		myDataParser.generateHP15(p_conversations, "data/rawFiles/HP15_predator_under_15min_raw.csv", "data/rawFiles/HP15_predator_over_15min_raw.csv");
//		//This should be run only when the HP15 conversations over 15 min have been manually shortened.
//		List<Message> mySubSetHP15_P = myDataParser.mergeHP15_files("data/rawFiles/HP15_predator_under_15min_raw.csv", "data/rawFiles/HP15_predator_over_15min_raw.csv");
//		System.out.println(mySubSetHP15_P.size());
//		generateRawCsvFile(mySubSetHP15_P, "data/rawFiles/HP15_predator_raw.csv");
//		//		List<Message> HP15_P = readRawSubset("data/rawFiles/HP15_predator_raw.csv");
//		//		addFeaturesToSubset(HP15_P);
//		//		generateCsvFile(HP15_P, "data/subsets/HP15_P.csv");
//		//		System.out.println(HP15_P.size());
//
//		//Example: Generate balanced HP15 set
//		//		myDataParser.randomNP("data/balancedSubsets/HP15_20P.csv", 02);
//		myDataParser.generateBalacedSubset("data/rawFiles/W15_predator_raw.csv", "data/rawFiles/W15_non_predator_raw.csv","data/balancedSubsets/W15_20P.csv", 20);
//		myDataParser.generateBalacedSubset("data/rawFiles/HP15_predator_raw.csv","data/rawFiles/W15_non_predator_raw.csv","data/balancedSubsets/HP15_20P.csv", 20);
	}


	/**
	 * Generate a balanced subset using a non predatory file and a predatory
	 * one from data/subsets/
	 * Make a data set either a specified percentage of predators conversations in them.
	 * @param predatoryFile
	 * @param non_predatory_file
	 * @param outputFile
	 * @param percent_of_predatory_lines
	 */
	public void generateBalacedSubset(String predatoryFile, String nonPredatorFile,
			String outputFile, int percent_of_predatory_lines){	
		System.out.println("Generator of balaced subsets implemented");
		// Location of file to read
		File predator_File = new File(predatoryFile);
		File non_predatory_file =  new File(nonPredatorFile);
		ArrayList<String> predators = new ArrayList<String>();
		ArrayList<String> nonPredators = new ArrayList<String>();
		ArrayList<String> balancedSubset = new ArrayList<String>();
		HashSet<String> uniquePredators = new HashSet<String>();
		HashSet<String> uniqueNonPredators = new HashSet<String>();

		int numberOfPredators = 0;

		FileInputStream pStream;
		FileInputStream npStream;

		try {
			pStream = new FileInputStream(predator_File); 
			npStream = new FileInputStream(non_predatory_file); 

			//Construct BufferedReader for predators
			BufferedReader pBr = new BufferedReader(new InputStreamReader(pStream));
			String line = null;

			// collect unique p-ids and all lines
			while ((line = pBr.readLine()) != null) {

				uniquePredators.add(line.split(",")[1]);
				predators.add(line);
			}

			pBr.close();

			//Construct BufferedReader for predators
			BufferedReader npBr = new BufferedReader(new InputStreamReader(npStream));
			line = null;

			// collect unique p-ids and all lines
			while ((line = npBr.readLine()) != null) {

				uniqueNonPredators.add(line.split(",")[1]);
				nonPredators.add(line);
			}

			pBr.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		numberOfPredators = uniquePredators.size();
		System.out.println("p:" + numberOfPredators);
		System.out.println("np: " + uniqueNonPredators.size());


		try

		{
			FileWriter writer = new FileWriter(outputFile);

			// The number of non predator conversations to be added to the balanced set
			//is evaluated by finding first what 1% of the balanced subset are 
			//(numberOfPredators/percent_of_predatory_lines) and the multiplying
			// with the percentage wanted of non predators.

			int numberOfNonPredators = (int) Math.round(((100 - percent_of_predatory_lines)*((float)numberOfPredators/(float)percent_of_predatory_lines)));
			//In the case where there are not enough nonPredators,
			//the number of predators has to be evaluated at chosen randomly.
			System.out.println(numberOfNonPredators);

			if(numberOfNonPredators > uniqueNonPredators.size()){
				System.out.println("Error: not enough NP!!");

				// Change number of predators to match number of non predators
				numberOfPredators = (int) Math.round((percent_of_predatory_lines*((float)uniqueNonPredators.size()/(float)(100 -percent_of_predatory_lines))));

			}

			//The array holding the predators are shuffled,
			// and afterwards it is possible to take the first
			// x predators of the array, and still have chosen them
			//randomly.

			uniqueNonPredators.clear();
			uniquePredators.clear();

			Collections.shuffle(predators);
			for(String predator: predators){
				if(!uniquePredators.contains(predator.split(",")[1])){ //check id
					balancedSubset.add(predator); // add line
					uniquePredators.add(predator.split(",")[1]); // add id
					if(uniquePredators.size() == numberOfPredators) break;
				}
			}

			System.out.println(numberOfPredators + " unique random predators added to balanced subset.");
			//all non predators are added.

			for (String nonPredator: nonPredators){
				if (!uniqueNonPredators.contains(nonPredator.split(",")[1]) && !nonPredator.split(",")[2].equals("<<nl>")){ //check id and skip empty message
					balancedSubset.add(nonPredator); // add line
					uniqueNonPredators.add(nonPredator.split(",")[1]); // add id
					if(uniqueNonPredators.size() == numberOfNonPredators) break;
				}
			}

			System.out.println(numberOfNonPredators + " unique non predators added to balanced subset.");

			//The balanced subset is written to a file,
			//after it has been randomly shuffled.
			Collections.shuffle(balancedSubset);
			for (String conversation: balancedSubset){
				writer.append(conversation+"\n");
			}


			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	private void splitConversationList() {
		//The list new_list contains now coversation where only one author is present.
		List<Conversation> newList = splitConversatitionsByAuthor(conversations);

		List<Conversation> test_list = new ArrayList<Conversation>();

		//Instantiate the predator identifier
		PredatorIdentifier predatorDetector = new PredatorIdentifier("data/pan2012-list-of-predators-id_NEW.txt");
		PredatorLineIdentifier pli = new PredatorLineIdentifier("data/predatory_lines_list.txt");
		int p_c = 0;
		for(Conversation c: newList) {
			int firstMessageTime = 0;
			int lastMessageTime = 0;
			int firsNotNormalized = 0;
			boolean isFirst = false;	
			boolean containsPredLines = false;
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
				}
			}
			
			if(predatorDetector.isAPredator(c.getAuthor()).equalsIgnoreCase("p")){
				if(pli.isAPredatorLine(c.getId())){
					c.setPredator(true);
					p_c++;
					test_list.add(c);
				}
			}else{
				c.setPredator(false);
				test_list.add(c);
			}
		}
//		create subset from conversations
		dataParser.test_conversations = test_list;
		System.out.println("Authors have been separated from each other. Predators found = " + p_c);

	}


	/**
	 * Split each conversation in to a set of conversations having one
	 * unique author each.
	 */
	private void splitConversationListByPredatorOrNot() {
		//The list new_list contains now coversation where only one author is present.
		List<Conversation> newList = splitConversatitionsByAuthor(conversations);

		//Iterate through the messages to get:
		//1. The lenght of each conversation
		//Discard all messages thart havent been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> predator_list = new ArrayList<Conversation>();
		List<Conversation> non_predator_list = new ArrayList<Conversation>();

		//Instantiate the predator identifier
		PredatorIdentifier predatorDetector = new PredatorIdentifier("data/pan2012-list-of-predators-id_NEW.txt");

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
				}
			}
			if(predatorDetector.isAPredator(c.getAuthor()).equalsIgnoreCase("p")){
				c.setPredator(true);
				predator_list.add(c);
			}else{
				c.setPredator(false);
				non_predator_list.add(c);
			}
		}

		// create subset from conversations
		dataParser.np_conversations = non_predator_list;
		dataParser.p_conversations = predator_list;
		System.out.println("Predator and Non Predator conversations have been separated from each other.");

	}

	/**
	 * Go through the conversations and those over 15 min should go to a different
	 * list with the time stamp of each message added for manual selection.
	 * @param conversations
	 * @param file_under15
	 * @param file_over15
	 */
	private void generateHP15(List<Conversation> conversations, String file_under15, String file_over15 ) {

		//Iterate through the messages to get:
		//1. The lenght of each conversation
		//Discard all messages thart havent been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> over15_list = new ArrayList<Conversation>();
		List<Conversation> under15_list = new ArrayList<Conversation>();
		for(Conversation c: conversations) {
			//instantiate to something big.
			int firstMessageTime = 90000000;
			int lastMessageTime = 0;
			for(ConversationMessage cm: c.messages) {
				//Time stamps have been normalize change logic to max min updates
				if(cm.getNormalized_time() < firstMessageTime){
					firstMessageTime = cm.getNormalized_time();
				}else if(cm.getNormalized_time() > lastMessageTime){
					lastMessageTime = cm.getNormalized_time();
				}
			}
			int line_number = 0;
			int duration = getDuration(firstMessageTime, lastMessageTime);
			if(duration <= 15){
				under15_list.add(c);
				line_number++;
			}else{
				//If the conversation lasted more than 15 min discard all messages that occurred 
				//outside the time lapse required i.e. L15
				for(ConversationMessage cm: c.messages) {
					Conversation tmpConversation = new Conversation(c.getId(), c.getAuthor());
					tmpConversation.addC_Message(cm);
					tmpConversation.setTimestamp(cm.getNormalized_time());
					over15_list.add(tmpConversation);
				}
			}
		}
		// create subset from conversations


		generateRawCsvFile(generateSubSet(under15_list), file_under15);
		generateRawCsvFile(generateSubSet(over15_list), file_over15);
	}

	/**
	 * For the set HP15, merge the manually sorted conversations to the ones
	 * under 15 mins
	 * @param under15minFile
	 * @param over15minFileManuallyProcessed
	 * @return
	 */
	private List<Message> mergeHP15_files(String under15minFile, String over15minFileManuallyProcessed) {
		//TODO Merge files after conversations over 15 min have been manually shortened
		List<Message> messages_under = readRawSubset(under15minFile);
		List<Message> messages_over = readRawHPFile(over15minFileManuallyProcessed);
		List<Message> merged = new ArrayList<Message>();
		for(Message m : messages_under){
			merged.add(m);
		}
		for(Message m : messages_over){
			merged.add(m);
		}
		return merged;
	}

	/**
	 * Create set L15
	 * @param print_output TODO
	 */
	private List<Message> generateTestSet(List<Conversation> conversations ) {

		//Iterate through the messages to get:
		//1. The lenght of each conversation
		//Discard all messages thart havent been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> finalList = new ArrayList<Conversation>();
		for(Conversation c: conversations) {
			//instantiate to something big.
				finalList.add(c);
		}
		// create subset from conversations
		return generateSubSet(finalList);
	}
	private List<Message> generateL15(List<Conversation> conversations ) {

		//Iterate through the messages to get:
		//1. The lenght of each conversation
		//Discard all messages thart havent been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> finalList = new ArrayList<Conversation>();
		for(Conversation c: conversations) {
			//instantiate to something big.
			int firstMessageTime = 90000000;
			int lastMessageTime = 0;
			for(ConversationMessage cm: c.messages) {
				//Time stamps have been normalize change logic to max min updates
				if(cm.getNormalized_time() < firstMessageTime){
					firstMessageTime = cm.getNormalized_time();
				}else if(cm.getNormalized_time() > lastMessageTime){
					lastMessageTime = cm.getNormalized_time();
				}
			}
			int line_number = 0;
			int duration = getDuration(firstMessageTime, lastMessageTime);
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
						//System.out.println("Splitting conversation: " + c.getId() + "\nduration: " + duration + "minutes \nfirst: " +									firstMessageTime + " last: " + lastMessageTime + "\nAdded to L15_raw.csv line: " + ++line_number);
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
	 * @param print_output TODO
	 * @return
	 * @throws IOException 
	 */
	private List<Message> generateW15(List<Conversation> conversations, String file) throws IOException {
		//Iterate through the messages to get:
		//1. The lenght of each conversation
		//Discard all messages thart havent been send during the last 15 minutes of the 
		//conversation (L15).
		List<Conversation> finalList = new ArrayList<Conversation>();

		FileWriter outputWriter = new FileWriter(file);
		for(Conversation c: conversations) {
			//instantiate to something big.
			int firstMessageTime = 90000000;
			int lastMessageTime = 0;
			for(ConversationMessage cm: c.messages) {
				//Time stamps have been normalize change logic to max min updates
				if(firstMessageTime > cm.getNormalized_time()){
					firstMessageTime = cm.getNormalized_time();
				}else if(lastMessageTime  < cm.getNormalized_time()){
					lastMessageTime = cm.getNormalized_time();
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
				//talk to Yun next time.
				int limit = 15;
				int number_of_segments = duration/limit;
				//take the last segment even though it is shorter than 15 min.
				if(duration%limit > 0){
					number_of_segments++;
				}
				outputWriter.append("conversation " + c.getId()+ " author: " + c.getAuthor() + " in " + number_of_segments + "segments\n");
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
					}
					limit += 15;
				}
			}
		}
		outputWriter.flush();
		outputWriter.close();
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
		PredatorIdentifier predatorDetector = new PredatorIdentifier("data/pan2012-list-of-predators-id_NEW.txt");

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
			if(c.isTimeSet()){
				newMessage.timeStamp = c.getTimestamp();
			}
			String cID = c.getId();
			newMessage.cID = cID;
			// add message to subset
			subSet.add(newMessage);

			//System.out.println(newMessage);
		}
		return subSet;
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
				writer.append(',');
				writer.append(message.timeStamp);
				writer.append(',');
				writer.append(message.cID);
				writer.append(',');
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

	/**
	 * Read the raw HP file of conversations over 15 min and merge those 
	 * messages corresponding to the same author and conversation into one
	 * message to match the format of all the other raw files.
	 * @param file
	 * @return
	 */
	private static ArrayList<Message> readRawHPFile(String file) {
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
				newMessage.cID = tuple[4];

				set.add(newMessage);

			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Message> finalSet = new ArrayList<>();
		Message tmpMessage = null;
		for(Message message : set){
			if(tmpMessage == null){
				tmpMessage = new Message(message.senderID, message.message);
				tmpMessage.isPredator = message.isPredator;
				tmpMessage.cID = message.cID;
			}else if(tmpMessage.senderID.equalsIgnoreCase(message.senderID) &&
					tmpMessage.cID.equalsIgnoreCase(message.cID)){
				tmpMessage.message += "<nl>" + message.message;
			}else{
				finalSet.add(tmpMessage);
				tmpMessage = new Message(message.senderID, message.message);
				tmpMessage.isPredator = message.isPredator;
				tmpMessage.cID = message.cID;
			}
		}
		System.out.println(file + " imported");
		return finalSet;
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
		PredatorLineIdentifier pli = new PredatorLineIdentifier("data/predatory_lines_list.txt");
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
