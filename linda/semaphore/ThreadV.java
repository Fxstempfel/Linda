package linda.semaphore;

import linda.Linda;
import linda.Tuple;

public class ThreadV extends Thread {
	
	private Linda linda;
	private Tuple token;
	
	public ThreadV(Linda linda,Tuple token) {
		this.linda = linda;
		this.token = token;
	}
	
	public void run() {
		linda.write(token);
	}

}
