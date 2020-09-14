import java.util.ArrayList;
import java.util.List;

import model.AgentParams;
import model.Settings;
import model.Tags;
import model.Website;
import handler.Database;
import handler.Parser;
import handler.WebPage;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 *
 * @author zaf
 */
public class ServerAgent extends Agent {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * inisial process untuk host directory list link
	 */
	public int processId = 0;

	/**
	 * menangani operasi database
	 */
	public Database db;
	
	/**
	 * untuk keperlauna parsing format data
	 */
	public Parser p;
	
	/**
	 * untuk menyimpan daftar link dari website yang sesuai dengan keyword
	 */
	public List<Website> resultWeb = new ArrayList<Website>();
	
	/**
	 * sama seperti resultWeb, hanya saja berfngsi untuk memeriksa duplikat website yang telah ditemukan saja
	 */
	public List<String> websiteMatch = new ArrayList<String>();
	
	/**
	 * load link - link yang ada di hostDictionary sebagai initial links
	 */
	//public List<String> hostDirectoryLink;
	
	/**
	 * list link yang telah dibuatkan BotAgent (processed)
	 * gunanya untuk merekam link - link yang telah dibuatkan BotAgent
	 * sehingga tidak terjadi duplikasi
	 */
	public List<String> linksProcessed = new ArrayList<String>();
	
	/**
	 * berisi daftar antrian link, untuk menentukan kapan pencarian akan berakhir,
	 * yaitu dengan membandingkan apakah antrian link sudah kosong atau masih ada
	 */
	public List<String> linksQueued = new ArrayList<String>();
	
	/**
	 * berisi daftar link yang tidak akan dilakukan penelurusan
	 * dari site blocked dan dari robots.txt
	 */
	public List<String> linksIgnored = new ArrayList<String>();
	
	/**
	 * agent params
	 */
	public AgentParams agentParams;
	
	/**
	 * load pengaturan dari database
	 */
	public Settings setting;
	
	/**
	 * untuk keperluan web page processing
	 */
	public WebPage wp;
	
	/**
	 * untuk keperluan penyimpanan sementara objek Website
	 */
	public Website w;
	
	/**
	 * nyimpen sementara daftar link dari robots.txt
	 */
	public List<String> y = new ArrayList<String>();
	
	/**
	 * nyimpen sementara pesan yang dikirim oleh agent
	 */
	public String x;
	
