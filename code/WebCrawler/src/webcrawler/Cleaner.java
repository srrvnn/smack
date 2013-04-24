
//package webcrawler

import java.io.*;
import java.util.*;

public class Cleaner{

	Cleaner() {}

	/* function 'deleteFilesfromFolder' : deletes all files (excluding any subfolders) in 'nameFolder' */
	/* param 'nameFolder' : folder, one level up, from which all files will be deleted.  */
	public void deleteFilesFromFolder(String nameFolder) {

		ArrayList<String> ListFiles = getFiles(new File("../"+nameFolder));

		for(String f : ListFiles){

			File d = new File(f);
			d.delete();
		}
	}

	private ArrayList<String> getFiles(final File folder)
    {        
        ArrayList<String> list_files = new ArrayList<String>();

        try{
        
	        for(final File tEntry : folder.listFiles()){
	            
	            if(tEntry.isDirectory()){
	                
	                list_files.addAll(getFiles(tEntry));
	            }
	            
	            else {  
	                
	               list_files.add(folder.toString()+"\\"+tEntry.getName());                
	            }
	        }

        }

        catch (Exception e){

        	System.out.println("Error, reported at 'Cleaner'.");
        }

        
        return list_files;        
    }
}