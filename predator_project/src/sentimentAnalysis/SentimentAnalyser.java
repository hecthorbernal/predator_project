package sentimentAnalysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SentimentAnalyser {
		String file = "";
		HashMap<String, Integer> wordList; 

		/**
		 * Loads the a file (based on the AFINN-111 list) and creates a hash
		 * map with it, where the word is the key and its sentiment the value.
		 * @param file = a file where one line contains one ore more words and 
		 * and int at the end (the sentiment value of the word +or-)
		 */
		public SentimentAnalyser(String file) {
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
					String[] arr = line.split("\\s");
					int value;
					String key = "";
					if(arr.length < 2){
						continue;
					}else if(arr.length < 3){
						value = Integer.parseInt(arr[1]);
						key = arr[0];
					}else{
						value = Integer.parseInt(arr[arr.length - 1]);
						for(int i = 0; i < arr.length -1; i++){
							key += arr[i]; 
						}
					}
					wordList.put(key, value);
					//System.out.println("Word: " + key + " sentiment = " + value);
					
				}
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/**
		 * Get the negative sentiment of a phrase.
		 * @param text = the phrase to be analyzed.
		 * @return
		 */
		public int getNegativeSentiment(String text){
			int negative = 0;
			String[] words = text.split("\\s");
			for(int i = 0; i < words.length; i++){
				if(wordList.containsKey(words[i])){
					if(wordList.get(words[i]) < 0){
						negative += wordList.get(words[i]);
					}
				}
			}
			return negative;
		}
		/**
		 * Get the positive sentiment of a phrase.
		 * @param text = the phrase to be analyzed.
		 * @return
		 */
		public int getPositiveSentiment(String text){
			int positive = 0;
			String[] words = text.split("\\s");
			for(int i = 0; i < words.length; i++){
				if(wordList.containsKey(words[i])){
					if(wordList.get(words[i]) > 0){
						positive += wordList.get(words[i]);
					}
				}
			}
			return positive;
		}
}
