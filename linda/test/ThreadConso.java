package linda.test;

import java.util.Random;

import linda.Linda;
import linda.Tuple;

public class ThreadConso extends Thread {
	private Linda linda;
	private int num_conso;
	private Tuple t = new Tuple("Un objet");
	
	public ThreadConso(Linda linda,int num_conso){
		this.linda = linda;
		this.num_conso = num_conso;
	}
	
	public void run(){
		while(true){
			int sleep_time ;
			Random random = new Random();
			sleep_time = random.nextInt(5);
			try {
				this.sleep(sleep_time*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Le consomateur " + this.num_conso + " essaye de prendre un objet");
			linda.take(t);
			System.out.println("Le consomateur " + this.num_conso + " a pris un objet");
		}
	}
}
