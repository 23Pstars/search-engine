package handler;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.AgentParams;
import model.Settings;
import model.Website;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebPage {
	
	/** /
	public String getHtml(String link) {
		String line, html = "";
		try {
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = in.readLine()) != null) html += line;
			in.close();
			con.disconnect();
		} catch (MalformedURLException e) {
			_e("Error1 saat mengambil HTML dari : "+link+", "+e.getMessage());
		} catch (IOException e) {
			_e("Error2 saat mengambil HTML dari : "+link+", "+e.getMessage());
		}
		return html;
	}
	/**/
	
	/**
	 * untuk melakukan pencarian pada web page
	 * termasuk mendapatkan link dan host yang ada pada suatu halaman web
	 * 
	 * @param url
	 * @param keyword
	 * @return
	 */
	public Website searchOnPage(AgentParams ap) {
		
		Website w = new Website();
		
		String url = ap.getUrl();
		String keyword = ap.getKeyword();
		
		try {
			// membuat objek doc untuk web page
			/** /
			Document doc = Jsoup.connect(url)
					.data("query", "Java")
					.userAgent("Mozilla")
					.cookie("auth", "token")
					.timeout(3000)
					.get();
			/**/
			Document doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", url);
			
			
			//-- SEARCHING V1--//
			
			/** /
			// cek title apakah mengandung keyword
			w.setFounded( !w.isFounded() && doc.select("title").text().matches("(.*)" + keyword.toLowerCase() + "(.*)") ? true : w.isFounded() );
			
			// cek description apakah mengandung keyword
			w.setFounded( !w.isFounded() && doc.select("meta[name=description]").attr("content").toLowerCase().matches("(.*)" + keyword.toLowerCase() + "(.*)") ? true : w.isFounded() );
			
			// cek keywords apakah mengandung keyword
			w.setFounded( !w.isFounded() && doc.select("meta[name=keywords]").attr("content").toLowerCase().matches("(.*)" + keyword.toLowerCase() + "(.*)") ? true : w.isFounded() );
			
			// jika ditemukan pada halaman, maka di set atribut web page berikut
			if ( w.isFounded() ) {
				
				w.setLink(url);
				w.setTitle(doc.select("title").text().equals("") ? url : doc.select("title").text());
				w.setDescription(doc.select("meta[name=description]").attr("content").equals("") ? url : doc.select("meta[name=description]").attr("content"));
			}
			/**/
			
			//-- SEARCHING V2 --//
			
			// title website
			String titleText =  doc.select("title").text();
			
			// description meta website
			String descriptionText = doc.select("meta[name=description]").attr("content");
			
			// keywords meta website
			String keywordsText = doc.select("meta[name=keywords]").attr("content");
			
			// gabungin semua text informasi
			
			String text = titleText + descriptionText + keywordsText;
			
			// convert ke lower case
			keyword = keyword.toLowerCase();
			text = text.toLowerCase();
			
			// bersihkan keyword, regex => a-z A-Z 0-9
			keyword = keyword.replaceAll("[^a-zA-Z0-9\".:]", " ");
			text = text.replaceAll("[^a-zA-Z0-9]", " ");
			
			// hapus multiple whitespace pada hasil bersih-bersih diatas
			keyword = keyword.replaceAll("\\s+", " ");
			text = text.replaceAll("\\s+", " ");
			
			//-- LOAD SETTING FROM DATABASE --//
			
			Database db = new Database();
			Settings setting = db.getSettings();
			db.close();
			
			switch( this.getOperator(keyword) ) {
				case 1 :
					keyword = keyword.substring(1, keyword.length()-1);
					if (text.matches("(.*)" + keyword + "(.*)")) {
						w.setFounded(true);
						
						w.setLink(url);
						
						/**
						 * jika description nya kosong, maka akan diisi oleh keywords
						 * dan jika keyword juga kosong, maka description diisi dengan title.
						 * dan jika title sampe kosong juga, maka description terpaksa diisi dengan url
						 * ini bertujuan menghindari kesalahan pada clss Parser
						 */
						w.setTitle(titleText.equals("") ? url : titleText);
						w.setDescription(descriptionText.equals("") ? ( keywordsText.equals("") ? ( titleText.equals("") ? url : titleText ) : keywordsText ) : descriptionText);
						w.setPercentage(100);
					}
					break;
				case 2 :
					String[] x = keyword.split("site:");
					
					String kw = x[0].replaceAll("\\s+", " ");
					String site = x[1].replaceAll("\\s+", " ");
					
					if (text.matches("(.*)" + kw + "(.*)") && url.contains(site)) {
						w.setFounded(true);
						
						w.setLink(url);
						
						/**
						 * jika description nya kosong, maka akan diisi oleh keywords
						 * dan jika keyword juga kosong, maka description diisi dengan title.
						 * dan jika title sampe kosong juga, maka description terpaksa diisi dengan url
						 * ini bertujuan menghindari kesalahan pada clss Parser
						 */
						w.setTitle(titleText.equals("") ? url : titleText);
						w.setDescription(descriptionText.equals("") ? ( keywordsText.equals("") ? ( titleText.equals("") ? url : titleText ) : keywordsText ) : descriptionText);
						w.setPercentage(100);
					}
					break;
				default :
					// pisahkan keyword menjadi per-kata kedalam sebuah array berdasarkan space
					String[] keywordArray = keyword.split(" ");
					
					float ditemukan = 0;
					
					// cari tiap kata pada text
					for (String kata : keywordArray) {
						if (text.matches("(.*)" + kata + "(.*)")) ditemukan++;
					}
					
					float persentasi = (ditemukan/keywordArray.length)*100;
					
					/**
					 * jika persentasi lebih dari atau sama dengan persentasi dari pengaturan
					 */
					if (setting.getMinPercentageMatch() <= persentasi) {
						w.setFounded(true);
						
						w.setLink(url);
						
						/**
						 * jika description nya kosong, maka akan diisi oleh keywords
						 * dan jika keyword juga kosong, maka description diisi dengan title.
						 * dan jika title sampe kosong juga, maka description terpaksa diisi dengan url
						 * ini bertujuan menghindari kesalahan pada clss Parser
						 */
						w.setTitle(titleText.equals("") ? url : titleText);
						w.setDescription(descriptionText.equals("") ? ( keywordsText.equals("") ? ( titleText.equals("") ? url : titleText ) : keywordsText ) : descriptionText);
						w.setPercentage(persentasi);
					}
			}
			
			
			//-- EXTRACT LINKS --//
			
			List<String> linkFounded = new ArrayList<String>();
			List<String> hostFounded = new ArrayList<String>();
			
			Elements links = doc.select("a[href]");
			
			//int total_links = 0, total_host = 0, total_sublink = 0, total_host_not_duplicate = 0, total_link_under_max_deep = 0, total_link_not_duplicate = 0;
			
			//_e("links yang ditemukan dihalaman!");
			
			String l, lBaseUrl;
			
            for (Element link : links) {
            	
            	l = link.attr("abs:href");
            	
            	//_e(l);
            	
            	/**
				 * memeriksa protokol link yang ditemukan dan bukan merupakan hash tag
				 */
				if ((isHttp(l) || isHttps(l)) && !isHashTag(l)) {
					
					//_e("isHttp / isHttps / !isHashTag : "+l);
					//total_links++;
	            	
	            	/**
	            	 * untuk setiap link, periksa apakah link tersebut host baru atau tidak 
	            	 */
	            	if (isHost(url, l)) {
	            		
	            		lBaseUrl = getBaseUrl(l);
	            		
	            		//_e("host baru : " + l);
	            		//total_host++;
	            		
	            		/**
	            		 * menghindari duplikasi
	            		 */
	            		if (!duplicate(hostFounded, lBaseUrl)) {
	            			
	            			//_e("host not duplicate : " + lBaseUrl);
	            			//total_host_not_duplicate++;
	            			
	            			hostFounded.add(lBaseUrl);
	            			
	            		}
	            		
	            		/**
		            	 * jika bukan sebuah host
		            	 */
	            	} else {
	            		
	            		//_e("sublink : "+l);
	            		//total_sublink++;

	                    /**
	                     * memeriksa kedalaman link tersebut, sesuai dengan Settings
	                     */
	                    if (linkDeepLevel(l) <= setting.getMaxDeep()) {
	                    	
	                    	//_e("link under max deep : "+l);
	                    	//total_link_under_max_deep++;
	            					
	            			/**
	            			 * dan terakhir untuk menghindari duplikasi
	            			 */
	            			if (!duplicate(linkFounded, l)) {
	            				
	            				//_e("link no duplicated : "+l);
	            				//total_link_not_duplicate++;
	            				
	            				linkFounded.add(l);
	            				
	            			} // duplicate
	            			
	            		} // comparation linkDeepLevel <==> setting.getMaxDeep
	            				
	            	} // jika bukan merupakan host
					
				}
            	
            } // looping untuk link yang ditemukan
            
            //_e(url+" => total_links : "+total_links+", total_host : "+total_host+", total_sublink : "+total_sublink+", total_host_not_duplicate : "+total_host_not_duplicate+", total_link_under_max_deep : "+total_link_under_max_deep+", total_link_not_duplicate : "+total_link_not_duplicate);
            
            w.setLinkFounded(linkFounded);
            w.setHostFounded(hostFounded);
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			_e("[ERROR fetching web] "+url);
		}
			
		return w;
	}
	
	/**
	 * untuk memeriksa tingkat kedalaman suatu link
	 * nantinya digunakan untuk membatasi akses bot
	 * 
	 * @param url
	 * @return
	 */
	public int linkDeepLevel(String url) {
		int urlCount = 0;
		for (int j=0; j<url.length(); j++) { urlCount += url.charAt(j) == '/' ? 1 : 0; }
		
		/**
		 * PASTI mengandung protokol http // https makanya dikurangi 2 
		 */
		return urlCount - 2;
	}
	
	/**
	 * menentukan apakah link yang diterima merupakan host baru atau tidak
	 * 
	 * @param url1
	 * @param url2
	 * @return
	 */
	public boolean isHost(String url1, String url2) {
		
		/**
		 * membandingkan baseUrl url1 dan url2
		 */
		
		/** /
		_e("membandingkan "+url1+" dengan "+url2);
		_e("membandingkan "+getBaseUrl(url1)+" dengan "+getBaseUrl(url2));
		/**/
		
		return !getBaseUrl(url1).equals(getBaseUrl(url2));
	}
	
	/**
	 * untuk mencari baseUrl dari link yang didapat saat pencarian
	 * misal : 
	 * http://anu.com/a/b/c/d/f.php => http://anu.com
	 * 
	 * @param url
	 * @return
	 */
	public String getBaseUrl(String url) {
		if (isBaseUrl(url)) {
			return url;
		} else {
			if (isHttp(url)) {
				
				/**
				 * cek format link, jika tidak sesua standar (ada slash "/" diakhir)
				 * maka tambahkan slash "/"
				 */
				if (badHostFormat(url)) url += "/";
				
				/**
				 * angka 7 didapat dari 'http://x' (7, karena yang dihitung mulai x)
				 */
				return url.substring(0, url.indexOf('/', 7));
				
			} else if (isHttps(url)) {
				
				/**
				 * cek format link, jika tidak sesua standar (ada slash "/" diakhir)
				 * maka tambahkan slash "/"
				 */
				if (badHostFormat(url)) url += "/";
				
				/**
				 * angka 8 didapat dari 'https://x' (8, karena yang dihitung mulai x)
				 */
				return url.substring(0, url.indexOf('/', 8));
				
			} else {
				
				return null;
				
			}
		}
	}
	
	/**
	 * untuk melakukan pengecekan bahwa url adalah protokol HTTP
	 * bukan mailto, call, ftp, dll
	 * 
	 * @param url
	 * @return
	 */
	public boolean isHttp(String url) {
		return url.contains("http://");
	}
	
	/**
	 * untuk melakukan pengecekan bahwa url adalah protokol HTTPS
	 * bukan mailto, call, ftp, dll
	 * 
	 * @param url
	 * @return
	 */
	public boolean isHttps(String url) {
		return url.contains("https://");
	}
	
	public boolean isBaseUrl(String url) {
		return linkDeepLevel(url) == 0;
	}
	
	/**
	 * untuk menentukan hashtag pada url,
	 * hashtag berada dalam 1 page
	 * 
	 * @param url
	 * @return
	 */
	public boolean isHashTag(String url) {
		return url.contains("#");
	}
	
	/**
	 * untuk memeriksa link pada sebuah list,
	 * apakah sudah ada atau belum
	 * 
	 * @param list
	 * @param url
	 * @return
	 */
	public boolean duplicate(List<String> list, String url) {

		/**
		 * hapus prefix link, untuk kemungkinan duplikasi
		 */
		
		String url1, url2, url3, url4;
		
		if (isHttp(url)) {
			url1 = url.replace("http://", "");
			url2 = url.replace("http://www.", "");
			url3 = url.replace("http://www.", "http://");
			url4 = isBaseUrl(url) ? url + "/" : url;
		} else if (isHttps(url)) {
			url1 = url.replace("https://", "");
			url2 = url.replace("https://www.", "");
			url3 = url.replace("https://www.", "http://");
			url4 = isBaseUrl(url) ? url + "/" : url;
		} else {
			url1 = url2 = url3 = url4 = "";
		}
		
		return list.contains(url) || list.contains(url1) || list.contains(url2) || list.contains(url3) || list.contains(url4);
	}
	
	/**
	 * jika mengandung protokol http:// atau https://
	 * tapi format link tidak baik
	 * baik => http://example.com/
	 * buruk => http://example.com
	 * 
	 * @param url
	 * @return
	 */
	public boolean badHostFormat(String url) {
		return linkDeepLevel(url) == 0;
	}
	
	/**
	 * parsing robots.txt
	 * @param host
	 */
	public List<String> getRobotsTxtList(String host) {
		
		List<String> result = new ArrayList<String>(); 
		
		String ss = "";
		
		try {
			URL url = new URL( host + ( badHostFormat( host ) ? "/" : "" ) + "robots.txt" );
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while((line = in.readLine()) != null)
			{
			    ss += line;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			_e("robots.txt not found : "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			_e("IO error while get robots.txt, "+e.getMessage());
		}
		
		/**
		 * memisahkan komen # dari robots.txt
		 */
		String[] w = ss.split("#");
		
		for (int h=0; h<w.length; h++) {
			
			/**
			 * memisahkan tiap user agent
			 */
			String[] x = w[h].split("User-agent: ");
			
			for(int i=0; i<x.length; i++) {
				
				/**
				 * untuk setiap useragent dilakukan pengecekan untuk link yang Disallow
				 */
				String[] y = x[i].split("Disallow: ");
				
				/**
				 * dan jika useragent (*) ditemukan, itu artinya semua bot tidak
				 * boleh mengakses link yang ada pada daftar Disallow dibawahnya
				 */
				if (y[0].equals("*")) {
					for(int j=1; j<y.length; j++){
						result.add(host+y[j].substring(1));
						//_e("link ["+j+"] : " + y[j]);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * untuk mendeteksi operator keyword
	 *
	 * 0 => default (sesuai pengaturan)
	 * 1 => "" (mencari kata / kalimat yang persis sama)
	 * 2 => site: (mencari didalam situs tertentu)
	 *
	 * @param keyword
	 * @return
	 */
	public int getOperator(String keyword) {
		String k = trimString(keyword);
		if (k.startsWith("\"") && k.endsWith("\"")) return 1;
		else if (k.contains("site:")) return 2;
		else return 0;
	}
	
	/**
	 * untuk ngapus spasi di awal dan akhir dari sebuah string
	 * 
	 * @param str
	 * @return
	 */
	public String trimString(String str) {
		String result = str;
		while(result.startsWith(" ")) {
			result = result.substring(1, result.length());
		}
		while(result.endsWith(" ")) {
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	
	void _e(String msg) {
    	System.out.println(msg);
    }

}