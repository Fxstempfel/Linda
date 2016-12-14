package linda.test;

import linda.Linda;
import linda.Tuple;

public class TestCribleSequentiel {
	
	final Linda linda = new linda.shm.CentralizedLinda();
	private int k;
	public TestCribleSequentiel(int k ) {
		this.k=k;
	}
	
	private void setup() {
		for(int i=2;i<=this.k;i++){
			linda.write(new Tuple(i));
		}
	}
	
	
	private void takeMultiple(int i) {
		for(int j = i+1; j<=this.k;j++) {
			if(j%i==0) {
				linda.tryTake(new Tuple(j));
			}
		}
	
	}
	
	public void doCrible() {
		long timer_start = System.currentTimeMillis();
		this.setup();
		for(int i=0;i<=this.k;i++){
			if(linda.tryRead(new Tuple(i)) != null) {
				//System.out.println(i);
				this.takeMultiple(i);
			}
		}
		System.out.println("Le crible sequentiel a pris : " + (System.currentTimeMillis()-timer_start) + "ms");
		linda.debug("Crible Sequentiel");
	}
	
}
