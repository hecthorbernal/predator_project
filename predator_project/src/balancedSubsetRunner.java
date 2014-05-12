import preprocessing.dataParser;


public class balancedSubsetRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		dataParser myDataparser = new dataParser();
		
		myDataparser.generateBalacedSubset("data/rawFiles/W15_predator_raw.csv", "data/rawFiles/W15_non_predator_raw.csv","data/balancedSubsets/W15_20P_test.csv", 20);
		myDataparser.generateBalacedSubset("data/rawFiles/HP15_predator_raw.csv","data/rawFiles/W15_non_predator_raw.csv","data/balancedSubsets/HP15_20P_test.csv", 20);
	
	}

}
