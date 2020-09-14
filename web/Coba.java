import handler.Database;
import handler.WebPage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.AgentParams;
import model.Settings;
import model.Website;


public class Coba {
	public static void main(String[] args) {
		
		/** /
		List<String> links = html.extractLinks("http://www.google.com");
        for (String link : links){
            System.out.println(link);
        }
        /** /
		String url = "http://zaf.web.id/";
		String keywords = "zaf";
        html.searchOnPage(url, keywords);
        /** /
		System.out.print("Compiling HelloAgent... ");
		new HelloAgent();
		System.out.print("done!");
		/** /
		
		List<HDModel> hds = new DatabaseHandler().getHostDirectory();
		/** /
		for (HostDirectory hd : new Database().getHostDirectory()) {
			System.out.println(hd.getAddress());
		}
		/** /
		WebPage wp = new WebPage();
		Website w = wp.searchOnPage("http://localhost/projects/zaf", "ahmad");
		System.out.println(wp.linkDeepLevel("http://zaf.web.id", "http://zaf.web.i"));
		/** /
		String url = "http://zaf.web.id";
		String url2 = "http://zaf.web.id/ahmad";
		String url3 = "http://zaf.web.id/u/anu";
		String url4 = "http://localhost/references/codex-wordpress/codex.wordpress.org/";
		System.out.println(url4.substring(0, url4.indexOf('/', 7)));
		if (url2.contains(url)) {
			int urlSlash = 0, url2Slash = 0, url3Slash = 0;
			for (int i = 0; i<url.length(); i++) {
				urlSlash += url.charAt(i) == '/' ? 1 : 0;
			}
			for (int i = 0; i<url2.length(); i++) {
				url2Slash += url2.charAt(i) == '/' ? 1 : 0;
			}
			for (int i = 0; i<url3.length(); i++) {
				url3Slash += url3.charAt(i) == '/' ? 1 : 0;
			}
			System.out.println("url : "+urlSlash+", url2 : "+url2Slash+", url3 : "+url3Slash);
		}
		/** /
		AgentParams ap = new AgentParams();
		WebPage wp = new WebPage();
		ap.setKeyword("wordpress");
		//ap.setUrl("http://wahanagiliocean.com/");
		ap.setUrl("http://localhost/projects/zaf/");
		Website w = wp.searchOnPage(ap);
		
		/** /
		for (String hf : w.getHostFounded()) {
			System.out.println(hf);
		}
		/**/
		
		/** /
		for (String lf : w.getLinkFounded()) {
			System.out.println(lf);
		}
		/** /
		Database db = new Database();
		List<String> siteBlocked = db.getSiteBlocked();
		for (String site : siteBlocked) {
			System.out.println(site);
		}
		/** /
		Settings s = db.getSettings();
		System.out.println("max deep : "+s.getMaxDeep());
		System.out.println("max links received : "+s.getMaxLinksReceived());
		System.out.println("max links crawled : "+s.getMaxLinksCrawled());
		System.out.println("host directory per process : "+s.getHostDirectoryPerProcess());
		/** /
		String desc = "Ahmad Zafrullah Mardiansyah, Tapon Timur, Bilebante, Pringgarata, Lombok Tengah, Indonesia";
		String keyword = "zaf tapon";
		desc = desc.toLowerCase();
		keyword = keyword.toLowerCase();
		System.out.println(keyword);
		keyword = keyword.replaceAll("[^a-zA-Z0-9]", " ");
		System.out.println(keyword);
		keyword = keyword.replaceAll("\\s+", " ");
		System.out.println(keyword);
		String[] keywordArray = keyword.split(" ");
		float founded = 0;
		for (String x : keywordArray) {
			System.out.println("search the "+x);
			System.out.println("desc : "+desc);
			if (desc.matches("(.*)" + x + "(.*)")) {
				System.out.println("matches");
				founded+=1;
			}
		}
		float percent = (founded/keywordArray.length)*100;
		System.out.println("Presentasi : " + percent + "%");
		/** /
		WebPage wp = new WebPage();
		String url = "http://wordpress.org/";
		//Document doc = Jsoup.parse(wp.getHtml(url));
		try {
			Document doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
			/** /
			Document doc = Jsoup.connect(url)
					.data("query", "Java")
					.userAgent("Mozilla")
					.cookie("auth", "token")
					.timeout(3000)
					.get();
			/** /
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				System.out.println(link.attr("abs:href"));
			}
			/** /
			System.out.println(doc.select("title").text());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**/

		String k0 = "  ahmad    ";
		String k1 = "    \"ahmad\"   ";
		String k2 = "   ahmad site:zaf.web.id   ";
		
		System.out.println(k1.replaceAll("[^a-zA-Z0-9\".:]", " "));
		
		WebPage wp = new WebPage();
		
		System.out.println(wp.getOperator(k0));
		System.out.println(wp.getOperator(k1));
		System.out.println(wp.getOperator(k2));
		
		String k = k2, kw = "";
		
		switch(wp.getOperator(k)) {
			case 1 :
				System.out.println("tipe 1 : "+wp.getOperator(k));
				kw = wp.trimString(k);
				kw = kw.substring(1, kw.length()-1);
				break;
			case 2 :
				System.out.println("tipe 2 : "+wp.getOperator(k));
				String[] x = k.split("site:");
				kw = wp.trimString(x[0]);
				System.out.println("site : "+wp.trimString(x[1]));
				break;
			default :
				System.out.println("tipe 0 : "+wp.getOperator(k));
				kw = wp.trimString(k);
		}
		
		System.out.println(kw);
		
		
		/**/
		
	}

}
