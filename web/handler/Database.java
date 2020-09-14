package handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Settings;
import model.Website;


public class Database {
	
	Connection connection;
	Statement statement;
	ResultSet result;
	
	String path = "jdbc:postgresql://localhost/mesin_pencari";
    String username = "winkom";
    String password = "gakpakepassword";
	
	public Database() {

    	try {
			connection = DriverManager.getConnection(path, username, password);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			System.out.println("Error while get db connection, "+e.getMessage());
		}
	}
    
    /**
     * VERSI 2
     * @return
     */
    public List<String> getHostDirectory(int offset, int limit) {
    	List<String> out = new ArrayList<String>();
    	try {
			result = statement.executeQuery("SELECT * FROM host_directory ORDER BY visited DESC LIMIT " + offset + " OFFSET " + limit );
			while (result.next()) {
				out.add(result.getString("address"));
			}
		} catch (SQLException e) {
			System.out.println("Error while get host directory, "+e.getMessage());
		}
    	return out;
	}
    
    /**
     * ambil daftar situs yang di telah di blok
     * 
     * @return
     */
    public List<String> getSiteBlocked() {
    	List<String> out = new ArrayList<String>();
    	try {
			result = statement.executeQuery("SELECT * FROM site_blocked" );
			while (result.next()) {
				out.add(result.getString("link"));
			}
		} catch (SQLException e) {
			System.out.println("Error while get site blocked, "+e.getMessage());
		}
    	return out;
	}
    
    /**
     * buat ngambil pengaturan yang ada didalam database
     * 
     * @return
     */
    public Settings getSettings() {
    	String opt;
    	Settings s = new Settings();
    	try {
    		result = statement.executeQuery("SELECT * FROM settings");
    		while (result.next()) {
    			opt = result.getString("option");
    			if (opt.equals("max_deep")) s.setMaxDeep(result.getInt("value"));
    			if (opt.equals("max_links_received")) s.setMaxLinksReceived(result.getInt("value"));
    			if (opt.equals("max_links_crawled")) s.setMaxLinksCrawled(result.getInt("value"));
    			if (opt.equals("host_directory_per_process")) s.setHostDirectoryPerProcess(result.getInt("value"));
    			if (opt.equals("min_percentage_match")) s.setMinPercentageMatch(result.getFloat("value"));
    		}
    	} catch (Exception e) {
    		System.out.println("Error while get settings from database, "+e.getMessage());
    	}
    	return s;
    }
    
    /**
     * mulai memasukkan address yang telah ditemukan kedalam host_directory
     * 
     * @param address
     */
    public void addHostDirectory(String address) {
    	
    	WebPage wp = new WebPage();
    	
    	String url1, url2, url3, url4;
		
		if (wp.isHttp(address)) {
			url1 = address.replace("http://", "");
			url2 = address.replace("http://www.", "");
			url3 = address.replace("http://www.", "http://");
			url4 = wp.isBaseUrl(address) ? address + "/" : address;
		} else if (wp.isHttps(address)) {
			url1 = address.replace("https://", "");
			url2 = address.replace("https://www.", "");
			url3 = address.replace("https://www.", "http://");
			url4 = wp.isBaseUrl(address) ? address + "/" : address;
		} else {
			url1 = url2 = url3 = url4 = address;
		}
    	
    	/**
    	 * pemeriksaan apakah address sudah ada sebelumnya didalam database
    	 */
    	
    	try {
			result = statement.executeQuery("SELECT address FROM host_directory WHERE address LIKE '%"+address+"%' OR address LIKE '%"+url1+"%' OR address LIKE '%"+url2+"%' OR address LIKE '%"+url3+"%' OR address LIKE '%"+url4+"%'");
			
			/**
			 * jika belum ada didatabase, maka langsung tambahin
			 * dan jika sudah ada, lewatin aja, jangan berikan notifikasi
			 */
			result.first();
			if (result.getRow() <= 0) {
				statement.execute("INSERT INTO host_directory (address) VALUES ('"+address+"')");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Gagal dalam add host directory, "+e.getMessage());
		}
    }
    
    /**
     * mulai memasukkan website yang telah ditemukan kedalam database
     * 
     * @param w
     */
    public void addWebsiteMatch(Website w) {
    	
    	WebPage wp = new WebPage();
    	
    	String url1, url2, url3, url4;
		
		if (wp.isHttp(w.getLink())) {
			url1 = w.getLink().replace("http://", "");
			url2 = w.getLink().replace("http://www.", "");
			url3 = w.getLink().replace("http://www.", "http://");
			url4 = wp.isBaseUrl(w.getLink()) ? w.getLink() + "/" : w.getLink();
		} else if (wp.isHttps(w.getLink())) {
			url1 = w.getLink().replace("https://", "");
			url2 = w.getLink().replace("https://www.", "");
			url3 = w.getLink().replace("https://www.", "http://");
			url4 = wp.isBaseUrl(w.getLink()) ? w.getLink() + "/" : w.getLink();
		} else {
			url1 = url2 = url3 = url4 = w.getLink();
		}
    	
    	/**
    	 * pemeriksaan apakah address sudah ada sebelumnya didalam database
    	 */
    	
    	try {
			result = statement.executeQuery("SELECT link FROM websites WHERE link LIKE '%"+w.getLink()+"%' OR link LIKE '%"+url1+"%' OR link LIKE '%"+url2+"%' OR link LIKE '%"+url3+"%' OR link LIKE '%"+url4+"%'");
			
			/**
			 * jika belum ada didatabase, maka langsung tambahin
			 * dan jika sudah ada, lewatin aja, jangan berikan notifikasi
			 */
			result.first();
			if (result.getRow() <= 0) {
				statement.execute("INSERT INTO websites (title, link, description) VALUES ('"+w.getTitle()+"', '"+w.getLink()+"', '"+w.getDescription()+"')");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Gagal dalam add website match, "+e.getMessage());
		}
    }
    
    /**
     * untuk memutuskan koneksi dengan database
     */
    public void close() {
    	try {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    }
    
}
