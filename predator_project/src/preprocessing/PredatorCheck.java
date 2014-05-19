package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import featureExtractors.BlackListWordsDetector;
import featureExtractors.EmoticonAnalyzer;
import featureExtractors.JazzySpellChecker;
import featureExtractors.LinguisticFeaturesDetectorTrieST;
import featureExtractors.SentimentAnalyser;


/**
 * Provides static methods for extracting feaures from message string
 *
 */
public class PredatorCheck {

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


	public static void correctPredators(String inputFile, String outputName){

		replaceP(readSubset(inputFile), outputName);

	}

	public static void replaceP(List<Message> subset, String outputName){

		int i = 0;
		PredatorIdentifier pi = new PredatorIdentifier("data/pan2012-list-of-predators-id_NEW.txt");
		for(Message cm: subset) {
			if(pi.isAPredator(cm.senderID).equalsIgnoreCase("p")){
				i++;
				cm.isPredator = "p";
			}else{
				cm.isPredator = "np";
			}
		}
		System.out.println(i + " predators found.");
		String saveFilePath = "data/subsetsWithFeatures/"+ outputName;
		// save to csv file - use method from dataparser
		generateCsvFile(subset, saveFilePath +".csv");

	}


	private static ArrayList<Message> readSubset(String file) {
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

				newMessage.features[wordLines] = Integer.parseInt(tuple[2]);
				newMessage.features[numberOfLines] = Integer.parseInt(tuple[3]);
				newMessage.features[spaces] = Integer.parseInt(tuple[4]);
				newMessage.features[letterLines] = Integer.parseInt(tuple[5]);
				newMessage.features[funkyWords] = Integer.parseInt(tuple[6]);
				newMessage.features[consecutiveLetters] = Integer.parseInt(tuple[7]);
				newMessage.features[alert] = Integer.parseInt(tuple[8]);
				newMessage.features[blacklist] = Integer.parseInt(tuple[9]);
				newMessage.features[posEmoticons] = Integer.parseInt(tuple[10]);
				newMessage.features[negEmoticons] = Integer.parseInt(tuple[11]);
				newMessage.features[neuEmoticons] = Integer.parseInt(tuple[12]);
				newMessage.features[negativeSent] = Integer.parseInt(tuple[13]);
				newMessage.features[positiveSent] = Integer.parseInt(tuple[14]);
				newMessage.features[misspelledWords] = Integer.parseInt(tuple[15]);

				newMessage.message = tuple[16];
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

	public static void main (String args[]) {

		correctPredators("data/subsetsWithFeatures/TEST_SET.csv", "TEST_SET_corrected");

	}

}
