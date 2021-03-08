// java Client <host_name> <remote_object_name> <oper> <opnd>*

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    String host;
    String remoteObjectName;

    String oper;
    String dnsName;
    String ipAddress;

    private Client(String[] args) {
        this.host = args[0];
        this.remoteObjectName = args[1];
        this.oper = args[2];
        this.dnsName = args[3];
        if (this.oper.equals("REGISTER"))
            this.ipAddress = args[4];
    }

    public static void main(String[] args) {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> REGISTER <DNS name> <IP address>");
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> LOOKUP <DNS name>");
            System.exit(1);
        }
        else if ((args[2].equals("REGISTER") && args.length != 5) ||
                (args[2].equals("LOOKUP") && args.length != 4) ||
                (!args[2].equals("REGISTER") && !args[2].equals("LOOKUP"))) {
            System.out.println("Usage: java Client <host> <port> REGISTER <DNS name> <IP address>");
            System.out.println("Usage: java Client <host> <port> LOOKUP <DNS name>");
            System.exit(1);
        }

        Client c = new Client(args);

        try {
            Registry registry = LocateRegistry.getRegistry(c.host);
            RMI stub = (RMI) registry.lookup(c.remoteObjectName);

            String reply = "";

            switch (c.oper) {
                case "REGISTER":
                    reply = String.valueOf(stub.register(c.dnsName, c.ipAddress));
                    System.out.println("REGISTER " + c.dnsName + " " + c.ipAddress + " : " + reply);
                    break;
                case "LOOKUP":
                    reply = stub.lookup(c.dnsName);
                    System.out.println("LOOKUP " + c.dnsName + " : " + reply);
                    break;
            }

        } catch (Exception e) {
            System.err.println("remoteObjectName not found...");
        }
    }
}