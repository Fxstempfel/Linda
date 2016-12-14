package linda.test;

import java.util.ArrayList;

import linda.Linda;
import linda.Tuple;

public class TestCribleParallel {
	final Linda linda = new linda.shm.CentralizedLinda();
	private int k;
	private int nb_max_threads = 4;
	
	public TestCribleParallel(int k ) {
		this.k=k;
	}

	private void setup() {
		for(int i=2;i<=this.k;i++){
			linda.write(new Tuple(i));
		}
	}


	private void takeMultiple(int i) {
		ArrayList<ThreadCrible> listThreads = new ArrayList<ThreadCrible>();
		for(int nth=0;nth<nb_max_threads;nth++) {
			listThreads.add(new ThreadCrible((i+1)+((nth*this.k)/nb_max_threads), (i+1)+((nth+1)*this.k/nb_max_threads), i, this.linda));
		}
		
		for(int nth=0;nth<nb_max_threads;nth++){
			listThreads.get(nth).run();
		}
		for(int nth=0;nth<nb_max_threads;nth++){
			try {
				listThreads.get(nth).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void doCrible() {
		this.setup();
		for(int i=0;i<=this.k;i++){
			if(linda.tryRead(new Tuple(i)) != null) {
				//System.out.println(i);
				this.takeMultiple(i);
			}
		}

		linda.debug("Crible Parallel");
	}
}
