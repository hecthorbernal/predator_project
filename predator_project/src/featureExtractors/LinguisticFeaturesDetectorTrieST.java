package featureExtractors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinguisticFeaturesDetectorTrieST {
	String file;
	static TrieST<Integer> wordTrie = new TrieST<>();


	/**
	 * Load a list of blacklist words
	 * @param file = the input file.
	 */
	public LinguisticFeaturesDetectorTrieST(String file) {
		this.file = file;

		FileInputStream fis;
		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				line = line.replace("\\n", "").replace("\\r", "");

				// add word to Trie
				wordTrie.put(line, i++);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int numberOfAlertWords(String text) {

		int count = 0;

		for(String s: text.split(" ")) {

			if (wordTrie.contains(s))
				count++;

		}

		return count;
	}


	public static int numberOfBlackListWords(String text) {

		int countBlackListWords = 0;

		for (String s:text.split("\\s+")) {

			for (int i = 0; i < s.length()-1; i++) {

				if (wordTrie.containsBlackListWord(s.substring(i))) {
					//System.out.println(s.substring(i));
					countBlackListWords++;
				}
			}

		}

		return countBlackListWords;

	}

	/*
	 * Only used for testing
	 */
	public static void main(String args[]){

		// Small test
		LinguisticFeaturesDetectorTrieST myDetector = new LinguisticFeaturesDetectorTrieST("data/OffensiveProfaneWordList.txt");

		System.out.println(myDetector.wordTrie.size());

		String s = "ssexy sex fuck shi tabuse fucking whoreass***homo";

		System.out.println(numberOfAlertWords(s));
		System.out.println(numberOfBlackListWords(s));


	}

}
