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
public class FileHandler {
    
    private String fileName;
    FileHandler(String name){
        fileName = name;
    }
    public String read()
    {
        File file = new File(fileName);
        String buffer = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            char[] bufferChars = new char[(int)file.length()];
            
            int count = reader.read(bufferChars);
            buffer = String.copyValueOf(bufferChars);
            
            
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(SentenceRanker.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) {
            Logger.getLogger(SentenceRanker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer; 
    }
    public boolean write(String text)
    {
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();
            return true;
        } catch (IOException ex) {
            
        } 
        return false;
    }
}
