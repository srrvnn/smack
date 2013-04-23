/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcrawler;
import java.io.*;
import java.io.File;
import java.util.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.Jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element.*;
import org.jsoup.select.Elements.*;
import org.apache.lucene.util.*;
import org.apache.lucene.analysis.*;

import org.apache.lucene.analysis.tokenattributes.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


/**
 *
 * @author NEO
 */
public class CleanHtml {
    
    private String baseDirectory;
    private List<String> fileNames;

    public CleanHtml() {
        
        baseDirectory = "CleanFiles";
        fileNames = new ArrayList<String>();
    }
    public void setBaseDir(String base)
    {
        baseDirectory = base;
        
    }
    
    public List<String> getFileList()
    {
        return fileNames;
    }
    
    public void cleanDirectory(String pathName)
    {
        File file = new File(pathName);
        
        if(file.isDirectory())
        {
            for(File fileEntry: file.listFiles())
            {
                cleanDirectory(fileEntry.getPath());
            }
            
        }
        else if(file.isFile())
        {
            cleanFile(file.getPath());
        }
        else
        {
            //File doesn't exist
        }
        
    }

    private void cleanFile(String path) {
        try {
            File file = new File(path);
            String savePath = baseDirectory + "\\" + file.getParent() + "\\" + file.getName() +".txt";
            File cleanFile = new File(savePath);
            BufferedReader bufferRead = new BufferedReader(new FileReader(path));
            char[] bufferChars = new char[(int)file.length()];
            
            int count = bufferRead.read(bufferChars);
            String buffer = String.copyValueOf(bufferChars);
            Document doc = Jsoup.parse(buffer.toString());
            /* Parsing specific to Hindu */
            Elements trs = doc.getElementsByTag("tr");
            Element tr =  trs.get(3);
            /*Elements story = tr.getElementsContainingText("story begins");*/
            Element searchElement = tr.child(0);
            /*if(story.size() == 1)
            {
                searchElement = story.get(1).firstElementSibling();
            }*/
            doc.outputSettings().prettyPrint(true);
            String text = tr.text().replace("Send this article to Friends by E-Mail","");
            if(!cleanFile.exists())
            {
                (new File(savePath.replace(cleanFile.getName(), "") )).mkdirs();
                cleanFile.createNewFile();
            }
            FileWriter fw = new FileWriter(cleanFile);
            BufferedWriter bw = new BufferedWriter(fw);
            fileNames.add(savePath);
            bw.write(text);
            bw.close();
           
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CleanHtml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CleanHtml.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

    }
    
}
