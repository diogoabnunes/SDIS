// java Server <remote_object_name>

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server implements RMI {
    String remoteObjectName;
    HashMap<String, String> DNSTable;

    public Server(String remoteObjectName) {
        this.remoteObjectName = remoteObjectName;
        this.DNSTable = new HashMap<>();
    }

    public static void main(String[] args) {

        try {
            Server obj = new Server(args[0]);
            RMI stub = (RMI) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(obj.remoteObjectName, stub);

            System.err.println("Server ON!");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public int register(String DNS, String IP) {
        int ret;
        if (DNSTable.containsKey(DNS))
            ret = -1; // already registered
        else {
            DNSTable.put(DNS, IP);
            ret = DNSTable.size();
        }
        System.out.println("REGISTER " + DNS + " " + IP + " : " + ret);
        return ret;
    }

    public String lookup(String DNS) {
        String ret = "";
        if (DNSTable.containsKey(DNS)) {
            ret = DNSTable.get(DNS);
        }
        else {
            ret = "NOT_FOUND";
        }
        System.out.println("LOOKUP " + DNS + " : " + ret);
        return ret;
    }

}