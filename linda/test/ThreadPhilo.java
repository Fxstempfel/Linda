package linda.test;

import java.util.Random;

import linda.Linda;
import linda.Tuple;

public class ThreadPhilo extends Thread {
	private int numero_philo;
	private int nb_philo;
	private Linda linda;
	private Tuple f_d;
	private Tuple f_g;
	
	public ThreadPhilo(int numero_philo,int nb_philo,Linda linda){
		this.numero_philo = numero_philo;
		this.nb_philo = nb_philo;
		this.linda = linda;
		if(this.numero_philo==1){
			f_d = new Tuple(1,2,"Fourchette");
			f_g = new Tuple(this.nb_philo,1,"Fourchette");
		}else if (this.numero_philo == this.nb_philo) {
			f_d = new Tuple(this.nb_philo,1,"Fourchette");
			f_g = new Tuple(this.nb_philo-1,this.nb_philo,"Fourchette");
		}else{
			f_d = new Tuple(this.numero_philo,this.numero_philo+1,"Fourchette");
			f_g = new Tuple(this.numero_philo-1,this.numero_philo,"Fourchette");
		}
	}
	
	public void run(){
		int sleep_time_before;
		int sleep_time_lunch;
		while(true) {
			Random rd = new Random();
			sleep_time_before = rd.nextInt(5);
			sleep_time_lunch = rd.nextInt(5);
			
			try {
				this.sleep(sleep_time_before*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Le philosophe : " + this.numero_philo + " demande sa fourchette droite puis gauche " );			
			linda.take(f_d);
			linda.take(f_g);
			System.out.println("Le philosophe : " + this.numero_philo + " a ses deux fourchettes " );
			
			try {
				this.sleep(sleep_time_lunch*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Le philosophe : " + this.numero_philo + " repose ses deux fourchettes " );
			linda.write(f_d);
			linda.write(f_g);
			
		}
	}
	

}
