
import linda.Linda;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by thibaut on 11/01/17.
 */
public class AlignementBatchTest {

    public static void main(String[] args) throws InterruptedException {
        long départ, fin;
        int résultat;
        int nbThreads = 300;
        int nbThreadsTr = 1;

        String URL_Serv = "//fix-N550JK:5555/monserveur";
        //final Linda lindaClient = new linda.server.LindaClient(URL_Serv);
        Linda lindaCtrl = new linda.shm.CentralizedLinda();
        Linda lindaShared = new linda.shm.SharedLinda();

        BDSequences BDS = new BDSequences();
        BDSequences cible = new BDSequences();

        if (args.length == 2) { //analyse des paramètres
            try {
                BDS.lier(args[0]);
                cible.lier(args[1]);
            } catch (IOException iox) {
                throw new IllegalArgumentException("Usage : AlignementSeq <chemin BD> <chemin cible>");
            }
        }
        if (BDS.estVide() || cible.estVide())
            throw new IllegalArgumentException("Usage : AlignementSeq <chemin BD> <chemin cible>");

        for (int i = 2; i < 300; i= i+10) {
            nbThreads = i;
            nbThreadsTr = 1;
            départ = System.nanoTime();
            AlignementSeq.AMonoLindaParallel(BDS, cible, 0, nbThreads, nbThreadsTr, lindaShared);
            fin = System.nanoTime();
            System.out.println((fin - départ) / 1_000);
            lindaCtrl = new linda.shm.CentralizedLinda();
        }
        /*
        départ = System.nanoTime();
        résultat =
                AlignementSeq.AMonoLindaParallel(BDS,cible,0,nbThreads,nbThreadsTr,lindaShared);
        fin = System.nanoTime();
        System.out.println("test linda shared: durée = "+ (fin-départ) /1_000+
                "µs -> résultat : " + résultat);
                */
    }
}
