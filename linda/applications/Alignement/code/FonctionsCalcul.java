package linda.applications.Alignement.code;

/**
 * Created by thibaut on 08/01/17.
 */
public class FonctionsCalcul {

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
}
