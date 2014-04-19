package featureExtractors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BlackListSpacesDetector {
String file;
	ArrayList<String> wordList;

	/**
	 * Load a list of blacklist words and create a hasmap with it, where the
	 * profane word is the key for fast execution.
	 * @param file = the input file.
	 */
	public BlackListSpacesDetector(String file) {
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
	public int numberOfOffensiveProfanes(String text){
		int profanes = 0;
		for (String word : wordList) {
			//If a word ends with symbols (!,?), remove them so
			//the word can be matched.
			if(text.matches(word)){
				profanes++;
			}
		}
		return profanes;
	}
	
}
