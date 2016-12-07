package linda.test;

import linda.Linda;
import linda.Tuple;

public class ThreadCrible extends Thread {
	
	private int debut;
	private int fin;
	private int multiple;
	private Linda linda;
	
	public ThreadCrible(int debut,int fin,int multiple, Linda linda) {
		this.debut=debut;
		this.fin=fin;
		this.multiple = multiple;
		this.linda = linda;
	}

	public void run() {
		for(int i = debut; i<=fin;i++){
			if(i%this.multiple==0) {
				this.linda.tryTake(new Tuple(i));
			}
		}
	}
}
