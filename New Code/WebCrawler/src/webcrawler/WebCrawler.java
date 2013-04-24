/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcrawler;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Object;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import edu.smu.tspell.wordnet.*; 
//import com.gistlabs.mechanize.MechanizeAgent;
//import com.gistlabs.mechanize.document.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author NEO
 */
public class WebCrawler {

    /**
     * @param args the command line arguments
     */
    static String summaryFile = "../summary.txt";
    static List<String> listOfLinks;
    static List<String> listOfKeywords;
    static String savePath;
    static String query;
    static Date date;
    static int maxTrials = 3;
    /*Takes the input file name cotaining the keywords and number of days to check and the date as args
     * args[0] = keyword file name
     * args[1] = year
     * args[2] = month
     * args[3] = day
     * args[4] = Num of days before and after the given date
     */
    public static void getSyns()
    {
        NounSynset nounSynset;
        NounSynset[] hyponyms;

        WordNetDatabase database = WordNetDatabase.getFileInstance();
        Synset[] synsets = database.getSynsets("love", SynsetType.NOUN);
        for (int i = 0; i < synsets.length; i++) {
            nounSynset = (NounSynset)(synsets[i]);
            hyponyms = nounSynset.getHyponyms();
            System.out.println(nounSynset.getWordForms()[0] +
                ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
            for(int j = 0; j < hyponyms.length; j++)
            {
                System.out.println(hyponyms[j].toString());
            }
            for (String word: nounSynset.getWordForms())
            {
                System.out.println(word);
            }
        } 
    }
    public static void main(String[] args) {
        
        System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict");
        getSyns();
        CleanHtml cleaner = new CleanHtml();
        cleaner.cleanDirectory("files");
        SentenceRanker ranker = new SentenceRanker();
        Map<String, String> dicFileToSummary = ranker.startRanker(cleaner.getFileList());
        StringBuilder summary = new StringBuilder();
        List<String> summaries = new ArrayList<String>();
        List<Calendar> dates = new ArrayList<Calendar>();
        for(String file:dicFileToSummary.keySet())
        {
            String text = String.format("%s: %s\r\n\r\n", file, dicFileToSummary.get(file));
            //summaries.add(text);
            String [] folders= file.split("\\\\");
            String dateFolder = folders[folders.length - 2];
            dateFolder = dateFolder.replace("stories","");
            String [] dateFields = dateFolder.split("-");
            int year = Integer.parseInt(dateFields[0]);
            int month = Integer.parseInt(dateFields[1]);
            int day = Integer.parseInt(dateFields[2]);
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);
            //summaries.add(text);
            int i=-1;
            for(i = dates.size()-1; i >= 0; i--)
            {
                if(date.compareTo(dates.get(i))>= 0)
                {
                    break;
                }
            }
            i++;
            summaries.add(i, text);
            dates.add(i, date);
            
        }
        for(int j=0; j<summaries.size(); j++)
        {
            Calendar cal = dates.get(j);
            summary.append(String.format("%d-%d-%d: %s", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), summaries.get(j)).replace("''", "\"").replace("``", "\""));
        }
        FileHandler handler = new FileHandler(summaryFile);
        handler.write(summary.toString());
        
        // TODO code application logic here
//        listOfLinks = new ArrayList<String>();
//        listOfKeywords = new ArrayList<String>();
//        String keywordFile = args[0];
//        int year = Integer.parseInt(args[1]);
//        int month = Integer.parseInt(args[2]);
//        int day = Integer.parseInt(args[3]);
//        int count = Integer.parseInt(args[4]);
//        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Calendar cal = Calendar.getInstance();
//        cal.set(year, month, day);
//        cal.add(Calendar.DAY_OF_MONTH, -count);
//        
//        try 
//        {
//            BufferedReader bufferRead = new BufferedReader(new FileReader(keywordFile));
//            String read = bufferRead.readLine();
//            while(read!=null)
//            {
//                read = bufferRead.readLine();
//                if(read!=null && !read.trim().isEmpty())
//                    listOfKeywords.add(read);
//            }
//        } 
//        catch (FileNotFoundException ex) 
//        {
//            Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        for(int i=0; i<2*count; i++)
//        {
//            year = cal.get(Calendar.YEAR);
//            month = cal.get(Calendar.MONTH);
//            day = cal.get(Calendar.DATE);
//            query = "http://www.hindu.com/thehindu/%04d/%02d/%02d/";
//            query = String.format(query,year,month,day);
//            savePath = String.format("c:\\NLP\\%04d-%02d-%02d", year, month, day);
//            
//            //Document page = agent.get(query);
//            Document doc = null;
//            doc = getPage(doc, query);
//            if(doc!=null)
//            {
//                inspectAllLinks(doc);
//                Elements links = doc.getElementsByTag("a");
//                for(Element link : links)
//                {
//                    String linkHref = link.attr("href");
//                    String linkText = link.text();
//                    if(linkHref.contains("hdline.htm"))
//                    {
//                        Document insideDoc = null;
//                        insideDoc = getPage(insideDoc, query+linkHref);
//                        
//                        inspectAllLinks(insideDoc);
//                    }
//                }
//            }
//            
//            cal.add(Calendar.DAY_OF_MONTH, 1);
//        }
//        for(String link:listOfLinks)
//        {
//            System.out.println(link);
//        }
        
        
    }
    public static void inspectAllLinks(Document doc)
    {
        
        Elements links = doc.getElementsByTag("a");
        for (Element link : links) 
        {
            String linkHref = link.attr("href");
            String linkText = link.text();
            Elements siblings = link.siblingElements();
            boolean linkChosen = false;
            if(linkHref.contains(("stories")))
            {
                Document insideDoc = null;
                insideDoc = getPage(insideDoc, query + linkHref);
                
                if(insideDoc!=null)
                {
                    String docText = insideDoc.text();
                    for(String keyword: listOfKeywords)
                    {
                        if(docText.toLowerCase().contains(keyword.toLowerCase()))
                        {

                            if(!listOfLinks.contains(query+linkHref))
                            {
                                listOfLinks.add(query + linkHref);
                                String filePath = savePath + linkHref.replace("/", "\\");
                                File file = new File(filePath);
                                
                                FileWriter fw = null;
                                try 
                                {
                                    if(!file.exists())
                                    {
                                        (new File(filePath.replace(file.getName(), "") )).mkdirs();
                                        file.createNewFile();
                                    }
                                    fw = new FileWriter(file.getAbsoluteFile());
                                    BufferedWriter bw = new BufferedWriter(fw);
                                    insideDoc.outputSettings().prettyPrint(true);
                                    bw.write(insideDoc.html());
                                    bw.close();
                                } 
                                catch (IOException ex) 
                                {
                                    Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                            }
                            linkChosen = true;
                            
                            break;
                        }
                    }
                }
                
            }
        }
    }

    private static Document getPage(Document doc, String webQuery)
    {
        for(int tryCount = 0; tryCount < maxTrials; tryCount++)
        {
            try 
            {
                doc = Jsoup.connect(webQuery).get();
                if(doc!=null)
                    break;
                else
                    Thread.sleep(1000);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
         
            }
            catch (Exception ex) 
            {
                Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return doc;
    }
    
}
