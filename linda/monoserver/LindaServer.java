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
import linda.Linda;
import linda.Tuple;

public class LindaServer extends UnicastRemoteObject implements linda.monoserver.ILindaServer {

	public LindaServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Linda tupleSpace = new linda.shm.CentralizedLinda();


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


	public void eventRegister(linda.Linda.eventMode mode, linda.Linda.eventTiming timing, Tuple template,String URL_Callback)
			throws RemoteException {
		// TODO Auto-generated method stub
		linda.server.CallbackClient rcallback;
		try {
			rcallback = (linda.server.CallbackClient) Naming.lookup(URL_Callback);
			linda.Callback callbacktest = new Callback() {

				@Override
				public void call(Tuple t) {
					try {
						rcallback.call(t);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}; 
			
			
			tupleSpace.eventRegister(mode, timing, template, callbacktest);
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
			Registry registryLinda = LocateRegistry.createRegistry(5555);
			Registry registryCallback = LocateRegistry.createRegistry(5556);
			LindaServer linda = new LindaServer();
			String URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 5555 + "/monserveur";
			System.out.println(URL);
			Naming.rebind(URL, linda);

		} catch (Exception exc) {
			System.out.println("Probleme serveur : " + exc);
		}
	}
}

