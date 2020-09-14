public class Compile {
	public static void main(String[] args) {
		System.out.print("Compiling ServerAgent... ");
		new ServerAgent();
		System.out.println("done!");
		System.out.print("Compiling BotAgent... ");
		new BotAgent();
		System.out.println("done!");
	}
}
