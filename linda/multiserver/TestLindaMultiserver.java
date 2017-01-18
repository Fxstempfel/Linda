package linda.multiserver;

import linda.Linda;
import linda.Tuple;
import linda.server.LindaClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tboissin on 18/01/17.
 */
public class TestLindaMultiserver {

    public static void main(String[] args){
        List<LindaMultiServer> serverList = new ArrayList<>();
        for (int i = 0; i < 10; i++){
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
        System.out.println("fini!");
        try {
            String URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 7004 + "/monserveur";
            Linda linda = new LindaClient(URL);
            linda.write(new Tuple("lol"));
            linda.debug("debug:>>");
            URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + 7002 + "/monserveur";
            linda = new LindaClient(URL);
            System.out.println(linda.read(new Tuple(String.class)));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
