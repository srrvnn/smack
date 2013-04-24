
// package webcrawler;

import java.lang.Exception;
import java.io.IOException;
import java.io.FileNotFoundException;

public class index {

	public static void main(String[] args) throws FileNotFoundException, IOException, Exception {

		Cleaner ocleaner = new Cleaner();
		ocleaner.deleteFilesFromFolder("../files");
		// // ocleaner.deleteFilesFromFolder("../logs");

		// Analyser oanalyser = new Analyser(); 
		// oanalyser.run();

		WebCrawler ocrawler = new WebCrawler();

		Statser ostatster = new Statser();

		ostatster.setTruthFile("truth.txt");
		ostatster.setResultsFile("results.txt");

		ostatster.run();		
		ostatster.printStats();
	}
}