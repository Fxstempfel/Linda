import linda.Linda;
import linda.Tuple;

public class ThreadGenomeCharger extends Thread {

	private Linda linda;
	private int nbThreads;
	private int threadNum;
	private int taillePartie;
	private BDSequences BD;

	public ThreadGenomeCharger(int threadNum, int nbThreads, int taillePartie, BDSequences BD, Linda linda) {
		this.linda = linda;
		this.taillePartie = taillePartie;
		this.nbThreads = nbThreads;
		this.BD = BD;
		this.threadNum = threadNum;
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
			this.linda.write(new Tuple("BD",courant.lireSéquence(),courant.afficher()));
		}
	}
}
