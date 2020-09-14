import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.lang.acl.ACLMessage;

public class HelloAgent extends Agent {
	
	protected void setup() {
		addBehaviour(new Test2(this));
		addBehaviour(new Test(this));
	}

}

class Test extends SimpleBehaviour{
	public Test(Agent a){
		super(a);
	}
	public void action(){
		System.out.print("<!--start-->");
		System.out.println("Hello world! My name is "+myAgent.getLocalName());
		System.out.print("<!--end-->");
	}
	private boolean finished = true;
	public boolean done() {
		return finished;
	}
}

class Test2 extends CyclicBehaviour {
	int i=0;
	public Test2(Agent a) {
		super(a);
	}
	public void action() {
		/** /
		if (i==0) System.out.print("<!--start-->");
		System.out.println("Hello Cyclic behaviur<br/>");
		if (i++ >= 10) {
			System.out.print("<!--end-->");
			System.exit(0);
		}
		/**/
		if (i++ <= 10) System.out.println("Hello Cyclic Behaviour<br/>");
	}
}