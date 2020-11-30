package week1;
import jade.core.Agent;
import java.util.concurrent.TimeUnit;

public class TimerAgent extends Agent{

	protected void setup() {
		
		while(true){
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				System.out.println("Eror");
			} finally {
				System.out.println("My name is " + getAID().getName() + " and 10 seconds have elapsed!");
			}
		}
	}
}
