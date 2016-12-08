package linda.test;

import java.util.ArrayList;

import linda.Linda;

public class TestProducteurConsomateur {
	final Linda linda = new linda.tshm.CentralizedLinda();
	private int nb_conso;
	private int nb_produc;
	
	public TestProducteurConsomateur(int nb_conso,int nb_produc) {
		this.nb_conso = nb_conso;
		this.nb_produc = nb_produc;
	}
	
	private void launchScheme() {
		ArrayList<ThreadConso> list_conso = new ArrayList<ThreadConso>();
		ArrayList<ThreadProduc> list_produc = new ArrayList<ThreadProduc>();
		
		for(int i=0;i<this.nb_conso;i++){
			list_conso.add(new ThreadConso(this.linda, i+1));
		}
		for(int i=0;i<this.nb_produc;i++){
			list_produc.add(new ThreadProduc(this.linda,i+1));
		}
		
		for(int i=0;i<this.nb_conso;i++){
			list_conso.get(i).start();
		}
		for(int i=0;i<this.nb_produc;i++){
			list_produc.get(i).start();
		}
		
		for(int i=0;i<this.nb_conso;i++){
			try {
				list_conso.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i=0;i<this.nb_produc;i++){
			try {
				list_produc.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void doPC(){
		this.launchScheme();
	}
	
}
