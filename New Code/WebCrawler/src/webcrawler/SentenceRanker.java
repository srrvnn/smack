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
public class SentenceRanker {
    
    Map<String, List<String>> dicDocumentsToSentenceList;
    Map<String, Integer> wordRanks;
    Map<String, List<String>> dicWordToDocs;
    SentenceFilter sentenceFilter;
    public Map<String, String> dicFileToSummary;
    int totalFileCount;
    int maxLinesPerSummary;
    public SentenceRanker() {
        dicDocumentsToSentenceList = new HashMap<String, List<String>>();
        wordRanks = new HashMap<String, Integer>();
        dicWordToDocs = new HashMap<String, List<String>>();
        dicFileToSummary = new HashMap<String, String>();
        totalFileCount = 0;
        maxLinesPerSummary = 5;
    }
    
    //Get Sentences from text
    public List<String> getSentences(String text)
    {
        int start = 0, end = 0, lengthOfText = text.length();
        List<String> sentenceList = new ArrayList<String>();
        end = text.indexOf(".");
        while(end != -1)
        {
            sentenceList.add(text.substring(start, end));
            text = text.substring(end+1);
            end = text.indexOf(".");
        }
        return sentenceList;
        
    }
    
    //Init the word counts
    private void initialiseWordRanks(String text, String fileName)
    {
        String [] words = text.split(" ");
        if(words.length > 0)
        {
            for(String word: words)
            {
                if(word.length()==0)
                {
                    continue;
                }
                String searchWord = word.toLowerCase();
                Integer value = wordRanks.get(searchWord);
                if(value == null)
                {
                    wordRanks.put(searchWord,1);
                }
                else
                {
                    wordRanks.put(searchWord, ++value);
                }
                List<String> filesContainingWord = dicWordToDocs.get(word);
                if(filesContainingWord == null)
                {
                    List<String> fileList = new LinkedList<String>();
                    fileList.add(fileName);
                    dicWordToDocs.put(word, fileList);
                }
                else
                {
                    List<String> fileList = dicWordToDocs.get(word);
                    fileList.add(fileName);
                }
            }
        }
    }
    
    //Add count of words to the dictionary word rank
    private void rankFileWords(String fileName)
    {
        FileHandler file = new FileHandler(fileName);
        String fileText = file.read();
        List<String> listSentences = sentenceFilter.getSentences(fileText);
        dicDocumentsToSentenceList.put(fileName, listSentences);
        for(String sentence: listSentences)
        {
            String sentenceWithoutStopWords = removeStopWords(sentence);
            initialiseWordRanks(sentenceWithoutStopWords, fileName);
        }
    }
    //Remove stopwords
    public String removeStopWords(String text)
    {
        try {
            int count = 0;

            System.out.println("Cleaning page: "+count++);

            StringBuilder sb = new StringBuilder();

            Analyzer analyzer = new StopAnalyzer(Version.LUCENE_36);

            TokenStream tokens = analyzer.tokenStream(null,new StringReader(text));						
            tokens = new PorterStemFilter(tokens);	

            CharTermAttribute charTermAttribute = tokens.addAttribute(CharTermAttribute.class);
            while (tokens.incrementToken()) {
                sb.append(" "+charTermAttribute.toString());			    
            }
            text = sb.toString();
            text = text.replaceAll("[^\\p{L}\\p{N}]", " ");
            text = text.replaceAll("(\\t|\\r)+", " ");			
            
        } catch (IOException ex) {
            Logger.getLogger(SentenceRanker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }
    
    //Returns a map of file to summary
    public Map<String, String> startRanker(List<String> files)
    {
        sentenceFilter = new SentenceFilter();
        sentenceFilter.initializeModel();
        totalFileCount = files.size();
        for(String fileName: files)
        {
            rankFileWords(fileName);
            
        }
        for(String fileKey: dicDocumentsToSentenceList.keySet())
        {
            List<String> sentences = dicDocumentsToSentenceList.get(fileKey);
            if(sentences == null)
                 continue;
            Map<String,Integer> localWordCount = new HashMap<String, Integer>();
            if(sentences != null)
            {
                for(String sentence: sentences)
                {
                    String text = removeStopWords(sentence);
                    
                    String [] words = text.split(" ");
                    if(words.length > 0)
                    {
                        for(String word: words)
                        {
                            if(word.length() == 0)
                            {
                                continue;
                            }
                            String searchWord = word.toLowerCase();
                            Integer value = localWordCount.get(searchWord);
                            if(value == null)
                            {
                                localWordCount.put(searchWord,1);
                            }
                            else
                            {
                                localWordCount.put(searchWord, ++value);
                            }
                        }
                    }
                    
                }
            }
            if(localWordCount.size()>0)
            {
                String summary = getSummaryUsingTFIDF(fileKey, localWordCount, dicWordToDocs);
                dicFileToSummary.put(fileKey, summary);
            }
            
        }
        return dicFileToSummary;
        
    }
    //Takes the file name, local map of word to its count, global map of word to the documents containing it
    public String getSummaryUsingTFIDF(String fileName, Map<String, Integer> local, Map<String, List<String>> globalWordToDocs)
    {
        double maxLocal = 0;
        Map<String, Double> dicWordScore = new HashMap<String, Double>();
        for(String word: local.keySet())
        {
            double localCount = local.get(word);
            if(maxLocal < localCount)
            {
                maxLocal = localCount;
            }
        }
        for(String word: local.keySet())
        {
            if(local.get(word) == null || globalWordToDocs.get(word)==null)
                continue;
            int localCount = local.get(word);
            
            double tf = (double)(localCount / (1 + maxLocal));
            double idf = ((double)(totalFileCount/(1 + (double)globalWordToDocs.get(word).size())));
            if(idf > 0)
            {
                idf = Math.log(idf);
            }
            double tfidf = tf * idf;
            dicWordScore.put(word, tfidf);
        }
        /*SUMMARY*/
        String summary = null;
        double maxtfidf = 0;
        for(String sentence: dicDocumentsToSentenceList.get(fileName))
        {
            String sentWithoutStopWords = removeStopWords(sentence);
            String [] words = sentWithoutStopWords.split(" ");
            double tfidfScore = 0;
            for(String word: words)
            {
                if(dicWordScore.get(word)!=null)
                    tfidfScore = tfidfScore + dicWordScore.get(word);
            }
            if(words.length>0)
            {
                tfidfScore = tfidfScore/words.length;
            }
            if(tfidfScore > maxtfidf)
            {
                maxtfidf = tfidfScore;
                summary = sentence;
            }
        }
        return summary;
        
    }
}
