package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PredatorIdentifier {
	String file;
	HashMap<String, Integer> predatorList;
	public PredatorIdentifier(String file) {
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
				predatorList.put(line, 1);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String isAPredator(String id){
		if(predatorList.containsKey(id)){
				return "p";
			}
		return "np";
	}


}
