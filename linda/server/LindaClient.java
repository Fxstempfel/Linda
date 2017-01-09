package linda.server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.monoserver.Linda.eventMode;
import linda.monoserver.Linda.eventTiming;

public class LindaClient implements Linda {

	private linda.monoserver.Linda lindaRemote;
	private Registry registry;
	private String URL ;
	private static linda.monoserver.CallbackMono server;
	public LindaClient() {
		// TODO Auto-generated constructor stub
		try {
			registry = LocateRegistry.createRegistry(5556);
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 5556 + "/monclient";
			System.out.println(URL);
			lindaRemote = (linda.monoserver.Linda) Naming.lookup("//tits-workstation:5555/monserveur");
		} catch (MalformedURLException | RemoteException | NotBoundException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void write(Tuple t) {
		// TODO Auto-generated method stub
		try {
			lindaRemote.write(t);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Tuple take(Tuple template) {
		// TODO Auto-generated method stub
		try {
			return lindaRemote.take(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple read(Tuple template) {
		// TODO Auto-generated method stub
		try {
			return lindaRemote.read(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		try {
			return lindaRemote.tryTake(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		try {
			return lindaRemote.tryRead(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		// TODO Auto-generated method stub
		try {
			return lindaRemote.takeAll(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		// TODO Auto-generated method stub
		try {
			return lindaRemote.readAll(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		// TODO Auto-generated method stub
		try {
			CallbackClientImpl rcallback = new CallbackClientImpl(callback);
				//server = (linda.monoserver.CallbackMono) Naming.lookup("//tits-workstation:5555/remoteCallback");
				//server.register(rcallback);
				this.registry.rebind(URL, rcallback);
				// TODO Auto-generated catch block
			if (mode == eventMode.TAKE) {
				if( timing == eventTiming.IMMEDIATE){
					lindaRemote.eventRegister(linda.Linda.eventMode.TAKE, linda.Linda.eventTiming.IMMEDIATE, template);
				} else {
					lindaRemote.eventRegister(linda.Linda.eventMode.TAKE, linda.Linda.eventTiming.FUTURE, template);
				}
			} else {
				if( timing == eventTiming.IMMEDIATE){
					lindaRemote.eventRegister(linda.Linda.eventMode.READ, linda.Linda.eventTiming.IMMEDIATE, template);
				} else {
					lindaRemote.eventRegister(linda.Linda.eventMode.READ, linda.Linda.eventTiming.FUTURE, template);
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	@Override
	public void debug(String prefix) {
		try {
			lindaRemote.debug(prefix);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
