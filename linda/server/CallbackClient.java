package linda.server;

import java.rmi.Remote;

import linda.Tuple;
/** Callback when a tuple appears.
 * @author philippe.queinnec@enseeiht.fr
*/
public interface CallbackClient extends Remote{

    /** Callback when a tuple appears. 
     * See Linda.eventRegister for details.
     * 
     * @param t the new tuple
     */
    void call(Tuple t) throws java.rmi.RemoteException;
    linda.Callback getCallback() throws java.rmi.RemoteException;
}
