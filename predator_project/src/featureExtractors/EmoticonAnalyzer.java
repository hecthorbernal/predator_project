package featureExtractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoticonAnalyzer {

	File posListFile;
	File negListFile;
	File neuListFile;

	ArrayList<String> posEmoticonList;
	ArrayList<String> negEmoticonList;
	ArrayList<String> neuEmoticonList;


	/**
	 * Load a list of blacklist words and create a hasmap with it, where the
	 * profane word is the key for fast execution.
	 * @param file = the input file.
	 */
	public EmoticonAnalyzer(String file) {

		this.posListFile = new File("data/emoticons_positive");
		this.posListFile = new File("data/emoticons_negative");
		this.posListFile = new File("data/emoticons_neutral");

		//Load list
		posEmoticonList = loadFile(posListFile);
		negEmoticonList = loadFile(negListFile);
		neuEmoticonList = loadFile(neuListFile);

	}


	public int positiveEmoticons(String text) {

		return countEmoticons(text, this.posEmoticonList);

	}

	public int negativeEmoticons(String text) {

		return countEmoticons(text, this.negEmoticonList);

	}

	public int neutralEmoticons(String text) {

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
			if (text.contains(word))
				count++;

		return count;


	}


	/**
	 * reads emoticons from textfile
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
				list.add(line);
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


	}

}
