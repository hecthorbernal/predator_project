package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LineSelector {

	ArrayList<ArrayList<predatorLine>> inputSet;
	ArrayList<predatorLine> resultSet;
	String file;
	String processedOutputFile;
	String unprocessedOutputFile;
	int indexFixed;


	public LineSelector(String file) {

		this.inputSet = new ArrayList<ArrayList<predatorLine>>();
		this.resultSet = new ArrayList<predatorLine>();
		this.file = file;
		int indx = file.indexOf("_raw") + 4;
		
		this.processedOutputFile = file.substring(0, indx ) + "_processed.csv";
		this.unprocessedOutputFile = file.substring(0, indx ) + "_unprocessed.csv";
		
		indexFixed = 0;

	}

	private void readInputFile() {

		FileInputStream fis;

		try {

			fis = new FileInputStream(this.file); 

			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			predatorLine currentPredator = null;
			predatorLine nextPredatorLine = null;

			ArrayList<predatorLine> predatorLines = new ArrayList<predatorLine>();

			//read first line
			if((line = br.readLine()) != null) {
				currentPredator = new predatorLine(line);
				predatorLines.add(currentPredator);

				// read all lines
				while ((line = br.readLine()) != null) {

					nextPredatorLine = new predatorLine(line);

					if (nextPredatorLine.getID().equals(currentPredator.getID())) {

						// add to arraylist
						predatorLines.add(nextPredatorLine);

					} else {

						//add collected predatorlines to inputset
						ArrayList<predatorLine> newPredatorLines = predatorLines;
						inputSet.add(newPredatorLines);

						// move on to next set of lines
						predatorLines = new ArrayList<predatorLine>();
						predatorLines.add(nextPredatorLine);
						currentPredator = nextPredatorLine;

					}


				}

				br.close();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Imported " + inputSet.size() + "messages");

	}

	private void selectLinesHP15() {

		InputStreamReader r = new InputStreamReader(System.in); 
		BufferedReader selectorReader = new BufferedReader(r);
		boolean quit = false;

		for(ArrayList<predatorLine> p: inputSet) {

			//Display all lines and wait for selection

			if(!quit) {
				for (int i = 0; i < p.size(); i++) {

					System.out.println(i + " ("  + p.get(i).getTimestamp() + ") " + p.get(i).message);	

				}

				System.out.println("\n" + indexFixed + " predatorLines done - " + (inputSet.size() - indexFixed) + " more to process..." );
				System.out.println("Choose Line number and return - or 's' for skip or q for quit and save");

				try {

					int chosenIndex;
					boolean succes = false;


					while (!succes) {

						String selected = selectorReader.readLine();

						if (selected.matches("q")) {
							quit = true;
							saveAndQuit();
							break;
						}


						if (selected.matches("s")){
							indexFixed++;
							break;

						}

						if ( !selected.matches("[0-9]++") || Integer.parseInt(selected) > p.size()) {

							System.out.println("Please choose a valid linenumber or 's' for skipping line - 'q' for save and quit");

						} else {


							chosenIndex = Integer.parseInt(selected);

							// build new predatorLine from chosen
							predatorLine newPredatorLine = p.get(chosenIndex);

							int timeStamp = Integer.parseInt(newPredatorLine.timestamp);

							// concatenate messages from selected and 15 minutes ahead
							for(predatorLine pl: p) {

								int time = Integer.parseInt(pl.timestamp);

								if(time >= timeStamp && time <= timeStamp+15) {

									newPredatorLine.addToMessage(pl.message);
									// System.out.println(pl.timestamp);

								}


							}

							System.out.println("You picked " + chosenIndex + " " + p.get(chosenIndex).message);

							// Add predatorline to result Set
							resultSet.add(newPredatorLine);
							indexFixed++;
							succes = true;

						}

					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}

	private void selectLinesWP15() {

		InputStreamReader r = new InputStreamReader(System.in); 
		BufferedReader selectorReader = new BufferedReader(r);
		boolean quit = false;

		for(ArrayList<predatorLine> p: inputSet) {

			//Display all lines and wait for selection

			if(!quit) {

				for (int i = 0; i < p.size(); i++) {

					System.out.println(">>> " + i + " <<<\n " + p.get(i).message.replaceAll("<nl>", "\n") );

				}

				// System.out.println("\n" + indexFixed + " predatorLines done - " + (inputSet.size() - indexFixed) + " more to process..." );
				System.out.println("Choose Line number and return - or 's' for skip or q for quit and save");

				try {

					int chosenIndex;
					boolean succes = false;


					while (!succes) {

						String selected = selectorReader.readLine();

						if (selected.matches("q")) {
							quit = true;
							saveAndQuit();
							break;
						}


						if (selected.matches("s")) {
							indexFixed++;
							System.out.println("--------------------------------");
							System.out.println(indexFixed + " predatorLines done - " + (inputSet.size() - indexFixed) + " more to process...\n" );
							break;

						}

						if ( !selected.matches("[0-9]++") || Integer.parseInt(selected) > p.size()) {

							System.out.println("Please choose a valid linenumber or 's' for skipping line - 'q' for save and quit");

						} else {


							chosenIndex = Integer.parseInt(selected);

							// build new predatorLine from chosen
							predatorLine newPredatorLine = p.get(chosenIndex);

							System.out.println("--------------------------------");
							System.out.println(indexFixed + " predatorLines done - " + (inputSet.size() - indexFixed) + " more to process...\n" );

							// Add predatorline to result Set
							resultSet.add(newPredatorLine);
							indexFixed++;
							succes = true;

						}

					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}

	}
	
	private class predatorLine {

		public String senderID;
		public String timestamp;
		public String conversationID;
		public String message;

		public predatorLine(String s){

			String[] fields = s.split(",");
			this.senderID = fields[1];
			this.timestamp = fields[3];
			this.conversationID =  fields[4];
			this.message =  fields[2];

		}


		public String getID(){

			return this.senderID;

		}

		public String getTimestamp() {

			return this.timestamp;

		}

		public void addToMessage(String s) {

			this.message += s;

		}


	}
	
	
	// the method creates the x files with the totalNumberOfPredatorLine/x, here 686/3
	
	private void devide_predator_files(String subsetPath, int low, int high) throws IOException{
		FileInputStream fis;
		BufferedWriter writer = null;
            //create a temporary file
            File subset = new File(subsetPath);

            // This will output the full path where the file will be written to...
            System.out.println(subset.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(subset));
		
		try {
			fis = new FileInputStream(this.file);

			// Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			predatorLine currentPredator = null;
			predatorLine nextPredatorLine = null;

			ArrayList<predatorLine> predatorLines = new ArrayList<predatorLine>();
			int lineCounter = 1;
			// read first line
			if ((line = br.readLine()) != null) {
				currentPredator = new predatorLine(line);
				predatorLines.add(currentPredator);
				int count = 1;
				// read all lines
				while ((line = br.readLine()) != null && count <high ) {
					lineCounter++;
					nextPredatorLine = new predatorLine(line);
					if (nextPredatorLine.getID().equals(currentPredator.getID())) {
						if(low<=count){
							writer.write(line + "\n");
						}
					} else {
						// move on to next set of lines
						currentPredator = nextPredatorLine;
						writer.write(line + "\n");
						count++;
					}
				}
				br.close();
				System.out.println("total number of lines prossesed: " + lineCounter);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void saveAndQuit() {

		// Save results
		generateRawCsvFile(resultSet, processedOutputFile, true);

		//save unprocessed lines in new file
		ArrayList<predatorLine> unprocessed = new ArrayList<>();
		for(int i = indexFixed; i < inputSet.size(); i++) {	
			for(predatorLine pl: inputSet.get(i))			
				unprocessed.add(pl);	
		}

		generateRawCsvFile(unprocessed, unprocessedOutputFile, false);

	}

	private static void generateRawCsvFile(List<predatorLine> subset, String sFileName, boolean add)
	{
		try

		{
			FileWriter writer = new FileWriter(sFileName, add);

			// add lines
			for(predatorLine pLine: subset) {

				writer.append("p"); //predator label
				writer.append(',');
				writer.append(pLine.senderID);
				writer.append(',');

				// add message to line
				String csvMessage = pLine.message;
				csvMessage = csvMessage.replace(",", " ");
				csvMessage = csvMessage.replace("\n", "");

				writer.append(csvMessage);
				writer.append(',');
				writer.append(pLine.timestamp);
				writer.append(',');
				writer.append(pLine.conversationID);
				writer.append(',');
				writer.append('\n');

			}

			writer.flush();
			writer.close();
		}

		catch(IOException e)

		{
			e.printStackTrace();
		}

		System.out.println("Exported csv file: " + sFileName);

	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		
		/*
		 * Select lines in HP15
		 */
		LineSelector selector = new LineSelector("data/rawFiles/HP15_predator_over_15min_raw.csv.txt");
		selector.devide_predator_files("data/rawFiles/jacob_HP15_predator_over_15min_raw.csv", 458, 689);
//		selector.readInputFile();	
//		selector.selectLinesHP15();

		/*
		 * Select lines in W15
		 */

//		LineSelector selector2 = new LineSelector("data/rawFiles/W15_predator_raw.csv");
//		selector2.readInputFile();	
//		selector2.selectLinesWP15();


	}

}
