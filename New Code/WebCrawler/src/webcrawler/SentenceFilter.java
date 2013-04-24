/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcrawler;

/**
 *
 * @author NEO
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceFilter {

    SentenceDetectorME sentenceDetector;
    public void initializeModel()
    {
        
        InputStream modelIn = null;
        try {
            modelIn = new FileInputStream("en-sent.bin");  
            SentenceModel model = null;
            try {
               model = new SentenceModel(modelIn);  
            }
            catch (IOException e) {
              e.printStackTrace();
            }
            sentenceDetector = new SentenceDetectorME(model);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SentenceFilter.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
    @SuppressWarnings("empty-statement")
    public List<String> getSentences(String text)
    {
        List<String> sentencesList = new ArrayList<>();
        try
        {
        String sentences[] = sentenceDetector.sentDetect(text.trim());
        for(String str : sentences)
            sentencesList.add(str);
        }catch(Exception e)
        {
            System.out.printf("Error");
        }
        return sentencesList;
    }
}
