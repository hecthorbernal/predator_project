package preprocessing;
import preprocessing.dataParser;


public class balancedSubsetRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		dataParser myDataparser = new dataParser();
		
		myDataparser.generateBalacedSubset("data/rawFiles/L15_predator_raw.csv", "data/rawFiles/L15_non_predator_raw.csv","data/balancedSubsets/L15_20P.csv", 20);
		myDataparser.generateBalacedSubset("data/rawFiles/W15_predator_merged_raw_processed.csv","data/rawFiles/W15_non_predator_raw.csv","data/balancedSubsets/W15_20P.csv", 20);
		myDataparser.generateBalacedSubset("data/rawFiles/HP15_predator_over_15min_merged_raw_processed.csv","data/rawFiles/L15_non_predator_raw.csv","data/balancedSubsets/HP15_20P.csv", 20);
	
	}

}
