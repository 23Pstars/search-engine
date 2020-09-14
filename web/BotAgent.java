import handler.Parser;
import handler.WebPage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import model.AgentParams;
import model.Website;

/**
 *
 * @author zaf
 */
public class BotAgent extends Agent {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * untuk keperlauna parsing format data
	 */
	public Parser p;
	
	/**
	 * intansiasi objek pesan untuk komunikasi agent dengan server
	 */
	public ACLMessage msgToServer = new ACLMessage(ACLMessage.INFORM);
	
	/**
	 * agent params
	 */
	public AgentParams agentParams;
	
	/**
	 * untuk keperluan operasi pada sebuah halama web
	 */
	public WebPage wp;
	
	@Override
    protected void setup(){
		
		//_e("inisialisasi");
		
		//-- INISIALISASI --//
		
    	p = new Parser();
    	
    	wp = new WebPage();
    	
    	//------------------//
    	
    	agentParams = p.getAgentParamsModel(getArguments());
    	
    	/**
    	 * menentukan penerima pesan, dalam hal ini "server"
    	 */
    	msgToServer.addReceiver(new AID("server", AID.ISLOCALNAME));
    	
        addBehaviour(new SimpleBehaviour(this) {

			private static final long serialVersionUID = 1L;

			@Override
            public void action() {
            	
            	Website w = wp.searchOnPage(agentParams);
            	
            	try {
            	
            	/**
            	 * jika ditemukan keyword match
            	 */
            	
            	if (w.isFounded()) {
            		msgToServer.setContent(p.packWebsiteMatch(w));
            		send(msgToServer);
            	}
            	
            	/**
            	 * mengirim link host baru yang ditemukan ke 'server'
            	 * untuk dibuatkan BotAgent baru
            	 */
            	
            	/**/
            	//_e("host founded size : "+w.getHostFounded().size());
            	for (String hf : w.getHostFounded()) {
            		//_e("Sending hf on : "+hf);
            		msgToServer.setContent(p.packHostFounded(hf));
                	send(msgToServer);
            	}
            	/**/
            	
            	/**
            	 * mengirim link baru yang ditemukan ke 'server'
            	 * untuk kemudian dibuatkan BotAgent baru
            	 */
            	
            	/**/
            	//_e("link founded size : "+w.getLinkFounded().size());
            	for (String lf : w.getLinkFounded()) {
            		//_e("Sending lf on : "+lf);
					msgToServer.setContent(p.packLinkFounded(lf));
					send(msgToServer);
            	}
            	/**/
            	
            	} catch (Exception e) {
            		_e("[ERROR ADD BEHAVIOUR BotAgent] untuk => "+agentParams.getUrl()+" : "+e.getMessage());
            	}
            	
            	/**/
            	msgToServer.setContent(p.packAgentCreated(myAgent.getLocalName()));
                send(msgToServer);
                /**/
                
                /**
                 * matikan Agent jika sudah selesai (jika bisa)
                 */
                doDelete();
            }
            
            public boolean done() {
                return true;
            }
            
        });
        
    }
	
	void _e(String msg) {
    	System.out.println(msg);
    }
}
