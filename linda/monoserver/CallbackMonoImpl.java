package linda.monoserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CallbackMonoImpl extends UnicastRemoteObject implements CallbackMono {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CallbackMonoImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private linda.Callback callback;
	@Override
	public void register(linda.server.CallbackClient callback) throws RemoteException {
		this.callback = callback.getCallback();

	}
	
	public Object getCallback() throws RemoteException {
		return this.callback;
	}

	@Override
	public void sendObject(Object obj) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
