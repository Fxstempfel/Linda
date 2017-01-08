package linda.applications.Alignement.code;

import linda.Linda;
import linda.Tuple;

import java.util.Iterator;

import static linda.applications.Alignement.code.FonctionsCalcul.similitude;

/**
 * Created by thibaut on 08/01/17.
 */
public class AMonoLinda {
    static int AMonoLinda(BDSequences BD, BDSequences BDcibles, int position, Linda l) {
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
}