    @Override
    protected void setup(){
    	
    	/** /
    	System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
    	Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    	/**/
    	
    	/* INISIALISASI */
    	
    	db = new Database();
    	p = new Parser();
    	wp = new WebPage();
    	w = new Website();
    	
    	setting = db.getSettings();
    	
    	agentParams = p.getAgentParamsModel(getArguments());
    	
    	/**
    	 * mengaktifkan server untuk menerima pesan dari tiap Agent
    	 */
    	addBehaviour(new CyclicBehaviour(this) {
            
			private static final long serialVersionUID = 1L;

			@Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){
                	
                	switch (p.getResponseType(msg.getContent())) {
                	
                	case AgentCreated :
                		
                		/**
                    	 * jika menerima pesan dari BotAgent, maka agent akan di hapus dari list hostDirectoryLink
                    	 * (sementara tidak digunakan)
                    	 */
                		linksQueued.remove(msg.getSender().getLocalName());
                		//_e("remove host directory agent from link queue : "+p.parseAgentCreated(msg.getContent()));
                        
                		break;
                		
                	case HostFounded :

                		/**
                    	 * jika bot menemukan host baru, maka dibuatkan BotAgent baru,
                    	 * host baru akan disimpan kedalam database tabel host directory
                    	 * 
                    	 * selain itu disimpan ke dalam list linksQueued dan linksProcessed
                    	 * untuk menjamin tidak ada links yang duplikat
                    	 * 
                    	 * pemeriksaan juga dilakukan dengan membandingkan host yang baru ditemukan
                    	 * dengan list site blocked
                    	 */
                		x = p.parseHostFounded(msg.getContent());
                		
                		//_e("host received : "+x);
                		
                		/**
                		 * sebelumnya memeriksa file robots.txt nya jika tersedia
                		 */
                		y = wp.getRobotsTxtList(x);
                		
                		/**
                		 * menambahkan list link dari robots.txt kedalam lingIgnored
                		 */
                		for (String yy : y) linksIgnored.add(yy);
                		
                		if (!enoughCondition() && !wp.duplicate(linksProcessed, x) && !wp.duplicate(linksIgnored, x)) {
                			
                			//_e("host founded : "+x);
                			
                			//-- INSERT INTO DATABASE --//
                			db.addHostDirectory(x);
                			
                			/**
                			 * cek apakah agent berhasil dibuat sebelum dimasukkan kedalam list
                			 */
                			agentParams.setUrl(x);
                			
                			if (newBotAgent(agentParams)) {
                				linksProcessed.add(x);
                        		linksQueued.add(x);
                        		//_e("(HF) prepare the new agent for : "+x);
                			}
                		}
                		                		
                		break;
                		
                	case LinkFounded :
                		
                		/**
                		 * jika bot menemukan link baru, maka akan dibuatkan BotAgent baru,
                		 * 
                		 * selain itu disimpan kedalam list linksQueued dan linksProcessed
                		 * untuk menjamin tidak ada links yang duplikat
                		 * 
                		 * pemeriksaan juga dilakukan dengan membandingkan link yang baru ditemukan
                    	 * dengan list site blocked
                		 */
                		x = p.parseLinkFounded(msg.getContent());
                		if (!enoughCondition() && !wp.duplicate(linksProcessed, x) && !wp.duplicate(linksIgnored, x)) {
                			
                			//_e("link founded : "+x);
                			
                			/**
                			 * cek apakah agent berhasil dibuat sebelum dimasukkan kedalam list
                			 */
                			agentParams.setUrl(x);
                			
                			if (newBotAgent(agentParams)) {
                				linksProcessed.add(x);
                        		linksQueued.add(x);
                        		//_e("(LF) prepare the new agent for : "+x);
                			}
                			
                		}
                		                		
                		break;
                		
                	case WebsiteMatch :
                		
                		/**
                		 * jika ada hasil yang sesua dengan pencarian,
                		 * masukkan kedalam list resultWeb dan simpan kedalam database
                		 */
                		w = p.parseWebsiteMatch(msg.getContent());
                		if (!wp.duplicate(websiteMatch, w.getLink())) {
                			
                			//-- ISNERT INTO DATABASE --//
                			db.addWebsiteMatch(w);

                			resultWeb.add(w);
                			websiteMatch.add(w.getLink());
                    		
                    		_e(p.parseWebsiteMatchToXML(w));
                    		
                    		/**
                    		 * - tambah kedalam list resultWeb (done)
                    		 * - tambah kedalam database
                    		 * - tampilin bentuk xml nya (done)
                    		 */
                			
                		}
                		
                		break;
                		
					default:
						
						break;
						
                	}
                	
                	/** /
                	_e("links processed size : "+linksProcessed.size());
                	_e("links queue size : "+linksQueued.size());
                	_e("enoughCondition && isLinksQueueEmpty : "+(enoughCondition() && isLinksQueueEmpty()));
                	/**/
                	
                	/**
                	 * jika sudah sesuai dengan pengaturan
                	 */
                	if ( enoughCondition() && isLinksQueueEmpty() ) endTag();
                	
                	/**
					 * jika semua link queue sudah kosong
					 */
					if (isLinksQueueEmpty()) loopHostDirectoryProcess();
                	
                }
                
                //_e("check cyclic sudah jalan ato belum");
                
            }
        });
    	
    	
    	/* START RESULTING */
    	
    	startTag();

    	/**
    	 * cuma buat ngecek keyword yang masuk kedalam sistem
    	 */
    	//_e("ini adalah keyword : " + agentParams.getKeyword());
    	
        //_e("Server ready...");
    	//_e("keyword : "+agentParams.getKeyword());
    	
    	/**
    	 * pertama load site blocked dari database
    	 */
    	
    	linksIgnored = db.getSiteBlocked();
        
    	/**
    	 * mulai load dan looping host dari database sesuai dengan jumlah host yang ditentukan
    	 * tiap process nya
    	 */
    	loopHostDirectoryProcess();
    	
    }
    
    /**
     * membuka scope pesan ke output file PHP,
     * karena full text pesan dari terminal berisi info - info
     * dari system Agent, dan itu tidak dibutuhkan
     */
    public void startTag() {
    	//_e(Tags.start + Tags.matchStart + Tags.keywordStart + agentParams.getKeyword() + Tags.keywordClose);
    	_e(Tags.start + Tags.matchStart);
    	
    }
    
    /**
     * menutup scope pesan ke output file PHP,
     * karena full text pesan dari terminal berisi info - info
     * dari system Agent, dan itu tidak dibutuhkan
     */
    public void endTag() {
    	//_e(Tags.matchClose + Tags.end);
    	_e(Tags.matchClose + Tags.end);
    	System.exit(0);
    }
    
    /**
     * membuat BotAgent baru
     * 
     * @param agentParams
     */
    public boolean newBotAgent(AgentParams ap){
    	
    	/**
    	 * jika ada kesalahan, URL yang diberikan adalah "" atau null
    	 */
    	if (ap.getUrl().equals("") || ap.getUrl().equals(null) || ap.getUrl() == "" || ap.getUrl() == null) return false;
    	
    	boolean result = true;
    	AgentContainer c = getContainerController();
        try {
            AgentController a = c.createNewAgent(ap.getUrl(), "BotAgent", p.getAgentParamObj(ap));
            a.start();
        } catch (StaleProxyException ex) {
            System.out.println("Error while creating new BotAgent, " + ex.getMessage());
            result = false;
        }
        return result;
    }
    
    /**
     * membandingkan apakah hasil website match yang ditemukan telah sama dengan yang diharapkan (settings)
     * @return
     */
    public boolean enoughCondition() {
    	return setting.getMaxLinksReceived() <= resultWeb.size() || setting.getMaxLinksCrawled() <= linksProcessed.size();
    }
    
    /**
     * untuk mengetahui kapan host directory process di load
     * 
     * @return
     */
    public boolean isLinksQueueEmpty() {
    	/**
    	 * entah dari mana hitunganya angka 1 itu muncul, masih misteri :p
    	 */
    	return linksQueued.size() <= 1;
    }
    
    /**
     * melakukan looping host directory menjadi beberapa process
     */
    public void loopHostDirectoryProcess() {
    	
    	int offset = setting.getHostDirectoryPerProcess();
    	int limit = processId * offset;
    	
    	/**
    	 * inisialisasi link awal untuk memulai pencarian
    	 */
    	List<String> hostDirectoryLink = db.getHostDirectory(offset, limit);
    	
    	/**
    	 * jika di database sudah tidak ada lagi host directory link yang bisa di load
    	 * matikan sistem
    	 */
    	if (hostDirectoryLink.size() == 0) endTag();
    	
    	/**
    	 * copy list host directory ke list linksprocessed
    	 */
    	for (String hd : hostDirectoryLink) {
    		linksProcessed.add(hd);
    		linksQueued.add(hd);
    	}
    	
    	for (String lhd : hostDirectoryLink) {
        	
        	/**
        	 * nantinya digunakan pada setiap looping untuk pembentukan agent
        	 */
        	if ( enoughCondition() ) break;
        	
        	/**
        	 * mengisi url tiap host
        	 */
        	agentParams.setUrl(lhd);
        	
        	/**
        	 * membuat HostAgent baru untuk tiap address
        	 */
        	newBotAgent(agentParams);
        }
    	
    	processId++;
    }
    
    
    void _e(String msg) {
    	System.out.println(msg);
    }
}
