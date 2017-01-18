package linda.multiserver;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.monoserver.LindaServer;
import linda.server.CallbackClient;
import linda.server.LindaClient;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by thibaut on 17/01/17.
 */
public class LindaMultiServer extends UnicastRemoteObject implements linda.monoserver.ILindaServer{

    private int port = 5555;
    private List<LindaClient> lindaSpace = new ArrayList<LindaClient>();
    private Map<Tuple, Queue<CallbackClient>> pendingReads;
    private Map<Tuple, Queue<CallbackClient>> pendingTakes;
    private int nbPendingTakes;
    private int nbPendingReads;
    private int nbServers;
    private boolean isReading;
    private LindaServer node;


    public LindaMultiServer(int port) throws RemoteException{
        this.port = port;
        this.nbPendingTakes = 0;
        this.nbPendingReads = 0;
        this.nbServers = 1;
        this.isReading = false;
        this.pendingReads = new HashMap<Tuple, Queue<CallbackClient>>();
        this.pendingTakes = new HashMap<Tuple, Queue<CallbackClient>>();
        this.node = new LindaServer();
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            Registry registryCallbacks = LocateRegistry.createRegistry(port+1);
            Naming.rebind( "//" + InetAddress.getLocalHost().getHostName() + ":" + port + "/monserveur", node);
            this.lindaSpace.add(new LindaClient(InetAddress.getLocalHost().getHostName(),port,port+1));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public LindaClient getClient() throws RemoteException, UnknownHostException {
        return this.lindaSpace.get(0);
    }

    public void addLindaClient(LindaClient lindaClient) throws RemoteException{
        this.lindaSpace.add(lindaClient);
        this.nbServers ++;
    }

    @Override
    public void write(Tuple t) throws RemoteException {
        synchronized(this){
            while(this.isReading) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            boolean takeMatched = false;
            for(Tuple template : pendingReads.keySet()){
                if(t.matches(template)) {
                    for(CallbackClient c : pendingReads.remove(template)) {
                        c.call(t.deepclone());
                    }
                }
            }
            for (Tuple template : pendingTakes.keySet()) {
                if (t.matches(template)) {
                    Queue<CallbackClient> queue = pendingTakes.get(template);
                    // Remove the head of the queue
                    queue.remove().call(t.deepclone());
                    if (queue.isEmpty()) {
                        // Remove the key if there is no more pending takes
                        pendingTakes.remove(template);
                    }
                    takeMatched = true;
                    break;
                }
            }
            if(!takeMatched){
                Random rand = new Random();
                int index = rand.nextInt(this.nbServers);
                this.lindaSpace.get(index).write(t);
            }
        }
    }


    private Tuple accessFromIndex(int index, Linda.eventMode me, Tuple template) throws RemoteException {
        int localIndex = index;
        Tuple t = null;
        boolean found = false;
        while(localIndex != -1 && !found) {
            switch (me) {
                case READ :
                    t = this.lindaSpace.get(localIndex).tryRead(template);
                    if(t != null) {
                        found = true;
                    }
                    break;
                case TAKE :
                    t = this.lindaSpace.get(localIndex).tryTake(template);
                    if(t != null) {
                        found = true;
                    }
                    break;
                default :
                    break;

            }
            localIndex = (localIndex + 1) % this.nbServers;
            if(localIndex == index) {
                localIndex = -1;
            }

        }
        return t;
    }


    @Override
    public Tuple take(Tuple template) throws RemoteException {
        LindaMultiServer.BlockingCallback callback = new LindaMultiServer.BlockingCallback();
        this.nbPendingTakes++;
        eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE,
                template, callback);
        synchronized (callback) {
            if (callback.result==null) {
                try {
                    callback.wait();
                } catch(Exception e) {
                }
            }
            this.nbPendingTakes--;
            return callback.result;
        }
    }

    @Override
    public Tuple read(Tuple template) throws RemoteException {
        LindaMultiServer.BlockingCallback callback = new LindaMultiServer.BlockingCallback();
        this.nbPendingReads++;
        eventRegister(Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE,
                template, callback);
        synchronized (callback) {
            if (callback.result==null) {
                try {
                    callback.wait();
                } catch (Exception e) {
                }
            }
            this.nbPendingReads--;
            return callback.result;
        }
    }

