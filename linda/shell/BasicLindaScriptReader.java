package linda.shell;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import linda.Linda;
import linda.Tuple;

//Un script Linda sera ecrit avec une syntaxe "assembleur"
//Ex : new t1 [ ... ]	crée un nouveau tuple t1
//	   new t2 [ ... ]	crée un nouvea tuple t2
//	   write t1			écrit t1 dans l'espace des tuples de linda
//	   read t3 t1  		place dans t3 la valeur de read(t1)
//		...

public class BasicLindaScriptReader {
	String filename;
	FileReader input;
	BufferedReader buff;
	String myLine;
	String[] currentLine;
	Linda linda;
	HashMap<String, Tuple> hm = new HashMap<String,Tuple>();
	HashMap<String, Collection<Tuple>> hml = new HashMap<String,Collection<Tuple>>();

	public BasicLindaScriptReader(String filename, Linda linda) throws FileNotFoundException {
		this.input = new FileReader(filename);
		this.buff = new BufferedReader(input);
		this.linda = linda;
		this.filename = filename;
	}


	public void exec() throws Exception {
		while((myLine = buff.readLine()) != null) {
			currentLine = myLine.split(" ");
			if(currentLine[0].equals("new")) {
				String name = currentLine[1];
				String arg_tup = "";
				for(int i = 2; i<currentLine.length;i++) {
					arg_tup += " " + currentLine[i];
				}
				System.out.println(arg_tup);
				Tuple t = Tuple.valueOf(arg_tup);
				hm.put(name, t);
				System.out.println("Tuple " + name + " with value " + t + " added to local memory");
			} else if(currentLine[0].equals("write")) {
				if(hm.containsKey(currentLine[1])) {
					linda.write(hm.get(currentLine[1]));
					System.out.println("Tuple " + hm.get(currentLine[1]) + " successfully written");
				} else {
					throw new Exception(currentLine[1] + "was never created !");
				}
			} else if (currentLine[0].equals("read")) {
				String name = currentLine[1];
				Tuple t = linda.read(hm.get(currentLine[2]));
				hm.put(name, t);
			} else if (currentLine[0].equals("take")) {
				String name = currentLine[1];
				Tuple t = linda.take(hm.get(currentLine[2]));
				hm.put(name, t);
			} else if (currentLine[0].equals("tryTake")) {
				String name = currentLine[1];
				Tuple t = linda.tryTake(hm.get(currentLine[2]));
				if(t == null || !t.isEmpty()) {
					hm.put(name, t);
				}
			} else if (currentLine[0].equals("tryRead")) {
				String name = currentLine[1];
				Tuple t = linda.tryRead(hm.get(currentLine[2]));
				if(t == null || !t.isEmpty()) {
					hm.put(name, t);
				}
			}
			else if (currentLine[0].equals("readAll")) {
				String name = currentLine[1];
				Collection<Tuple> tl = linda.readAll(hm.get(currentLine[2]));
				hml.put(name,tl);
			} else if (currentLine[0].equals("takeAll")) {
				String name = currentLine[1];
				Collection<Tuple> tl = linda.takeAll(hm.get(currentLine[2]));
				hml.put(name,tl);
			} else if (currentLine[0].equals("debug")) {
				linda.debug("  ");
			} else if (currentLine[0].equals("print")) {
				if (currentLine.length == 1) {
					System.out.println();
				}
				else if ( hm.containsKey(currentLine[1])) {
					System.out.println(currentLine[1] + " = " + hm.get(currentLine[1]));
				} 
				else if (hml.containsKey(currentLine[1])) {
					System.out.println(currentLine[1] + " = " + hml.get(currentLine[1]));
				}
				else {
					printThisLine(myLine);
				}
			}
				else if (currentLine[0].equals("//")) { // comment
					continue;
				} 
				else if (myLine.equals("")) { // empty line 
					continue;
				} 
				else {
					System.out.println("ERROR IN FILE : " + this.filename);
					System.out.println(" Line : " +  this.myLine);
					System.exit(1);
				}

			}
		}

		private void printThisLine(String l) {
			String[] splitLine = myLine.split(" ");
			splitLine = Arrays.copyOfRange(splitLine, 1, splitLine.length);
			String line = String.join(" ", splitLine);
			System.out.println(line);
		}

	}
