// java Client <mcast_addr> <mcast_port> <oper> <opnd> *

import java.io.IOException;
import java.net.*;

import static java.lang.Integer.parseInt;

public class Client {
    MulticastSocket mcastSocket;
    DatagramSocket socket;

    InetAddress mcastAddress;
    int mcastPort;
    String oper;
    String dnsName;
    String ipAddress;

    public Client(String args[]) throws Exception {
        this.mcastAddress = InetAddress.getByName(args[0]);
        this.mcastPort = parseInt(args[1]);
        this.oper = args[2];
        this.dnsName = args[3];
        if (this.oper.equals("REGISTER"))
            this.ipAddress = args[4];

        this.socket = new DatagramSocket();
        this.mcastSocket = new MulticastSocket(this.mcastPort);
        this.mcastSocket.joinGroup(this.mcastAddress);
    }

    public static void main(String[] args) throws Exception {
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
        InetSocketAddress srvc = c.awaitServiceLocation();

        String request = c.buildRequest();
        c.sendRequest(request, srvc);
    }

    public String buildRequest() {
        String request = "";
        switch (this.oper) {
            case "REGISTER":
                request = this.oper + " " + this.dnsName + " " + this.ipAddress;
                break;
            case "LOOKUP":
                request = this.oper + " " + this.dnsName;
                break;
        }
        return request;
    }

    public void sendRequest(String request, InetSocketAddress srvc) throws IOException {
        DatagramPacket packet = new DatagramPacket(request.getBytes(), request.length(), srvc.getAddress(), srvc.getPort());
        this.socket.send(packet);

        byte[] buffer = new byte[256];
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                this.socket.receive(reply);

                String data = new String(reply.getData(), 0, reply.getLength());
                if (data != null) {
                    System.out.println("Client: " + request + " : " + data);
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public InetSocketAddress awaitServiceLocation() throws UnknownHostException {
        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        String data;

        while(true){
            try{
                this.mcastSocket.receive(packet);
                data = new String(packet.getData(),0,packet.getLength());
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }

            if(data != null) {
                String[] args = data.split(" ");

                InetAddress srvcAddress = InetAddress.getByName(args[0]);
                int srvcPort = Integer.parseInt(args[1]);

                System.out.println("multicast: " + this.mcastAddress.getHostAddress() + " " +
                        this.mcastPort + ": " + srvcAddress.getHostAddress() + " " + srvcPort);

                InetSocketAddress service = new InetSocketAddress(srvcAddress, srvcPort);
                return service;
            }
        }
    }
}