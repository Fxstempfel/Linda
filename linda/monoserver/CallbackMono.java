package linda.monoserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackMono extends Remote {
	
	public Object getCallback() throws RemoteException ;
	public void register(linda.server.CallbackClient callback) throws RemoteException;
	public void sendObject(Object obj) throws RemoteException;

}
