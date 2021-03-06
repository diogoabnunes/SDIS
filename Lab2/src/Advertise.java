import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.TimerTask;

public class Advertise extends TimerTask {
    int srvcPort, mcastPort;
    InetAddress srvcAddress, mcastAddress;
    DatagramSocket socket;
    DatagramPacket packet;

    public Advertise(int srvcPort, InetAddress srvcAddress, int mcastPort, InetAddress mcastAddress) throws IOException {
        this.srvcPort = srvcPort;
        this.srvcAddress = srvcAddress;
        this.mcastPort = mcastPort;
        this.mcastAddress = mcastAddress;

        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            String toSend = this.srvcAddress.getHostAddress() + " " + this.srvcPort;

            this.packet = new DatagramPacket(toSend.getBytes(), toSend.getBytes().length, this.mcastAddress, this.mcastPort);
            this.socket.send(this.packet);
            System.out.println("multicast: " + this.mcastAddress .getHostAddress() + " " + this.mcastPort + " : " + this.srvcAddress.getHostAddress() + " " + this.srvcPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
