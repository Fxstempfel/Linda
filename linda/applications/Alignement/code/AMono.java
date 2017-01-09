package linda.applications.Alignement.code;

import java.util.Iterator;

import static linda.applications.Alignement.code.FonctionsCalcul.similitude;

/**
 * Created by thibaut on 08/01/17.
 */
public class AMono {
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
}
