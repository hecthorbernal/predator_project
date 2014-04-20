/**
 * 
 */
package preprocessing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import featureExtractors.JazzySpellChecker;


/**
 * Provides static methods for extracting feaures from message string
 *
 */
public class FeatureExtractor {
	
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int letterLines(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int wordLines(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int numberOfLines(String s) {
		
		//TODO CANNOT BE IMPLMENTED HERE
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int spaces(String s) {
		
		//TODO implement method
		return -1;
		
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
			if(word.matches("\\w+'(m|re|s|ve|t)")){
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
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int posEmoticons(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int neuEmoticons(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int negEmoticons(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
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
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int alert(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
//	public static int blackList(String s) {
//		
//		//TODO CANNOT BE IMPLMENTED HERE
//		return -1;
//		
//	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
//	public static int misspelledWords(String s) {
//		
//		//NOT IMPLEMENTED HERE - USE JAZZYSPELLCHECKER
//		return -1;
//	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
//	public static int negativeSent(String s) {
//		
//		//TODO CANNOT BE IMPLMENTED HERE
//		return -1;
//		
//	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
//	public static int PositiveSent(String s) {
//		
//		//TODO CANNOT BE IMPLMENTED HERE
//		return -1;
//		
//	}

}
