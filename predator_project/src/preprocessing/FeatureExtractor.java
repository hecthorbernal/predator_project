/**
 * 
 */
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
public class FeatureExtractor {

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

	private static SentimentAnalyser sentiments = new SentimentAnalyser("data/AFINN-111.txt");
	private static BlackListWordsDetector forbiddenPhrasesDetector = new BlackListWordsDetector("data/blacklists/en_us.matchWholeWord.txt");
	private static BlackListWordsDetector alertDetector = new BlackListWordsDetector("data/blacklists/en_us.isAlert.txt");
	private static LinguisticFeaturesDetectorTrieST blackListDetectorTrieST = new LinguisticFeaturesDetectorTrieST("data/blacklists/en_us.isBlackList.txt");
	private static JazzySpellChecker spellChecker = new JazzySpellChecker();
	private static EmoticonAnalyzer emoticonAnalyzer = new EmoticonAnalyzer();


	public static void addFeaturesToSubset(String inputFile, String outputName){

		addFeaturesToSubset(readSubset(inputFile), outputName);

	}

	public static void addFeaturesToSubset(List<Message> subset, String outputName){

		// For statitistics
		HashSet<String> uniqueWords = new HashSet<>();
		HashSet<String> uniqueUsers = new HashSet<>();
		int countMisspelledWords;
		int countNumberOfUniqueWords;
		int countNumberOfLines = 0;
		int countUniqueUsers;

		float avgOccurrenceAlertWords = 0;;
		float avgOccurrenceBlacklistWords = 0;

		float avgLetterLines = 0;
		float avgWordLines = 0;
		float avgNumberOfLines = 0;
		float avgSpaces = 0;
		float avgNonletterwords = 0;
		float avgConsecutiveLetters = 0;
		float avgMisspellings = 0;

		for(Message cm: subset) {

			// forbidden phrases
			cm.features[wordLines] = forbiddenPhrasesDetector.forbiddenPhrases(cm.message);
			avgWordLines += cm.features[wordLines];

			cm.features[numberOfLines] = numberOfLines(cm.message);
			avgNumberOfLines += cm.features[numberOfLines];

			cm.features[spaces] = blackListDetectorTrieST.numberOfWordsWithSpaces(cm.message);
			avgSpaces += cm.features[spaces];

			cm.features[letterLines] = blackListDetectorTrieST.numberOfOneLetterLines(cm.message);
			avgLetterLines += cm.features[letterLines];

			// remove <nl> tags before further feature extraction and lowercase string
			cm.message = cm.message.replace("<nl>", " ").replace("$","");

			cm.features[funkyWords] = funkyWords(cm.message);
			avgNonletterwords += cm.features[funkyWords];

			cm.features[consecutiveLetters] = consecutiveLetters(cm.message);
			avgConsecutiveLetters += cm.features[consecutiveLetters];

			cm.features[alert] = alertDetector.numberOfAlerts(cm.message);
			avgOccurrenceAlertWords += cm.features[alert];
			
			cm.features[blacklist] = blackListDetectorTrieST.numberOfBlackListWords(cm.message);			
			avgOccurrenceBlacklistWords += cm.features[blacklist];

			// Emoticon features
			cm.features[posEmoticons] = emoticonAnalyzer.positiveEmoticons(cm.message);
			cm.features[negEmoticons] = emoticonAnalyzer.negativeEmoticons(cm.message);
			cm.features[neuEmoticons] = emoticonAnalyzer.neutralEmoticons(cm.message);

			cm.features[negativeSent] = sentiments.getNegativeSentiment(cm.message);
			cm.features[positiveSent] = sentiments.getPositiveSentiment(cm.message);

			cm.features[misspelledWords] = spellChecker.countMisspelledWords(cm.message);
			avgMisspellings += cm.features[misspelledWords];

			// Correct spelling errors before export
			cm.message = spellChecker.getCorrectedText(cm.message);


			/*
			 * Statistics updates in loop
			 */

			// collect unique words in hashset
			for(String s: cm.message.split(" "))
				uniqueWords.add(s);

			// add citaion marks to message before export
			cm.message = "\"" + cm.message + "\"";

			// update hashset of unique users
			uniqueUsers.add(cm.senderID);
			//update number of lines

			// count number of lines
			countNumberOfLines += cm.features[numberOfLines];

		}

		// Statistics summarize
		countMisspelledWords = spellChecker.numberOfUniqueMisspelledWords();
		countNumberOfUniqueWords = uniqueWords.size();
		countUniqueUsers = uniqueUsers.size();

		avgOccurrenceAlertWords /= countUniqueUsers;
		avgOccurrenceBlacklistWords /= countUniqueUsers;
		
		avgLetterLines /= countUniqueUsers ;
		avgWordLines /= countUniqueUsers;
		avgNumberOfLines /= countUniqueUsers;
		avgSpaces /= countUniqueUsers;
		avgNonletterwords /= countUniqueUsers;
		avgConsecutiveLetters /= countUniqueUsers;
		avgMisspellings /= countUniqueUsers;


		String saveFilePath = "data/subsetsWithFeatures/"+ outputName;

		// save to csv file - use method from dataparser
		generateCsvFile(subset, saveFilePath +".csv");

		// save statistics to file
		generateStatFile(saveFilePath + "_stats.txt",
				countUniqueUsers, countNumberOfLines, countNumberOfUniqueWords,countMisspelledWords,
				avgOccurrenceAlertWords, avgOccurrenceBlacklistWords,
				avgLetterLines, avgWordLines, avgNumberOfLines, avgSpaces, avgNonletterwords, avgConsecutiveLetters, avgMisspellings
				);

	}

	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int numberOfLines(String s) {

		return (s.split("<nl>").length);

	}

