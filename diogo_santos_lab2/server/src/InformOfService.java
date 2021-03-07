import java.io.IOException;
import java.net.*;

public class InformOfService extends Thread{
    private int mcast_port;
    private String mcast_addr;
    private int srvc_port;

    public InformOfService(int mcast_port, String mcast_addr, int srvc_port) {
        this.mcast_port = mcast_port;
        this.mcast_addr = mcast_addr;
        this.srvc_port = srvc_port;
    }

    public void run() {
        try {
            // creates an array of 256 bytes
            byte[] buf = Integer.toString(this.srvc_port).getBytes();

            MulticastSocket socket = new MulticastSocket();
            DatagramPacket data = new DatagramPacket(buf, buf.length, InetAddress.getByName(this.mcast_addr), this.mcast_port);
            String srvc_addr = InetAddress.getLocalHost().getHostAddress();

            while(true) {
                System.out.println("multicast: " + this.mcast_addr + " " + this.mcast_port + ": " + srvc_addr + " " + this.srvc_port);

                socket.setTimeToLive(1);
                socket.send(data);

                Thread.sleep(1000);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
