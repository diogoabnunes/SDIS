import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Client extends Thread{

    private MulticastSocket mcast_socket;
    private DatagramSocket srvc_socket;

    private String mcast_host;
    private int mcast_port;
    private String srvc_host;
    private int srvc_port;

    private String oper;
    private List<String> opnd = new ArrayList<>();

    private boolean error = false;

    public Client(String args[]) throws IOException {
        this("My Client", args);
    }

    public Client(String name, String args[]) throws IOException{
        super(name);

        this.processInput(args);
        if(!this.error)
            this.mcast_socket = new MulticastSocket(this.mcast_port);
    }

    private void processInput(String args[]) {

        int arraySize = args.length;

        boolean wrongSize = arraySize < 4 || arraySize > 5;
        if(wrongSize) {
            this.error = true;
            return;
        }

        boolean wrongOper = !"lookup".equals(args[2].toLowerCase()) && !"register".equals(args[2].toLowerCase());
        boolean correctLookup = arraySize == 4 && "lookup".equals(args[2].toLowerCase());
        boolean correctRegister = arraySize == 5 && "register".equals(args[2].toLowerCase());

        if(wrongSize || wrongOper || (correctLookup == false && correctRegister == false)) {
            this.error = true;
            return;
        }

        this.mcast_host = args[0];
        this.mcast_port = parseInt(args[1]);
        this.oper = args[2].toLowerCase();
        this.opnd.add(args[3]);
        if(arraySize == 5) this.opnd.add(args[4]);
    }

    public void run() {

        if(this.error) {
            System.out.println("Invalid Format!\nFormat: java Client <host> <port> <oper> <opnd>*");
            return;
        }

        this.getServiceSocket();

        try{
            byte[] buf = this.buildMessage();
            DatagramPacket data = new DatagramPacket(buf, buf.length, InetAddress.getByName(this.srvc_host), this.srvc_port);
            this.srvc_socket.send(data);

            byte[] buf1 = new byte[256];
            data = new DatagramPacket(buf1, buf1.length);
            this.srvc_socket.setSoTimeout(3000);
            this.srvc_socket.receive(data);

            this.outputMessage(new String(buf1, 0, this.getFirstNullByteIndex(buf1), StandardCharsets.UTF_8));
        }
        catch(UnknownHostException e) {
            System.out.println("Unknown host!");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] buildMessage() {
        String message = this.oper.toUpperCase();

        for(int i = 0; i < this.opnd.size(); i++)
            message += " " + this.opnd.get(i);

        System.out.println(message);

        return message.getBytes();
    }

    private void outputMessage(String buf) {
        System.out.print(this.oper + " ");
        for(int i = 0; i < this.opnd.size(); i++)
            System.out.print(this.opnd.get(i));
        System.out.println(" :: " + buf);
    }

    private int getFirstNullByteIndex(byte[] buf) {
        for(int i = 0; i < buf.length; i++) {
            if (buf[i] == 0) return i;
        }
        return -1;
    }

    private void getServiceSocket() {
        try {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            mcast_socket.joinGroup(InetAddress.getByName(this.mcast_host));
            //mcast_socket.setSoTimeout(3000);
            mcast_socket.receive(packet);

            this.srvc_host = packet.getAddress().getHostAddress();
            this.srvc_port = parseInt(new String(buf, 0, this.getFirstNullByteIndex(buf), StandardCharsets.UTF_8));
            this.srvc_socket = new DatagramSocket();

            System.out.println("multicast: " + this.mcast_host + " " + this.mcast_port + ": " + this.srvc_host + " " + this.srvc_port);

            mcast_socket.leaveGroup(InetAddress.getByName(this.mcast_host));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mcast_socket.close();
    }
}
