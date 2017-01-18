package linda.multiserver;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.LindaClient;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by thibaut on 18/01/17.
 */
public class LindaCluster implements Linda{

    private List<LindaMultiServer> serverList;
    private List<LindaClient> clientsList;
    private int nbNodes;

    public LindaCluster(int nbNodes) {
        try {
            this.nbNodes = nbNodes;
            serverList = new ArrayList<>();
            clientsList = new ArrayList<>();
            Registry reg = LocateRegistry.createRegistry(7500);
            Registry registryCallback = LocateRegistry.createRegistry(5556);
            for (int i = 0; i < nbNodes; i++) {
                try {
                    LindaMultiServer lms = new LindaMultiServer(7000 + 2 * i);
                    for (LindaMultiServer other : serverList) {
                        try {
                            other.addLindaClient(lms.getClient());
                            lms.addLindaClient(other.getClient());
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                    serverList.add(lms);
                    Naming.rebind("//" + InetAddress.getLocalHost().getHostName() + ":" + 7500 + "/multiserv" + i, serverList.get(i));
                    clientsList.add(new LindaClient("//" + InetAddress.getLocalHost().getHostName() + ":" + 7500 + "/multiserv"+i));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    private LindaClient pickRandomClient(){
        Random rand = new Random();
        return clientsList.get(rand.nextInt(nbNodes));
    }

    @Override
    public void write(Tuple t) {
        pickRandomClient().write(t);
    }

    @Override
    public Tuple take(Tuple template) {
        return pickRandomClient().take(template);
    }

    @Override
    public Tuple read(Tuple template) {
        return pickRandomClient().take(template);
    }

    @Override
    public Tuple tryTake(Tuple template) {
        return pickRandomClient().tryTake(template);
    }

    @Override
    public Tuple tryRead(Tuple template) {
        return pickRandomClient().tryRead(template);
    }

    @Override
    public Collection<Tuple> takeAll(Tuple template) {
        return pickRandomClient().takeAll(template);
    }

    @Override
    public Collection<Tuple> readAll(Tuple template) {
        return pickRandomClient().readAll(template);
    }

    @Override
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
        pickRandomClient().eventRegister(mode, timing,template, callback);
    }

    @Override
    public void debug(String prefix) {
        pickRandomClient().debug("");
    }
}
