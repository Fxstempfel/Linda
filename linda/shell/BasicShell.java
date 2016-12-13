package linda.shell;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import linda.Linda;
import linda.Tuple;

public class BasicShell {
	public static void main(String[] args){
		//Repertoir des scripts
		final String pathToScripts = "linda/script/";
		//Instanciation d'un Linda
		final Linda linda = new linda.shm.CentralizedLinda();

		//Instanciation du scanner et du token pour parser les entrée de l'utilisateur
		Scanner sc = new Scanner(System.in);
		StringTokenizer stk = new StringTokenizer("start");
		String token = stk.nextToken();

		//Creation d'une hashmap pour stocker des tuples
		HashMap<String,Tuple> hm = new HashMap<String,Tuple>();
		hm.put("t1", new Tuple(5,6));

		System.out.println("Starting Linda's shell");

		//On sort du shell quand l'utilisateur tape 'exit'
		while(!token.equals("exit")) {
			while(!stk.hasMoreTokens()){
				System.out.print(">>");
				//On prend la saisie de l'utilisateur
				stk = new StringTokenizer(sc.nextLine());
			}
			//On va regarder toute la saisie de l'utilisateur
			token = stk.nextToken();
			//Traitement d'un write
			if(token.equals("write")){
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "write Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					linda.write(t);
					System.out.println("Tuple : " + t +" has been successfully written");
					break;

					//Traitement d'un "write t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						linda.write(hm.get(token));
						System.out.println("Tuple : " + hm.get(token) +" has been successfully written");

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;
				}

				//Traitement d'un read	
			} else if(token.equals("read")) {
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "read Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					Tuple res = linda.read(t);
					System.out.println("Result : " + res);
					break;
					//Traitement d'un "read t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						Tuple resd = linda.read(hm.get(token));
						System.out.println("Result : " + resd);

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;
				}
				//Traitement d'un take
			} else if (token.equals("take")) {
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "take Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					Tuple res = linda.take(t);
					System.out.println("Result : " + res);
					break;
					//Traitement d'un "take t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						Tuple resd = linda.take(hm.get(token));
						System.out.println("Result : " + resd);

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;
				}
				//Traitement d'un tryTake
			} else if (token.equals("tryTake")) {
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "tryTake Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					Tuple res = linda.tryTake(t);
					System.out.println("Result : " + res);
					break;
					//Traitement d'un "tryTake t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						Tuple resd = linda.tryTake(hm.get(token));
						System.out.println("Result : " + resd);

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;
				}
				//Traitement d'un tryRead
			} else if (token.equals("tryRead")) {
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "tryRead Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					Tuple res = linda.tryRead(t);
					System.out.println("Result : " + res);
					break;
					//Traitement d'un "tryRead t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						Tuple resd = linda.tryRead(hm.get(token));
						System.out.println("Result : " + resd);

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;
				}
				//Traitement d'un takeAll
			} else if (token.equals("takeAll")) {
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "takeAll Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					Collection<Tuple> res = linda.takeAll(t);
					System.out.println("Result : " + res);
					break;
					//Traitement d'un "takeAll t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						Collection<Tuple> resd = linda.takeAll(hm.get(token));
						System.out.println("Result : " + resd);

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;

				}
			} else if (token.equals("readAll")) {
				token = stk.nextToken();
				switch (token) {
				//Traitement d'un "readAll Tuple [ ... ]"
				case  "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					Tuple t = Tuple.valueOf(arg_tup);
					Collection<Tuple> res = linda.readAll(t);
					System.out.println("Result : " + res);
					break;
					//Traitement d'un "readAll t1" avec t1 un tuple initialisé avant
				default : 
					if(hm.containsKey(token)){
						Collection<Tuple> resd = linda.readAll(hm.get(token));
						System.out.println("Result : " + resd);

					} else {
						System.out.println("The tuple" + token + "doesn't exist");
					}
					break;

				}
				//Traitement du new
			} else if (token.equals("new")) {
				String name = stk.nextToken();
				token = stk.nextToken();
				switch(token) {
				case "Tuple" :
					String arg_tup = stk.nextToken();
					while(stk.hasMoreTokens()) {
						arg_tup += " " + stk.nextToken();
					}
					System.out.println(arg_tup);
					Tuple t = Tuple.valueOf(arg_tup);
					hm.put(name, t);
					System.out.println("Tuple " + name + " with value " + t + " added to local memory");
					break;
				default :
					System.out.println("Wrong command");
					System.out.println("new tupleName Tuple [ ... ]");
					System.out.println("example : new t1 Tuple [ 5 10 ]");
				}
				// gestion des scripts
			}else if (token.equals("exec")) {
				while(stk.hasMoreTokens()) {
					token = stk.nextToken();
					if(token.contains(".ld")) {
						final String scriptname = token;
						new Thread() {public void run() {
							try {
								BasicLindaScriptReader bs = new BasicLindaScriptReader(pathToScripts + scriptname, linda);
								try {
									bs.exec();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("Make sur your script is in " + pathToScripts + " and end by .ld" );
							}
						}
						}.start();
					}
				}

			} else if (token.equals("debug")) {
				linda.debug("");
			}else if (token.equals("exit")) {
				System.out.println("Stopping Linda");
				break;
			} else {
				System.out.println("Wrong command");
				System.out.println("Existing commands are : ");
				System.out.println("new t1 Tuple [ ... ] create a new Tuple called t1");
				System.out.println("write Tuple [ ... ] || write t1");
				System.out.println("read Tuple [ ... ] || read t1");
				System.out.println("tryRead Tuple [ ... ] || tryRead t1");
				System.out.println("tryTake Tuple [ ... ] || tryTake t1");
				System.out.println("readAll Tuple [ ... ] || readAll t1");
				System.out.println("takeAll Typle [ ... ] || takeAll t1");
				System.out.println("exit to leave");
				while(stk.hasMoreTokens()) {
					stk.nextToken();
				}

			}
		}
		sc.close();
	}
}


