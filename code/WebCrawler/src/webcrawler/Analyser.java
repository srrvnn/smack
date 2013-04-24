// package webcrawler;

// The Analyzer class acts on the initiator news item. It's primary function run() makes an API call to Alchemy API to extract
// entities, keywords and text from the given URL. This information is written to corresponding files for further use. 

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;

import java.lang.Exception;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;



import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.util.Map;
import java.util.List;

import org.json.JSONArray;

public class Analyser {

	String seedURL;

	Analyser() {

		seedURL =  new String();
	}

	public void setURL(String s) {

		seedURL = s; 
	}

	public void run() throws FileNotFoundException, IOException, UnsupportedEncodingException, MalformedURLException, Exception {
		
		String url = "http://access.alchemyapi.com/calls/url/URLGetRankedKeywords";
		String rcharset = "UTF-8";
		String aurl = "value1";
		String apikey = "22801280f97e9d435dd9c70cec6c09e079036f95";
		String maxRetrieve = "10";
		String keywordExtractMode = "strict";

		BufferedReader b = new BufferedReader(new FileReader("../../seed.txt"));
		aurl = b.readLine();

		String query = String.format("url=%s&apikey=%s", 
			URLEncoder.encode(aurl, rcharset), 
			URLEncoder.encode(apikey, rcharset));

		URLConnection connection = new URL(url + "?" + query).openConnection();
// 		connection.setDoOutput(true);
		connection.setRequestProperty("Accept-Charset", rcharset);
// 		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + rcharset);
// 		try {
// 			connection.getOutputStream().write(query.getBytes(rcharset));
// 		}
// 		finally {
// 			connection.getOutputStream().close();
// 		}

		InputStream response = connection.getInputStream();		

		// for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
		// 	System.out.println(header.getKey() + "=" + header.getValue());
		// }

		String contentType = connection.getHeaderField("Content-Type");
		String charset = null;
		for (String param : contentType.replace(" ", "").split(";")) {
			if (param.startsWith("charset=")) {
				charset = param.split("=", 2)[1];
				break;
			}
		}

		BufferedWriter w = new BufferedWriter(new FileWriter("../../logs/keys.txt"));
		boolean keyfound = false;

		if (charset != null) {
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(response, charset));
				for (String line; (line = reader.readLine()) != null;) {

					// w.write(line); w.newLine();

					// line = line.replaceAll("[^\\p{L}\\p{N}<>/]", "");
					
					if(line.contains("<keyword>")){
						keyfound = true;
						continue;
					}

					if(keyfound){

						line = line.substring(line.indexOf("<text>")+6,line.indexOf("</text"));
						w.write(line); w.newLine();	
						keyfound = false;
						continue;
					}
            		
				}
			} finally {
				if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
				System.out.println("Content written to file: key.txt");								
				w.close();
			}
		} else {

    		System.out.println("It's likely binary content, use InputStream/OutputStream");
		}

	}

}