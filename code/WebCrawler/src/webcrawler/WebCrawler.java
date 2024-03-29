
//package webcrawler;

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

    List<String> listOfLinks;
    List<String> listOfKeywords;
    String savePath;
    String query;
    Date date;
    int maxTrials = 3; 

    WebCrawler() throws FileNotFoundException, IOException, Exception {

        BufferedReader br = new BufferedReader(new FileReader("../../logs/config.txt"));

        String[] arguments = new String[5];

        for(int i = 0; i < 5; i++)
            arguments[i] = br.readLine();
    
        listOfLinks = new ArrayList<String>();
        listOfKeywords = new ArrayList<String>();
        String keywordFile = arguments[0];
        int year = Integer.parseInt(arguments[1]);
        int month = Integer.parseInt(arguments[2]);
        int day = Integer.parseInt(arguments[3]);
        int count = Integer.parseInt(arguments[4]);
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        // cal.add(Calendar.DAY_OF_MONTH, -count);
        
        try 
        {
            BufferedReader bufferRead = new BufferedReader(new FileReader("../../logs/"+keywordFile));
            String read = bufferRead.readLine();
            while(read!=null)
            {
                read = bufferRead.readLine();
                if(read!=null && !read.trim().isEmpty())
                    listOfKeywords.add(read);
            }
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        int numberArticles = 0;

        for(int i=0; i<count; i++)
        {   

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DATE);
            query = "http://www.hindu.com/thehindu/%04d/%02d/%02d/";
            query = String.format(query,year,month,day);
            savePath = String.format("..\\..\\files\\%04d-%02d-%02d", year, month, day);
            
            System.out.print(String.format("Looking into : /%04d/%02d/%02d/",year,month,day));

            //Document page = agent.get(query);
            Document doc = null;
            doc = getPage(doc, query);
            if(doc!=null)
            {
                inspectAllLinks(doc);
                Elements links = doc.getElementsByTag("a");
                for(Element link : links)
                {
                    String linkHref = link.attr("href");
                    String linkText = link.text();
                    if(linkHref.contains("hdline.htm"))
                    {
                        Document insideDoc = null;
                        insideDoc = getPage(insideDoc, query+linkHref);
                        
                        inspectAllLinks(insideDoc);
                    }
                }
            }
            
            cal.add(Calendar.DAY_OF_MONTH, 1);

            System.out.println(" " + (listOfLinks.size() - numberArticles) + "articles found.");
            numberArticles = listOfLinks.size();
        }

        BufferedWriter w = new BufferedWriter(new FileWriter("../../logs/results.txt"));

        for(String link:listOfLinks)
        {
            w.write(link.toString()); w.newLine();
        }

        w.close();

        System.out.println("===========================================================");
        System.out.println("Totally " + listOfLinks.size() + " articles found over " + Integer.parseInt(arguments[4]) + " days.");
        
        
    }


    public void inspectAllLinks(Document doc)
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

    private Document getPage(Document doc, String webQuery)
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
