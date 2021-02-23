import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class Server extends Thread{

    private DatagramSocket socket;

    private int port;

    private boolean error = false;

    private Map<String, String> dnsServer = new HashMap<String, String>();

    public Server(String args[]) throws IOException{
        this("My Server", args);
    }

    public Server(String name, String args[]) throws IOException {
        super(name);

        this.processInput(args);
        if(!this.error)
            this.socket = new DatagramSocket(this.port);
    }

    private void processInput(String args[]) {
        if(args.length != 1) {
            this.error = true;
            return;
        }

        this.port = parseInt(args[0]);
    }

    public void run() {

        if(this.error) {
            System.out.println("Invalid Format!\nFormat: java Server <port number>");
            return;
        }

        while(true) {
            try {
                // ##################################
                //          READS REQUEST
                // ##################################

                // creates an array of 256 bytes
                byte[] buf = new byte[256];

                DatagramPacket data = new DatagramPacket(buf, buf.length);
                this.socket.receive(data); // it blocks until it gets data

                // ##################################
                //          PROCESS REQUEST
                // ##################################

                String response = this.processRequest(buf);

                // ##################################
                //          REPLY REQUEST
                // ##################################

                // do something with the data
                InetAddress adress = data.getAddress(); // gets the IP adress
                int port = data.getPort(); // gets the port number

                buf = response.getBytes();
                data = new DatagramPacket(buf, buf.length, adress, port); // creates a datagram to send with the content in buf
                this.socket.send(data); // sends the socket to host with IP -> adress, and port -> port

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processRequest(byte[] buf) {
        String clientMessage = new String(buf, 0, this.getFirstNullByteIndex(buf), StandardCharsets.UTF_8);
        String words[] = clientMessage.split(" ");

        System.out.print("Server: " + words[0]);
        for(int i = 1; i < words.length; i++)
            System.out.print(" " + words[i]);
        System.out.println("");

        if(words[0].equals("REGISTER") && words.length == 3) return String.valueOf(this.register(words[1], words[2]));
        else if(words[0].equals("LOOKUP") && words.length == 2) return words[1] + " " + this.lookup(words[1]);
        else return "BAD REQUEST";
    }

    private int register(String dns, String ip) {
        if(dnsServer.containsKey(dns))
            return -1; // the name has already been registered
        else {
            dnsServer.put(dns, ip);
            return dnsServer.size(); // there are already m bindings in the service
        }
    }

    private String lookup(String dns) {
        if(dnsServer.containsKey(dns))
            return dnsServer.get(dns); // return the ip adress corresponding to this dns
        else
            return "NOT_FOUND"; // this dns name has not been registered yet
    }

    private int getFirstNullByteIndex(byte[] buf) {
        for(int i = 0; i < buf.length; i++) {
            if (buf[i] == 0) return i;
        }
        return -1;
    }
}
