package linda.applications.Alignement.code;

import linda.Linda;
import linda.Tuple;

import java.util.HashMap;
import java.util.Iterator;

import static linda.applications.Alignement.code.FonctionsCalcul.similitude;

/**
 * Created by thibaut on 08/01/17.
 */
public class AMultiActivites {

    private static final int NBTHREADS = 4;

    private Linda l;
    private int bestScore = 0;
    private Tuple bestTuple;
    private BDSequences BD;
    private BDSequences BDcibles;
    private int position;

    public AMultiActivites(BDSequences BD, BDSequences BDcibles, int position, Linda l) {
        this.l = l;
        this.BD = BD;
        this.BDcibles = BDcibles;
        this.position = position;
    }

    private int Align(){
        ThreadWrite tw = new ThreadWrite();
        tw.start();

        ThreadRead[] trs = new ThreadRead[NBTHREADS];
        for (ThreadRead tr : trs) {
            tr = new ThreadRead();
            tr.start();
        }

        try {
            tw.join();
            for (ThreadRead tr : trs){
                tr.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Tuple tCible = l.read(new Tuple("cible", String.class, String.class, Integer.class));
        System.out.println("cible : "+tCible.get(2));
        System.out.println("résultat ("+bestScore+"/ "+
                100*bestScore/(((Integer)tCible.get(3))*Sequence.correspondance('A','A'))
                +"%): "+bestTuple.get(2));
        return bestScore;
    }

    private synchronized void registerResult(Tuple result, int score){
        if (score >= bestScore){
            bestTuple = result;
            bestScore = score;
        }
    }

    private class ThreadWrite extends Thread{

        @Override
        public void run() {

            Sequence cible = BDcibles.lire(position);
            Iterator<Sequence> it = BD.itérateur();
            Sequence courant = null;


            //déposer la cible dans l'espace de tuples
            l.write(new Tuple("cible",cible.lireSéquence(),cible.afficher(),cible.lireTailleSeq()));

            //déposer les séquences dans l'espace de tuples
            while (it.hasNext()) {
                courant = it.next();
                l.write(new Tuple("BD",courant.lireSéquence(),courant.afficher()));
            }
        }
    }

    private class ThreadRead extends Thread{

        @Override
        public void run(){
            Tuple tCourant = null;
            Tuple tCible = null;
            Tuple tRes = null;
            
            int score;
            int resultat = 0;

            //chercher la meilleure similitude
            tCible = l.read(new Tuple("cible", String.class, String.class, Integer.class));
            tCourant = l.tryTake(new Tuple("BD",String.class,String.class));
            while (tCourant != null) {
                score = similitude(((String)tCourant.get(1)).toCharArray(),
                        ((String)tCible.get(1)).toCharArray());
                if (score > resultat) {
                    tRes = tCourant;
                    resultat = score ;
                }
                tCourant = l.tryTake(new Tuple("BD",String.class,String.class));
            }


            
        }
    }


}
