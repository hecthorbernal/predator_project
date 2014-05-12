package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class Statistics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

//		countBlankMessages("data/subsets/HP15_P.csv");
//		countBlankMessages("data/subsets/L15_P.csv");
//		countBlankMessages("data/subsets/L15_NP.csv");
//		countBlankMessages("data/subsets/W15_P.csv");
//		countBlankMessages("data/subsets/W15_NP.csv");
		
		System.out.println("**** RAWFILES HP15 Processed****");	
		counter("data/rawFiles/HP15_predator_over_15min_merged_raw_processed.csv");
		
		System.out.println("**** RAWFILES W15 Processed****");	
		counter("data/rawFiles/W15_predator_merged_raw_processed.csv");

		
		System.out.println("**** RAWFILES L15 ****");	
		counter("data/rawFiles/L15_non_predator_raw.csv");
		counter("data/rawFiles/L15_predator_raw.csv");
		
		System.out.println("**** RAWFILES W15 ****");		
		counter("data/rawFiles/W15_non_predator_raw.csv");
		counter("data/rawFiles/W15_predator_raw.csv");

		System.out.println("**** BALANCED SUBSETS ****");
		counter("data/balancedSubsets/HP15_20P.csv");
		counter("data/balancedSubsets/W15_20P.csv");

		System.out.println("**** BALANCED SUBSETS TEST ****");
		counter("data/balancedSubsets/HP15_20P_test.csv");
		counter("data/balancedSubsets/W15_20P_test.csv");

		

	}

	private static void counter(String file) {

		int countLines = 0;

		int countBlanks = 0;

		HashSet<String> np = new HashSet<>();
		HashSet<String> p = new HashSet<>();

		FileInputStream fis;

		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;

			while ((line = br.readLine()) != null) {

				countLines++;

				String[] tuple = line.split(",");
				if(tuple[0].equals("p")) p.add(tuple[1]);
				if(tuple[0].equals("np")) np.add(tuple[1]);
				if(tuple[2].equals("<<nl>")) countBlanks++;

			}

			br.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(file);
		System.out.println("Total Lines: " + countLines);
		System.out.println("Unique Predators: " + p.size());
		System.out.println("Unique NonPredators: " + np.size());
		System.out.println("Blank messages: " + countBlanks);
		System.out.println();
	}

}
