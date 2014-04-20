package featureExtractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Counts positive, negative and neutral emoticons in text
 * based on 3 seperate textfiles contianing emoticons
 *
 */
public class EmoticonAnalyzer {

	// Files for import
	File posListFile;
	File negListFile;
	File neuListFile;

	// Lsts of emoticons
	ArrayList<String> posEmoticonList;
	ArrayList<String> negEmoticonList;
	ArrayList<String> neuEmoticonList;


	/**
	 * Load lists of emoticons and create ArrayLists
	 * profane word is the key for fast execution.
	 * @param file = the input file.
	 */
	public EmoticonAnalyzer() {

		this.posListFile = new File("data/emoticons_positive.txt");
		this.negListFile = new File("data/emoticons_negative.txt");
		this.neuListFile = new File("data/emoticons_neutral.txt");

		//Load list
		posEmoticonList = loadFile(posListFile);
		negEmoticonList = loadFile(negListFile);
		neuEmoticonList = loadFile(neuListFile);

	}


	/**
	 * Counts positive emoticons
	 * @param text - text to search for emoticons
	 * @return number of emoticons
	 */
	public int positiveEmoticons(String text) {

		// Call shared method
		return countEmoticons(text, this.posEmoticonList);

	}

	/**
	 * Counts negative emoticons
	 * @param text - text to search for emoticons
	 * @return number of emoticons
	 */
	public int negativeEmoticons(String text) {

		// Call shared method
		return countEmoticons(text, this.negEmoticonList);

	}

	/**
	 * Counts neutral emoticons
	 * @param text - text to search for emoticons
	 * @return number of emoticons
	 */
	public int neutralEmoticons(String text) {

		// call shared method
		return countEmoticons(text, this.neuEmoticonList);

	}

	/**
	 * Counts emoticons from given list
	 * @param text
	 * @param list
	 * @return
	 */
	private int countEmoticons(String text, ArrayList<String> list) {

		int count = 0;

		for (String word: list)
			if (text.contains(word)) {
				//System.out.println(word);
				count++;
			}
		
		return count;


	}


	/**
	 * import emoticon list from textfile
	 * @param file - file to read
	 * @return Arraylist of emoticons
	 */

	private ArrayList<String> loadFile(File file) {

		ArrayList<String> list = new ArrayList<>();

		FileInputStream fis;
		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replace("\\n", "").replace("\\r", "");

				for(String s: line.split("\\s+")) {

					list.add(s.trim());

				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/*
	 * Only used for testing
	 */
	public static void main(String args[]){

		EmoticonAnalyzer emot = new EmoticonAnalyzer();

		System.out.println(emot.posEmoticonList);
		System.out.println(emot.negEmoticonList);
		System.out.println(emot.neuEmoticonList);

		String myTest = "I B^D like :( to code :-) :) ";

		System.out.println(emot.positiveEmoticons(myTest));
		System.out.println(emot.negativeEmoticons(myTest));
		System.out.println(emot.neutralEmoticons(myTest));

	}

}
