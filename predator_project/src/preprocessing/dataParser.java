package preprocessing;

import java.io.FileWriter;
import java.io.IOException;
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

		// Now we have a List<Conversations> :-)
		// Each Conversation carries a List<Messages> (author, time, text)

		// TODO create (different) subsets from conversations
		//TODO extract and add features 

		// TEST - create subset containing all message lines
		List<Message> mySubSet = myDataParser.createSubSetExample();
		System.out.println(mySubSet.size());


		// TEST CSV export
		generateCsvFile(mySubSet, "data/test.csv");


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
				writer.append(message.message);
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



}
