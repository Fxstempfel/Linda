import java.io.IOException;
import java.util.Iterator;
import linda.*;

public class ThreadGenomeTraiter extends Thread {
	
	private Linda linda;
	private int numThread;
	private Sequence cible;
	private Iterator<Sequence> it;
	private int taillePartie;
	private int nbThreadsTr;
	private int tailleBD;

	public ThreadGenomeTraiter(int nbThreadsTr, int numThread, int taillePartie, BDSequences BD, BDSequences BDcibles, int position, Linda linda) {
		this.linda = linda;
		this.cible = BDcibles.lire(position);
		this.it = BD.itérateur();
		this.numThread = numThread;
		this.taillePartie = taillePartie;
		this.nbThreadsTr = nbThreadsTr;
		this.tailleBD = BD.taille();
	}

	public void run() {

		Tuple tRes = null;
		Tuple tCible = this.linda.take(new Tuple("cible", String.class, String.class, Integer.class));
        Tuple tCourant = null;
        int score = 0;
		int résultat = 0;
		int ind_top;
		int ind_th = numThread%nbThreadsTr;
		if (ind_th == nbThreadsTr-1) {
			ind_top = tailleBD;
		} else {
			ind_top = (ind_th+1)*taillePartie;
		}
		for (int i=ind_th*taillePartie; i<ind_top; i++) {
            tCourant = this.linda.take(new Tuple("BD",String.class,String.class));
            score = AlignementSeq.similitude(((String)tCourant.get(1)).toCharArray(),
                               ((String)tCible.get(1)).toCharArray());
            if (score > résultat) {
                tRes = tCourant;
                résultat = score ;
            }
		}
		this.linda.write(new Tuple("res" + ind_th, résultat, tRes));
	}
}
