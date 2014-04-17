/**
 * 
 */
package preprocessing;

import spellChecking.JazzySpellChecker;

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
		
		//TODO implement method
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
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int funkyWords(String s) {
		
		//TODO implement method
		return -1;
		
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
		
		//TODO implement method
		return -1;
		
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
	public static int blackList(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int misspelledWords(String s) {
		
		// Instantiate spellChecker
		JazzySpellChecker jazzySpellChecker = new JazzySpellChecker();
		
		// return number of misspelled words in text
		return jazzySpellChecker.getMisspelledWords(s).size();
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int negativeSent(String s) {
		
		//TODO implement method
		return -1;
		
	}
	
	/**
	 * @param s string to extract feature from
	 * @return feature value as integer
	 */
	public static int PositiveSent(String s) {
		
		//TODO implement method
		return -1;
		
	}

}