    @Override
    public Tuple tryTake(Tuple template) throws RemoteException {
        Tuple t =  null;
        synchronized(this) {
            this.isReading = true;
        }
        Random rand = new Random();
        int index = rand.nextInt(this.nbServers);
        t = accessFromIndex(index, Linda.eventMode.TAKE,template);
        synchronized(this) {
            this.isReading = false;
            this.notifyAll();
        }
        return t;
    }

    @Override
    public Tuple tryRead(Tuple template) throws RemoteException {
        Tuple t =  null;
        synchronized(this) {
            this.isReading = true;
        }
        Random rand = new Random();
        int index = rand.nextInt(this.nbServers);
        t = accessFromIndex(index, Linda.eventMode.READ,template);
        synchronized(this) {
            this.isReading = false;
            this.notifyAll();
        }
        return t;
    }


    private Collection<Tuple> accessFromIndexCollec(Linda.eventMode me, Tuple template) {
        int localIndex = 0;
        Collection<Tuple> t  = new ArrayList<Tuple>();
        while(localIndex != -1) {
            switch (me) {
                case READ :
                    t.addAll(this.lindaSpace.get(localIndex).readAll(template));
                    break;
                case TAKE :
                    t.addAll(this.lindaSpace.get(localIndex).takeAll(template));
                    break;
                default :
                    break;

            }
            localIndex = (localIndex + 1) % this.nbServers;
            if(localIndex == 0) {
                localIndex = -1;
            }

        }
        return t;
    }



    @Override
    public Collection<Tuple> takeAll(Tuple template) throws RemoteException{
        Collection<Tuple> t = accessFromIndexCollec(Linda.eventMode.TAKE, template);
        return t;
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) throws RemoteException{
        Collection<Tuple> t = accessFromIndexCollec(Linda.eventMode.READ, template);
        return t;
    }

    @Override
    public void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, String URL_Callback) throws RemoteException {
        try {
            CallbackClient rcallback;
            rcallback = (CallbackClient) Naming.lookup(URL_Callback);
            eventRegister(mode, timing, template,rcallback);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void eventRegister(Linda.eventMode mode, Linda.eventTiming timing, Tuple template, linda.server.CallbackClient rcallback)
            throws RemoteException {
        Tuple t=null;
        boolean tupleFound = false;
        synchronized (this) {
                if (timing == Linda.eventTiming.IMMEDIATE) {
                    Random rand = new Random();
                    int index = rand.nextInt(this.nbServers);
                    t = accessFromIndex(index, mode, template);
                    if (t == null) {
                        // If the tuple was not found in the tupleSpace, add it to
                        // the pending operations
                        if (mode == Linda.eventMode.TAKE) {
                            addCallbackToQueue(pendingTakes, template, rcallback);
                        } else {
                            addCallbackToQueue(pendingReads, template, rcallback);
                        }
                    } else {
                        tupleFound = true;
                    }
                } else {
                    // timing = FUTURE
                    // Add the callback to the pending operations
                    if (mode == Linda.eventMode.TAKE) {
                        addCallbackToQueue(pendingTakes, template, rcallback);
                    } else {
                        // mode = READ
                        addCallbackToQueue(pendingReads, template, rcallback);
                    }

                }
                // If the tuple was found in the tupleSpace, run the callback
                if (tupleFound) {
                    rcallback.call(t);
                }
        }
    }

    private void addCallbackToQueue(Map<Tuple, Queue<CallbackClient>> map, Tuple template, CallbackClient callback) {
        // If the key already exists, add the callback to the
        // corresponding queue
        if (map.containsKey(template)) {
            map.get(template).add(callback);
        } else {
            // Else, create a new queue
            Queue<CallbackClient> newQueue = new LinkedList<CallbackClient>();
            newQueue.add(callback);
            map.put(template, newQueue);
        }
    }

    @Override
    public void debug(String prefix) throws RemoteException{
        int i=1;
        for(Linda l : this.lindaSpace) {
            l.debug("Linda Fragment number " + i );
            i++;
        }


    }


    private class BlockingCallback implements CallbackClient {

        Tuple result;

        private BlockingCallback() {}

        public void call(Tuple t) {
            synchronized (this) {
                result = t;
                notify();
            }
        }

        @Override
        public Callback getCallback() throws RemoteException {
            return null;
        }
    }
}
