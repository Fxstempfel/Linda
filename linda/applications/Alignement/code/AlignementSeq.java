//v0   3/1/17 (PM)
import java.net.InetAddress;
import java.io.IOException;
import java.util.Iterator;
import linda.*;
import linda.monoserver.*;
import java.util.*;

//---------------------------------------------------------------------------------------
public class AlignementSeq {

    static int similitude(char [] s, char [] t) {
        int [][] tableSimilitude = new int [s.length][t.length];

        //System.out.println("s ("+s.length+") "+s);
        //System.out.println("t ("+t.length+") "+t);
        //initialisation
        tableSimilitude[0][0] = 0;
        //colonne 0
        for (int i=1 ; i<s.length ; i++) {
            tableSimilitude[i][0]=tableSimilitude[i-1][0]+Sequence.suppression(s[i]);
        }
        //ligne 0
        for (int j=1 ; j<t.length ; j++) {
            tableSimilitude[0][j]=tableSimilitude[0][j-1]+Sequence.insertion(t[j]);
        }
        //remplissage ligne par ligne
        for (int i=1 ; i<s.length ; i++) {
            for (int j=1 ; j<t.length ; j++) {
                tableSimilitude[i][j]=Math.max(tableSimilitude[i-1][j]+Sequence.suppression(s[i]),
                                               Math.max(tableSimilitude[i][j-1]+Sequence.insertion(t[j]),
                                                        tableSimilitude[i-1][j-1]+Sequence.correspondance(s[i],t[j])));
            }
        }
        // résultat (minimal : on pourrait aussi donner le chemin et les transformations,
        // mais on se limite à l'essentiel)
        return tableSimilitude[s.length-1][t.length-1];
    }