	/**
	 * returns count of words in a message
	 * for calculating average occurrence of different features
	 * @param s
	 * @return
	 */
	public static int numberOfWords(String s) {

		return(s.replace("<nl>", " ").split(" ").length);

	}

	/**
	 * I asume that this is the same as "Non Letter Words"
	 * don't match contractions
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int funkyWords(String s) {
		String[] words = s.split("\\s");
		int counter = 0;
		for(int i = 0; i < words.length; i++){
			String word = words[i].toLowerCase();
			word = word.replaceAll("^\\w\\w+\\?$", "");
			word = word.replaceAll("^\\w\\w+\\!$", "");
			if(word.matches("\\w+'(m|re|s|ve)$")){
				continue;
			}
			Pattern eee  = Pattern.compile(".*[\\W\\w]*\\W+[\\W\\w]*.*");
			Matcher m = eee.matcher(word);
			if(m.matches()){
				counter++;
			}
		}
		return counter;

	}


	/**
	 * Cosecutive identical letters CL
	 * More than 2 identical consecutive letters
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int consecutiveLetters(String s) {
		String[] words = s.split("\\s");
		int counter = 0;
		for(int i = 0; i < words.length; i++){
			Pattern eee = Pattern.compile(".*(\\w)\\1\\1+.*");
			Matcher m = eee.matcher(words[i]);
			if(m.matches()){
				counter++;
			}
		}
		return counter;

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
	 * Generates statistic file
	 */
	private static void generateStatFile(String sFileName,
			int nUsers,
			int nLines,
			int uWords,
			int mWords,
			float avgAlert,
			float avgBlacklist,
			float LL,
			float WL,
			float NL,
			float SP,
			float NLW,
			float CL,
			float MS
			){

	
		try

		{
			FileWriter writer = new FileWriter(sFileName);

			// Create headings
			writer.append("Filename, NumberOfUsers, NumberOfLines, UniqueWords, MisspelledWords, avgAlertWords, avgBlacklistWords, LL, WL, NL, SP, NLW, CL, MS");
			writer.append('\n');
			writer.append(sFileName + "," + nUsers  + "," + nLines + "," + uWords + "," + mWords + ",");
			writer.append(avgAlert + "," + avgBlacklist + ",");
			writer.append(LL + "," + WL+ "," + NL + "," + SP + "," + NLW + "," + CL + "," + MS);
			writer.append('\n');

			writer.flush();
			writer.close();
		}

		catch(IOException e)

		{
			e.printStackTrace();
		}

		System.out.println("Exported statistic file: " + sFileName);

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

	public static void main (String args[]) {

		addFeaturesToSubset("data/rawfiles/HP15_predator_over_15min_chunk1_raw_processed.csv", "test");

	}

}
