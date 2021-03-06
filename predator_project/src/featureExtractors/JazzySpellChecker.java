package featureExtractors;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;




import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.event.TeXWordFinder;

public class JazzySpellChecker implements SpellCheckListener {

	private SpellChecker spellChecker;
	private List<String> misspelledWords;
	HashSet<String> uniqueMisspelledWords = new HashSet<>();

	public JazzySpellChecker() {

		misspelledWords = new ArrayList<String>();
		initialize();
	}
	
	private static SpellDictionaryHashMap dictionaryHashMap;

	static{

		File dict = new File("dictionary/dictionary.txt");
		try {
			dictionaryHashMap = new SpellDictionaryHashMap(dict);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initialize(){
		spellChecker = new SpellChecker(dictionaryHashMap);
		spellChecker.addSpellCheckListener(this);  
	}

	
	/**
	 * get a list of misspelled words from the text
	 * @param text
	 */
	public List<String> getMisspelledWords(String text) {
		StringWordTokenizer texTok = new StringWordTokenizer(text,
				new TeXWordFinder());
		try {
			spellChecker.checkSpelling(texTok);
		} catch (Exception e) {
			System.out.println("Error. The following string couldn't get parsed by Jazzy: " + text + "\n");
		}
		return misspelledWords;
	}

	public int countMisspelledWords(String text) {

		StringWordTokenizer texTok = new StringWordTokenizer(text,
				new TeXWordFinder());
		spellChecker.checkSpelling(texTok);
		
		// add words to hashset
		for (String s: misspelledWords)
			uniqueMisspelledWords.add(s.toLowerCase().trim());
		
		int count = misspelledWords.size();
		misspelledWords.clear();
		return count;
	}
	
	public int numberOfUniqueMisspelledWords(){
		return uniqueMisspelledWords.size();
	}
	
	

	/**
	 * correct the misspelled words in the input String and return the result
	 */
	public String getCorrectedLine(String line){
		List<String> misSpelledWords = getMisspelledWords(line);

		for (String misSpelledWord : misSpelledWords){
			List<String> suggestions = getSuggestions(misSpelledWord);
			if (suggestions.size() == 0)
				continue;
			String bestSuggestion = suggestions.get(0);
			line = line.replace(misSpelledWord, bestSuggestion);
		}
		return line;
	}

	public String getCorrectedText(String line){

		line = line.replace("‘", "'").replace("’", "'").trim(); //bug fix - not that sophistcated, but works;-)

		StringBuilder builder = new StringBuilder();
		String[] tempWords = line.split("\\s++");
		for (String tempWord : tempWords){

			if(tempWord.matches("[.,!?']*\\w++[.,!?']*")) {

				if (!spellChecker.isCorrect(tempWord)){
					@SuppressWarnings("unchecked")
					List<Word> suggestions = spellChecker.getSuggestions(tempWord, 0);

					if (suggestions.size() > 0){
						builder.append(spellChecker.getSuggestions(tempWord, 0).get(0).toString());
					}

					else
						builder.append(tempWord);
				}

				else {
					builder.append(tempWord);
				}

				builder.append(" ");

			}
			
		}
		
		return builder.toString().trim();
	}


	public List<String> getSuggestions(String misspelledWord){

		@SuppressWarnings("unchecked")
		List<Word> su99esti0ns = spellChecker.getSuggestions(misspelledWord, 0);
		List<String> suggestions = new ArrayList<String>();
		for (Word suggestion : su99esti0ns){
			suggestions.add(suggestion.getWord());
		}

		return suggestions;
	}


	@Override
	public void spellingError(SpellCheckEvent event) {
		event.ignoreWord(true);
		misspelledWords.add(event.getInvalidWord());
	}

	public static void main(String[] args) {

		JazzySpellChecker jazz = new JazzySpellChecker();
		String s = "hey,,?";

		System.out.println(jazz.getCorrectedText(s));

	}


}




