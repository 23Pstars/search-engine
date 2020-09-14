package handler;

import model.AgentParams;
import model.AgentResponse;
import model.Tags;
import model.Website;

public class Parser {
	
	/**
	 * untuk meng-konversi "getArguments()" bawaan dari agent kedalam
	 * bentuk objek model
	 * 
	 * @param obj
	 * @return
	 */
	public AgentParams getAgentParamsModel(Object[] obj) {
		
		AgentParams x = new AgentParams();
		
		/**
		 * versi 2
		 */
		x.setKeyword(obj[0].toString());	// keyword
		try {
			x.setUrl(obj[1].toString());		// url
		} catch (Exception e) {
			/**
			 * getArguments() pada server hanya 1 dimensi, jadi abaikan
			 */
		}
		
		return x;
	}
	
	/**
	 * untuk mengembalikan objek model AgentParams kedalam bentuk
	 * "getArguments()" default dari Agent
	 * 
	 * @param ap
	 * @return
	 */
	public Object[] getAgentParamObj(AgentParams ap) {
		Object x[] = new Object[2];
		
		/**
		 * versi 2
		 */
		x[0] = ap.getKeyword();				// keywird
		x[1] = ap.getUrl();					// url
		
		return x;
	}
	
	/**
	 * untuk mendeteksi jenis response yang didapat dari agent
	 * 
	 * @param r
	 * @return
	 */
	public AgentResponse getResponseType(String r) {
		if (r.contains(Tags.agentCreated)) {
			return AgentResponse.AgentCreated;
		} else if (r.contains(Tags.hostFounded)) {
			return AgentResponse.HostFounded;
		} else if (r.contains(Tags.linkFounded)) {
			return AgentResponse.LinkFounded;
		} else if (r.contains(Tags.websiteMatch)) {
			return AgentResponse.WebsiteMatch;
		} else {
			return null;
		}
	}
	
	/**
	 * packing agent created
	 * 
	 * @param ac
	 * @return
	 */
	public String packAgentCreated(String ac) {
		return Tags.agentCreated + Tags.separator + ac + Tags.separator;
	}
	
	/**
	 * parsing response berupa agent created
	 * 
	 * @param ac
	 * @return
	 */
	public String parseAgentCreated(String ac) {
		return ac.split(Tags.separator)[1];
	}
	
	/**
	 * packing website match
	 * 
	 * @param w
	 * @return
	 */
	public String packWebsiteMatch(Website w) {
		return Tags.websiteMatch + Tags.separator + w.getLink() + Tags.separator + w.getTitle() + Tags.separator + w.getDescription() + Tags.separator;
	}
	
	/**
	 * parsing response berupa website match dari Agent
	 * 
	 * @param c
	 * @return
	 */
	public Website parseWebsiteMatch(String c) {
		Website w = new Website();
		String[] x = c.split(Tags.separator);
		w.setLink(x[1]);
		w.setTitle(x[2]);
		w.setDescription(x[3]);
		return w;
	}
	
	public String parseWebsiteMatchToXML(Website w) {
		return Tags.websiteStart + Tags.linkStart + w.getLink() + Tags.linkClose + Tags.titleStart + w.getTitle() + Tags.titleClose + Tags.descriptionStart + w.getDescription() + Tags.descriptionClose + Tags.websiteClose;
	}
	
	/**
	 * packing host founded
	 * 
	 * @param hf
	 * @return
	 */
	public String packHostFounded(String hf) {
		return Tags.hostFounded + Tags.separator + hf + Tags.separator;
	}
	
	/**
	 * parsing response berupa host founded dari agent
	 * 
	 * @param hf
	 * @return
	 */
	public String parseHostFounded(String hf) {
		return hf.split(Tags.separator)[1];
	}
	
	/**
	 * packing link founded
	 * 
	 * @param lf
	 * @return
	 */
	public String packLinkFounded(String lf) {
		return Tags.linkFounded + Tags.separator + lf + Tags.separator;
	}
	
	public String parseLinkFounded(String lf) {
		return lf.split(Tags.separator)[1];
	}
	
	void _e(String msg) {
    	System.out.println(msg);
    }
	
}
