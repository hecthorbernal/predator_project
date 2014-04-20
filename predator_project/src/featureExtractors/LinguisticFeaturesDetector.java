package featureExtractors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinguisticFeaturesDetector {
	String file;
	ArrayList<String> wordList;

	/**
	 * Load a list of blacklist words and create a hasmap with it, where the
	 * profane word is the key for fast execution.
	 * @param file = the input file.
	 */
	public LinguisticFeaturesDetector(String file) {
		this.file = file;
		wordList = new ArrayList<String>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replace("\\n", "").replace("\\r", "");
				wordList.add(line);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Counts the number of profane words in the given conversation.
	 * @param text = the conversation to analyze.
	 * @return the number of profane words.
	 */
	public int numberOfSpaces(String text){
		int profanes = 0;
		Pattern pattern;
		Matcher matcher;
		for (String word : wordList) {
			//If a word ends with symbols (!,?), remove them so
			//the word can be matched.
			
			pattern = regexWordsWithSpaces(word);
			matcher = pattern.matcher(text);
			if(matcher.find()){
				profanes++;
			}
		}
		return profanes;
	}

	public int numberOfOneLetterLines(String text){
		int profanes = 0;
		Pattern pattern;
		Matcher matcher;
		for (String word : wordList) {
			//If a word ends with symbols (!,?), remove them so
			//the word can be matched.
			
			pattern = regexOneLetterLines(word);
			matcher = pattern.matcher(text);
			if(matcher.find()){
				profanes++;
			}
		}
		return profanes;
	}

	

	private static Pattern regexWordsWithSpaces(String s) {

		// any number of other chars as prefix
		String regexString = ".*" + s.charAt(0);

		// one or more spaces between letters in word
		// no line-breaks 
		for (int i = 1; i < s.length(); i++)
			regexString += "[\\s && [^\\r]]+" + s.charAt(i);

		regexString += ".*";  // any number of chars as postfix
		
		return Pattern.compile(regexString);

	}
	
	private static Pattern regexOneLetterLines(String s) {

		// any number of other chars as prefix
		String regexString = ".*" + s.charAt(0);

		// one or more spaces and line-breaks between letters in word
		// no line-breaks 
		for (int i = 1; i < s.length(); i++)
			regexString += "\\s*\\r+\\s*" + s.charAt(i);

		regexString += ".*";  // any number of chars as postfix
		//System.out.println(regexString);
		
		return Pattern.compile(regexString);

	}
	
	/*
	 * Only used for testing
	 */
	public static void main(String args[]){

		// Small test
		LinguisticFeaturesDetector myDetector = new LinguisticFeaturesDetector("data/OffensiveProfaneWordList.txt");

		String s = "gffgds \r e \rx";
		
		Pattern p = regexOneLetterLines("sex");

		Matcher m = p.matcher(s);
		System.out.println(m.find());
		
		System.out.println(myDetector.numberOfSpaces(s));
		System.out.println(myDetector.numberOfOneLetterLines(s));


	}

}
