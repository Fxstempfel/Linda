package linda.semaphore;

import linda.Linda;
import linda.Tuple;

public class ThreadP extends Thread{

	private Linda linda;
	private Tuple token;
	
	public ThreadP(Linda linda,Tuple token){
		this.linda = linda;
		this.token = token;
	}
	
	
	public void run() {
		linda.take(this.token);
	}
}
