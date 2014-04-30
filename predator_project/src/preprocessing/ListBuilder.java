package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ListBuilder {

	String[] attributeList = new String[7];

	public ListBuilder() {

		attributeList[0] = "word";
		attributeList[1] = "matchWholeWord";
		attributeList[2] = "isYoutube";
		attributeList[3] = "isUsername";
		attributeList[4] = "isAlert";
		attributeList[5] = "isBlackList";
		attributeList[6] = "isExactFiltering";

	}

	public void buildList() {

		String file = "data/en_us.blacklist.csv";
		int numberOfFields = 7;

		ArrayList<ArrayList<String>> newLists = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < numberOfFields; i++) {

			newLists.add(i, new ArrayList<String>());

		}

		FileInputStream fis;
		
		try {
			fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			int i = 0;

			// skip first line
			line = br.readLine();

			while ((line = br.readLine()) != null) {


				//Split line and build when column true

				String[] split = line.split(",");

				for(int j=1; j < numberOfFields ; j++) {

					if(split[j].equals("1")) {

						// add text to list
						newLists.get(j-1).add(split[0]);

					}	

				}

			}


			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 1; i < numberOfFields; i++) {

			// save list to file

			String filename = "data/blacklists/en_us." + this.attributeList[i] + ".txt";
			saveListToFile(newLists.get(i-1), filename);	

		}

	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ListBuilder myList = new ListBuilder();

		myList.buildList();

	}

	private static void saveListToFile(List<String> list, String sFileName)
	{
		try

		{
			FileWriter writer = new FileWriter(sFileName);

			// add lines
			for(String s: list) {

				writer.append(s);
				writer.append('\n');
			}

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
