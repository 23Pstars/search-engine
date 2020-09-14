import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.Website;


public class TestAgent {
	
	public static void main( String args[] ) {
		
		String keyword = "", url = "", titleText = "", descriptionText = "", keywordsText = "";
		Elements links = null;
		
		keyword = args[0];
		url = args[1];

		Website w = new Website();
		try {
				
			Document doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
			
			// title website
			titleText =  doc.select("title").text();
			
			// description meta website
			descriptionText = doc.select("meta[name=description]").attr("content");
			
			// keywords meta website
			keywordsText = doc.select("meta[name=keywords]").attr("content");
			
			// ekstrak links
			links = doc.select("a[href]");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_e("Kata kunci : " + keyword);
		_e("URL : " + url);
		_e("");
		_e("Title halaman : " + titleText);
		_e("Description halaman : " + descriptionText);
		_e("Keywords halaman : " + keywordsText);
		_e("Link yang ditemukan ("+links.size()+") :");
		for (Element link : links) {
			_e(" - " + link.attr("abs:href"));
		}
		
	}
	
	static void _e(String msg) {
    	System.out.println(msg);
    }

}
