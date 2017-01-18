package linda.multiserver;

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
import java.util.List;

/**
 * Created by tboissin on 18/01/17.
 */
public class TestLindaMultiserver {

    public static void main(String[] args){

        System.out.println("Creating all servers...\n");
        List<LindaMultiServer> serverList = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            try {
                LindaMultiServer lms = new LindaMultiServer(7000+2*i);
                for (LindaMultiServer other: serverList){
                    try {
                        other.addLindaClient(lms.getClient());
                        lms.addLindaClient(other.getClient());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
                serverList.add(lms);
                System.out.println("added server" +i +" to the list");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nAll server created\n");
        try {
            //register de chaque serveur

            System.out.println("\nRegistering all server on port 7500...\n");
            String URL;
            Registry reg = LocateRegistry.createRegistry(7500);
            for (int i = 0; i < serverList.size(); i++) {
                URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 7500 + "/multiserv"+i;
                Naming.rebind(URL, serverList.get(i));
                System.out.println("successfully registered server"+i);
            }


            System.out.println("\nAll server registred\n");



            //coté client connecté au serv 2
            System.out.println("creating client connected to serv2");
            URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 7500 + "/multiserv"+2;
            Linda linda = new LindaClient(URL);
            System.out.println("client writesTuple to serv2...");
            linda.write(new Tuple("test"));

            System.out.println("new Linda Cluster state is :");
            linda.debug("debug:>>");

            //coté client2 connecté au serv 4
            System.out.println("creating client connected to serv4");
            URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 7500 + "/multiserv"+4;
            linda = new LindaClient(URL);
            System.out.println("client reads Tuple from serv4...");
            System.out.print("tuple read : ");
            System.out.println(linda.read(new Tuple(String.class)).toString());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
