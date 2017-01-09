package linda.applications.Alignement.code;//v0   3/1/17 (PM)
import java.io.IOException;
import java.util.Iterator;
import linda.*;

import static linda.applications.Alignement.code.FonctionsCalcul.similitude;

//---------------------------------------------------------------------------------------
public class AlignementSeq {

    public static void main(String[] args) throws InterruptedException {
        long départ, fin;
        int résultat;

        final Linda linda = new linda.shm.CentralizedLinda();
        BDSequences BDS = new BDSequences();
        BDSequences cible = new BDSequences();

        if (args.length == 2) { //analyse des paramètres
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
        départ = System.nanoTime();
        résultat = AMono.AMono(BDS,cible,0);
        fin = System.nanoTime();
        System.out.println("test mémoire monoactivité : durée = "+ (fin-départ) /1_000+
        												"µs -> résultat : " + résultat);
                           
        départ = System.nanoTime();
        résultat = AMonoLinda.AMonoLinda(BDS,cible,0,linda);
        fin = System.nanoTime();
        System.out.println("test linda monoactivité : durée = "+ (fin-départ) /1_000+
                										"µs -> résultat : " + résultat);
    }
}