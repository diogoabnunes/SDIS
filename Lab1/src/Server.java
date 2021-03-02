// java Server <port number>
// Loop "forever" waiting for client requests, processing and replying to them.
// Each time it processes a client request:
// Server: <oper> <opnd>*

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class Server {

    DatagramSocket socket;
    int port;
    HashMap<String, String> DNSTable;

    public Server(int port) throws Exception {
        this.socket = new DatagramSocket(port);
        this.port = port;
        this.DNSTable = new HashMap<>();
        System.out.println("Socket: " + this.socket);
        System.out.println("Port: " + this.port);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port number>");
            System.exit(1);
        }
        Server s = new Server(parseInt(args[0]));
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
                    System.out.println("Server: " + data);
                    reply = this.processRequest(data);

                    packet = new DatagramPacket(reply.getBytes(), reply.length(), packet.getAddress(), packet.getPort());
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