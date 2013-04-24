import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.lang.Exception;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Statser {

	String tfile; 
	String rfile;

	float precision, recall, accuracy, fmeasure; 
	int noCorrectFound, noTotalFound, noTotalExist;
	
	Statser(){

		tfile = new String();
		rfile = new String();

		noCorrectFound = 0;
		noTotalFound = 0;
		noTotalExist = 0;
	}

	public void setTruthFile(String s){

		tfile = s; 
	}

	public void setResultsFile(String s){

		rfile = s; 
	}

	public void run() throws FileNotFoundException, IOException, Exception {

		BufferedReader tr = new BufferedReader(new FileReader("../../logs/"+tfile));
		BufferedReader rr = new BufferedReader(new FileReader("../../logs/"+rfile));

		BufferedWriter w = new BufferedWriter(new FileWriter("../../logs/statslogs.txt"));

		boolean countTotalExist = false;		

		String buffer1, buffer2; 

		while((buffer1 = rr.readLine()) != null){

			++noTotalFound;

			while((buffer2 = tr.readLine()) != null){

				if(!countTotalExist) ++noTotalExist;
				
				if(buffer1.trim().equals(buffer2.trim())){

					++noCorrectFound;
				}
				else 
				{

					w.write(buffer1); w.newLine();
					w.write(buffer2); w.newLine();

					w.newLine();
				}
			}

			tr.close();
			tr = new BufferedReader(new FileReader("../../logs/"+tfile));

			countTotalExist = true;

		}

		w.close(); tr.close();	rr.close();

		precision = (float) (noCorrectFound * 100 / noTotalFound); 
		recall = (float) (noCorrectFound * 100 / noTotalExist);

	}

	public void printStats(){

		System.out.println("No Positive Found: "+noCorrectFound);
		System.out.println("No Total Found: "+noTotalFound);
		System.out.println("No Total Exist: "+noTotalExist);

		System.out.println("Precision : "+precision);
		System.out.println("Recall : "+recall);

		// System.out.println("Accuracy : "+accuracy);
		// System.out.println("f-measure: "+fmeasure);

	}


}