    public static void main(String[] args) throws InterruptedException {
        long départ, fin;
        int résultat;
		int nbThreads = Integer.parseInt(args[2]);
		int nbThreadsTr = Integer.parseInt(args[3]);

		String URL_Serv = "";
		try {
			URL_Serv = "//" + InetAddress.getLocalHost().getHostName() + ":" + 5555 + "/monserveur";
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Linda lindaClient = new linda.server.LindaClient(URL_Serv);
		final Linda lindaCtrl = new linda.shm.CentralizedLinda();
		final Linda lindaShared = new linda.shm.SharedLinda();

        BDSequences BDS = new BDSequences();
        BDSequences cible = new BDSequences();

        if (args.length == 4) { //analyse des paramètres
            try {
                BDS.lier(args[0]);
                cible.lier(args[1]);
            }
            catch (IOException iox) {
                throw new IllegalArgumentException("Usage : AlignementSeq <chemin BD> <chemin cible>");
            }
        }
        if (BDS.estVide() || cible.estVide())
                throw new IllegalArgumentException("Usage : AlignementSeq <chemin BD> <chemin cible>");

        //appel correct
/*        départ = System.nanoTime();
        résultat = AlignementSeq.AMono(BDS,cible,0);
        fin = System.nanoTime();
        System.out.println("test mémoire monoactivité : durée = "+ (fin-départ) /1_000+
        												"µs -> résultat : " + résultat);
                           
        départ = System.nanoTime();
        résultat = AlignementSeq.AMonoLinda(BDS,cible,0,lindaCtrl);
        fin = System.nanoTime();
        System.out.println("test linda monoactivité : durée = "+ (fin-départ) /1_000+
                										"µs -> résultat : " + résultat);

*/
		départ = System.nanoTime();
        résultat =
		AlignementSeq.AMonoLindaParallel(BDS,cible,0,nbThreads,nbThreadsTr,lindaClient);
        fin = System.nanoTime();
        System.out.println("test linda monoserver : durée = "+ (fin-départ) /1_000+
                										"µs -> résultat : " + résultat);

		départ = System.nanoTime();
        résultat =
		AlignementSeq.AMonoLindaParallel(BDS,cible,0,nbThreads,nbThreadsTr,lindaCtrl);
        fin = System.nanoTime();
        System.out.println("test linda centralized : durée = "+ (fin-départ) /1_000+
                										"µs -> résultat : " + résultat);
		départ = System.nanoTime();
        résultat =
		AlignementSeq.AMonoLindaParallel(BDS,cible,0,nbThreads,nbThreadsTr,lindaShared);
        fin = System.nanoTime();
        System.out.println("test linda shared: durée = "+ (fin-départ) /1_000+
                										"µs -> résultat : " + résultat);


    }

    static int AMono(BDSequences BD,BDSequences BDcibles,int position) {
        // version "directe", sans Linda, donnée à titre de documentation/spécification
        int score=0;
        int résultat=0;
        Sequence res = null;
        Sequence courant = null;
        Sequence cible = BDcibles.lire(position);
        Iterator<Sequence> it = BD.itérateur();

        while (it.hasNext()) {
            courant = it.next();
            score = similitude(courant.lireSéquence().toCharArray(),cible.lireSéquence().toCharArray());
            if (score > résultat) {
                res = courant;
                résultat = score ;
            }
        }
        System.out.println("cible : "+cible.afficher());
        System.out.println("résultat ("+résultat+"/ "+
                           100*résultat/(cible.lireTailleSeq()*Sequence.correspondance('A','A'))+"%): "+res.afficher());
        return résultat;
    }

    static int AMonoLinda(BDSequences BD,BDSequences BDcibles,int position,Linda l) {
        /* Cette version fait transiter les données par l'espace de tuples,
         * ce qui correspond effectivement au contexte d'exécution où l'on se place.
         * Elle est clairement moins efficace que la précédente
         * (passage par Linda, boucles distinctes pour la lecture et le traitement...),
         * mais représente une base pour une parallélisation.
         *
         * Noter que cette version peut/doit être adaptée (simplement), pour permettre de
         * traiter un volume de données supérieur à la mémoire disponible.
         */

        int score=0;
        int résultat=0;
        Sequence res = null;
        Sequence courant = null;
        Sequence cible = BDcibles.lire(position);
        Iterator<Sequence> it = BD.itérateur();
        Tuple tCourant = null;
        Tuple tCible = null;
        Tuple tRes = null;

        //déposer la cible dans l'espace de tuples
        l.write(new Tuple("cible",cible.lireSéquence(),cible.afficher(),cible.lireTailleSeq()));

        //déposer les séquences dans l'espace de tuples
        while (it.hasNext()) {
            courant = it.next();
            l.write(new Tuple("BD",courant.lireSéquence(),courant.afficher()));
        }

        //chercher la meilleure similitude
        tCible = l.take(new Tuple("cible", String.class, String.class, Integer.class));
        tCourant = l.tryTake(new Tuple("BD",String.class,String.class));
        while (tCourant != null) {
            score = similitude(((String)tCourant.get(1)).toCharArray(),
                               ((String)tCible.get(1)).toCharArray());
            if (score > résultat) {
                tRes = tCourant;
                résultat = score ;
            }
            tCourant = l.tryTake(new Tuple("BD",String.class,String.class));
        }


        System.out.println("cible : "+tCible.get(2));
        System.out.println("résultat ("+résultat+"/ "+
                           100*résultat/(((Integer)tCible.get(3))*Sequence.correspondance('A','A'))
                           +"%): "+tRes.get(2));
        return résultat;
    }

	static int AMonoLindaParallel(BDSequences BD,BDSequences BDcibles,int
			position,int nbThreads,int nbThreadsTr,Linda l) {

        int nbThreadsCharger = nbThreads-nbThreadsTr;/* valeur a regler ! */
        Sequence courant = null;
        Sequence cible = BDcibles.lire(position);
        Tuple tCible = null;
        int résultat=0;

        int taillePartie = BD.taille()/nbThreadsCharger;
		int taillePartieTraiter = BD.taille()/(nbThreads-nbThreadsCharger);

        List<ThreadGenomeCharger> listThreadsCharger = new ArrayList<ThreadGenomeCharger>();
        List<ThreadGenomeTraiter> listThreadsTraiter = new ArrayList<ThreadGenomeTraiter>();
		for (int k=0; k<nbThreads; k++) {
		    if (k<nbThreadsCharger){
		        listThreadsCharger.add(new
						ThreadGenomeCharger(k,nbThreadsCharger,taillePartie,BD,l));
            } else {
		        listThreadsTraiter.add(new ThreadGenomeTraiter(nbThreads-nbThreadsCharger,k,taillePartieTraiter,BD,BDcibles,position,l));
            }
		}


        //déposer la cible dans l'espace de tuples
		tCible = new Tuple("cible",cible.lireSéquence(),cible.afficher(),cible.lireTailleSeq());
		l.write(tCible);

        //on lance tous les threads en meme temps
        for (int j=0; j<nbThreads; j++) {
		    if (j < nbThreadsCharger){
                listThreadsCharger.get(j).start();
            } else {
                listThreadsTraiter.get(j-nbThreadsCharger).start();
            }
        }


		for (int j=0; j<nbThreads; j++) {
			try {
			    if (j>=nbThreadsCharger){
			        listThreadsTraiter.get(j-nbThreadsCharger).join();
                } else {
			        listThreadsCharger.get(j).join();
                }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


		int score = -100;
		Tuple tRes = null;
		Tuple tCourant = null;
		int scoreCourant = 0;
		for (int j=0; j<nbThreads-nbThreadsCharger; j++) {
			tCourant = l.tryTake(new Tuple("res"+j,Integer.class,Tuple.class));
			if (tCourant != null) {
				scoreCourant = (Integer)tCourant.get(1);
				if (scoreCourant > score) {
					score = scoreCourant;
					tRes = (Tuple)tCourant.get(2);
				}
			}
		}
		résultat = score;

        System.out.println("cible : "+tCible.get(2));
        System.out.println("résultat ("+résultat+"/ "+
                           100*résultat/(((Integer)tCible.get(3))*Sequence.correspondance('A','A'))
                           +"%): "+tRes.get(2));
        return résultat;
    }
}
