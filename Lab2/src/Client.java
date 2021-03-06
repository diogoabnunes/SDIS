import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.util.*;
import java.io.IOException;

public class Client{
    MulticastSocket mcSocket;
    DatagramSocket socket;
    InetAddress mcAddress;
    int mcPort;
    String operation;
    String [] operands;

    public Client(String mcAddress, int mcPort, String oper, String[] opnd) throws IOException{
        this.mcSocket = new MulticastSocket(mcPort);
        this.socket = new DatagramSocket();
        this.mcAddress = InetAddress.getByName(mcAddress);
        mcSocket.joinGroup(this.mcAddress);

        this.mcPort = mcPort;
        this.operation = oper;
        this.operands = opnd;
    }

    public static void main(String args[]) throws IOException{
        if (args.length<4){
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd> *");
            System.exit(1);
        }

        int mcPort = Integer.parseInt(args[1]);
        String[] opnd = Arrays.copyOfRange(args, 3, args.length);

        Client client = new Client(args[0],mcPort,args[2],opnd);

        InetSocketAddress service = client.awaitServiceLocation();

        String request = client.buildRequest();
        client.sendRequest(request,service);
    }

    public String buildRequest(){
        String request = new String(this.operation).toUpperCase();

        switch(request){
            case "REGISTER":
                request = request + " " + this.operands[0] + " " + this.operands[1];
                break;
            case "LOOKUP":
                request = request + " " + this.operands[0];
                break;
            default:
                System.out.println("Invalid Operation");
                System.exit(3);
        }

        return request;
    }

    public void sendRequest(String request, InetSocketAddress service) throws IOException{
        byte[] buf = new byte[1024];
        DatagramPacket reply = new DatagramPacket(buf,1024);
        DatagramPacket packet = new DatagramPacket(request.getBytes(),request.length(),service.getAddress(),service.getPort());
        this.socket.send(packet);

        while(true){
            try{
                socket.receive(reply);
            } catch(Exception e){
                System.out.println(e);
            }

            String data = new String(reply.getData(),0,reply.getLength());
            if(data != null && !data.trim().isEmpty()){
                System.out.println("Client: " + request + " : " + data);
                break;
            }
        }

        this.socket.close();
    }


    public InetSocketAddress awaitServiceLocation() throws IOException{
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        String data;
        while(true){
            try{
                this.mcSocket.receive(packet);
                data = new String(packet.getData(),0,packet.getLength());
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }

            if(data != null && !data.trim().isEmpty()){
                String[] args = data.split(" ");
                InetAddress serviceIP = InetAddress.getByName(args[0]);
                int servicePort = Integer.parseInt(args[1]);

                System.out.println("multicast: " + this.mcAddress.getHostAddress() + " " + this.mcPort + ": " + serviceIP.getHostAddress() + " " + servicePort);

                InetSocketAddress service = new InetSocketAddress(serviceIP,servicePort);
                return service;
            }
        }
    }
}