package linda.server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class LindaClient implements Linda {

	private linda.monoserver.ILindaServer lindaRemote;
	private String URL ;
	public LindaClient() {
		// TODO Auto-generated constructor stub
		try {
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 5556 + "/monclient";
			System.out.println(URL);
			lindaRemote = (linda.monoserver.ILindaServer) Naming.lookup("//tits-workstation:5555/monserveur");
		} catch (MalformedURLException | RemoteException | NotBoundException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LindaClient(String URL_Serveur) {
		// TODO Auto-generated constructor stub
		try {
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 5556 + "/monclient";
			System.out.println(URL);
			lindaRemote = (linda.monoserver.ILindaServer) Naming.lookup(URL_Serveur);
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
			CallbackClient rcallback = new CallbackClientImpl(callback);
			Naming.rebind(URL, rcallback);
			System.out.println("callback binded to " + URL);
			
			
			lindaRemote.eventRegister(mode, timing, template,URL);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
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
