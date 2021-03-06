// java Server <srvc_port> <mcast_addr> <mcast_port>
// Loop "forever" waiting for client requests, processing and replying to them.
// Each time it processes a client request:
// Server: <oper> <opnd>*

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Timer;

import static java.lang.Integer.parseInt;

public class Server {

    DatagramSocket socket;
    HashMap<String, String> DNSTable;
    Advertise ads;

    public Server(int srvcPort, InetAddress MCAddress, int MCPort) throws Exception {
        this.socket = new DatagramSocket(srvcPort);
        this.DNSTable = new HashMap<>();

        InetAddress srvcAddress = InetAddress.getLocalHost();
        this.ads = new Advertise(srvcPort, srvcAddress, MCPort, MCAddress);
        new Timer().scheduleAtFixedRate(this.ads, 0, 1000);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
            System.exit(1);
        }
        Server s = new Server(parseInt(args[0]), InetAddress.getByName(args[1]), parseInt(args[2]));
        s.handleRequests();
    }

    public void handleRequests() throws Exception {

        System.out.println("Server ON!");

        while (true) {
            try {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                this.socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());


                String reply;
                if (data != null) {
                    reply = this.processRequest(data);
                    System.out.println(data + " *:: " + reply);

                    packet = new DatagramPacket(reply.getBytes(), reply.getBytes().length, packet.getAddress(), packet.getPort());
                    this.socket.send(packet);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String processRequest(String data) {
        String[] args = data.split(" ");
        String reply;

        switch(args[0]) {
            case "REGISTER":
                reply = String.valueOf(this.register(args[1], args[2]));
                break;
            case "LOOKUP":
                reply = this.lookup(args[1]);
                break;
            default:
                reply = "ERROR";
                break;
        }

        return reply;
    }

    private int register(String DNS, String IP) {
        if (DNSTable.containsKey(DNS))
            return -1; // already registered
        else {
            DNSTable.put(DNS, IP);
            return DNSTable.size();
        }
    }

    private String lookup(String DNS) {
        return DNSTable.getOrDefault(DNS, "NOT_FOUND");
    }
}