package linda.test;

import java.util.ArrayList;

import linda.Linda;
import linda.Tuple;

public class TestPhilosophes {

	final Linda linda = new linda.tshm.CentralizedLinda();
	private int nb_philo;

	public TestPhilosophes(int nb_philo) {
		this.nb_philo = nb_philo;
	}

	private void setUp() {
		for(int i=1;i<=this.nb_philo;i++) {
			if(i<this.nb_philo){
				this.linda.write(new Tuple(i,i+1,"Fourchette"));
			} else {
				this.linda.write(new Tuple(i,1,"Fourchette"));
			}
		}
	}
	
	private void launchDinner(){
		ArrayList<ThreadPhilo> listThreads = new ArrayList<ThreadPhilo>();
		
		for(int i=0;i<this.nb_philo;i++){
			listThreads.add(new ThreadPhilo(i+1,this.nb_philo,this.linda));
		}
		
		for(int i=0;i<this.nb_philo;i++) {
			System.out.println(i);
			listThreads.get(i).start();
		}
		
		for(int i=0;i<this.nb_philo;i++){
			try {
				listThreads.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void doPhilo() {
		this.setUp();
		//linda.debug("");
		this.launchDinner();
	}

}
