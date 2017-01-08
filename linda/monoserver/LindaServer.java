package linda.monoserver;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Callback;
import linda.Tuple;

public class LindaServer extends UnicastRemoteObject implements linda.monoserver.Linda {


	protected LindaServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private linda.shm.SharedLinda tupleSpace = new linda.shm.SharedLinda(1);


	public void write(Tuple t) throws RemoteException {
		// TODO Auto-generated method stub
		tupleSpace.write(t);
	}

	public Tuple take(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return tupleSpace.take(template);
	}

	public Tuple read(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return tupleSpace.read(template);
	}

	public Tuple tryTake(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return tupleSpace.tryTake(template);
	}

	public Tuple tryRead(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return tupleSpace.tryRead(template);
	}

	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return tupleSpace.takeAll(template);
	}

	public Collection<Tuple> readAll(Tuple template) throws RemoteException {
		// TODO Auto-generated method stub
		return tupleSpace.readAll(template);
	}

	public void eventRegister(linda.Linda.eventMode mode, linda.Linda.eventTiming timing, Tuple template)
			throws RemoteException {
		// TODO Auto-generated method stub
		// Ajouter un bind du callback sur un serveur de nom du coté client
		linda.server.CallbackClient rcallback;
		try {
			rcallback = (linda.server.CallbackClient) Naming.lookup("//tits-workstation:5556/monclient");

			if (mode == linda.Linda.eventMode.TAKE) {
				if( timing == linda.Linda.eventTiming.IMMEDIATE){
					tupleSpace.eventRegister(linda.Linda.eventMode.TAKE, linda.Linda.eventTiming.IMMEDIATE, template, rcallback.getCallback());
				} else {
					tupleSpace.eventRegister(linda.Linda.eventMode.TAKE, linda.Linda.eventTiming.FUTURE, template, rcallback.getCallback());
				}
			} else {
				if( timing == linda.Linda.eventTiming.IMMEDIATE){
					tupleSpace.eventRegister(linda.Linda.eventMode.READ, linda.Linda.eventTiming.IMMEDIATE, template, rcallback.getCallback());
				} else {
					tupleSpace.eventRegister(linda.Linda.eventMode.READ, linda.Linda.eventTiming.FUTURE, template, rcallback.getCallback());
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void debug(String prefix) throws RemoteException {
		// TODO Auto-generated method stub
		tupleSpace.debug(prefix);

	}

	public static void main(String[] args ) {
		try{
			Registry registry = LocateRegistry.createRegistry(5555);	
			LindaServer linda = new LindaServer();
			String URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 5555 + "/monserveur";
			System.out.println(URL);
			Naming.rebind(URL, linda);
			
		} catch (Exception exc) {
			System.out.println("Probleme serveur : " + exc);
		}
	}
}

