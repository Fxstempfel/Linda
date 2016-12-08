package linda.test;

import java.util.Random;

import linda.Linda;
import linda.Tuple;

public class ThreadProduc extends Thread {
	private Linda linda;
	private int num_produc;
	private Tuple t = new Tuple("Un objet");

	public ThreadProduc(Linda linda,int num_produc){
		this.linda = linda;
		this.num_produc = num_produc;
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
			System.out.println("Le producteur " + this.num_produc + " dépose un objet");
			linda.write(t);
		}
	}
}
