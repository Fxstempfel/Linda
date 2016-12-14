package linda.semaphore;

import linda.Linda;
import linda.Tuple;

public class LindaSemaphore {
	
	Linda linda = new linda.shm.CentralizedLinda();
	Tuple t = new Tuple("Token");
	
	public LindaSemaphore(int nb) {
		for(int i=0;i<nb;i++) {
			linda.write(t);
		}
		
	}
	
	public void p(){
		ThreadP thread = new ThreadP(linda,t);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void v(){
		ThreadV thread = new ThreadV(linda,t);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
