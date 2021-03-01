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

public class Server extends Thread {

    DatagramSocket socket;
    int port;
    HashMap<String, String> DNSTable;

    public Server(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.port = port;
        this.DNSTable = new HashMap<>();
        System.out.println("Socket: " + this.socket);
        System.out.println("Port: " + this.port);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port number>");
            System.exit(1);
        }
        Server s = new Server(parseInt(args[0]));
        s.handleRequest();
    }

    public void handleRequest() {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, 256);
        System.out.println("Server ON!");

        while (true) {
            try {
                this.socket.receive(packet);
                String res = this.processRequest(buffer);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                buffer = res.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, address, port);
                this.socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String processRequest(byte[] buffer) {
        String msg = new String(buffer, 0, buffer.length);
        String words[] = msg.split(" ");

        System.out.println("Server: " + words[0]);
        for (int i = 1; i < words.length; i++) {
            System.out.println(" " + words[i]);
        }
        System.out.println("");

        String ret = "";

        if (words[0].equals("REGISTER") && words.length == 3) ret = String.valueOf(this.register(words[1], words[2]));
        else if (words[0].equals("LOOKUP") && words.length == 2) ret = words[1] + " " + this.lookup(words[1]);
        return ret;
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
        if (DNSTable.containsKey(DNS))
            return DNSTable.get(DNS);
        else
            return "Not registered...";
    }
}