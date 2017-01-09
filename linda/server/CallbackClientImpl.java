package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.Tuple;

public class CallbackClientImpl extends UnicastRemoteObject implements CallbackClient  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private linda.Callback callback;
	
	protected CallbackClientImpl(linda.Callback callback) throws RemoteException {
		this.callback = callback;
		// TODO Auto-generated constructor stub
	}
	

	public void call(Tuple t) throws java.rmi.RemoteException {
		callback.call(t);

	}


	@Override
	public linda.Callback getCallback() throws RemoteException {
		return this.callback;
	}

}
