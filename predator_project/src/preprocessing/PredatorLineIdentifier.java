package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PredatorLineIdentifier {
	String file;
	HashMap<String, Integer> predatorList;
	public PredatorLineIdentifier(String file) {
		this.file = file;
		predatorList = new HashMap<>();
		FileInputStream fis;
		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replace("\\n", "").replace("\\r", "");
				line = line.toLowerCase();
				String[] tokens = line.split("\\s+");
				predatorList.put(tokens[0], 1);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean isAPredatorLine(String id){
		if(predatorList.containsKey(id)){
				return true;
			}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		String test = "f38606808ff2af1f258d158b23f8867a        61";
		String[] tokens = test.split("\\s+");
		System.out.println(tokens[0] + " -> result");


	}

}
