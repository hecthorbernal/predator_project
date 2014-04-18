package blacklist;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class BlackListWordsDetector {
	String file;
	HashMap<String, Integer> wordList;

	/**
	 * Load a list of blacklist words and create a hasmap with it, where the
	 * profane word is the key for fast execution.
	 * @param file = the input file.
	 */
	public BlackListWordsDetector(String file) {
		this.file = file;
		wordList = new HashMap<String, Integer>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replace("\\n", "").replace("\\r", "");
				line = line.toLowerCase();
				wordList.put(line, 1);
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
	public int numberOfOffensiveProfanes(String text){
		int profanes = 0;
		String[] words = text.split("\\s");
		for(int i = 0; i < words.length; i++){
			if(wordList.containsKey(words[i])){
				profanes++;
			}
		}
		return profanes;
	}

}
