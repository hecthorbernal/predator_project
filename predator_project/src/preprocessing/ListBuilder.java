package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ListBuilder {

	/**
	 * Builds list from en_us.blacklists.txt
	 */
	public static void main(String[] args) {


		String file = "data/en_us.blacklist.csv";
		FileInputStream fis;

		String[] attributeList;
		ArrayList<ArrayList<String>> newLists;

		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;

			// build attributelist from first line
			line = br.readLine();
			attributeList = line.split(",");

			// prepare lists
			newLists = new ArrayList<ArrayList<String>>();

			for (int i = 0; i < attributeList.length; i++)
				newLists.add(i, new ArrayList<String>());

			// build lists
			while ((line = br.readLine()) != null) {

				//Split line and build when column true
				String[] split = line.split(",");

				for(int j=1; j < attributeList.length; j++) 
					if(split[j].equals("1")) 
						newLists.get(j-1).add(split[0]);
			}


			br.close();

			for (int i = 1; i < attributeList.length; i++) {

				// save list to file
				String filename = "data/blacklists/en_us." + attributeList[i] + ".txt";	
				saveListToFile(newLists.get(i-1), filename);	

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveListToFile(List<String> list, String sFileName){

		try {

			FileWriter writer = new FileWriter(sFileName);

			// add lines
			for(String s: list)
				writer.append(s + "\n");

			writer.flush();
			writer.close();
		}

		catch(IOException e)

		{
			e.printStackTrace();
		}

		System.out.println("Exported file: " + sFileName);

	}


}
