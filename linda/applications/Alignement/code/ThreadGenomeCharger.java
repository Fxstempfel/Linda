import linda.Linda;
import linda.Tuple;

public class ThreadGenomeCharger extends Thread {

	private Linda linda;
	private int nbThreads;
	private int threadNum;
	private int taillePartie;
	private BDSequences BD;
	private int nbThreadsTot;

	public ThreadGenomeCharger(int threadNum, int nbThreads, int nbThreadsTot, int taillePartie, BDSequences BD, Linda linda) {
		this.linda = linda;
		this.taillePartie = taillePartie;
		this.nbThreads = nbThreads;
		this.BD = BD;
		this.threadNum = threadNum;
		this.nbThreadsTot = nbThreadsTot;
	}

	public void run() {
		Sequence courant = null;
		int ind_top;
		if (threadNum == nbThreads-1) {
			ind_top = BD.taille();
		} else {
			ind_top = (threadNum+1)*taillePartie;
		}
		for (int i = threadNum*taillePartie; i < ind_top; i++){
			courant = BD.lire(i);
			this.linda.write(new Tuple(i%(nbThreadsTot-nbThreads),courant.lireSéquence(),courant.afficher()));
		}
	}
}
