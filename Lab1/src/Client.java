// java Client <host> <port> <oper> <opnd>*
// Waits for reply to the request, prints it, and terminates.
// Client: <oper> <opnd>* : <result> (result by the server or "ERROR")

        import java.io.IOException;
        import java.net.*;

        import static java.lang.Integer.parseInt;

public class Client {
    DatagramSocket socket;
    String host;
    int port;
    String oper;
    String dnsName;
    String ipAddress;

    public Client(String args[]) throws Exception {
        this.socket = new DatagramSocket();
        this.host = args[0];
        this.port = parseInt(args[1]);
        this.oper = args[2];
        this.dnsName = args[3];
        if (this.oper.equals("REGISTER"))
            this.ipAddress = args[4];

        /*System.out.println("Socket: " + this.socket);
        System.out.println("Host: " + this.host);
        System.out.println("Port: " + this.port);
        System.out.println("Oper: " + this.oper);
        System.out.println("DNS Name: " + this.dnsName);
        System.out.println("IP Address: " + this.ipAddress);*/
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: java Client <host> <port> REGISTER <DNS name> <IP address>");
            System.out.println("Usage: java Client <host> <port> LOOKUP <DNS name>");
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
        String request = c.buildRequest();
        c.sendRequest(request);
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

    public void sendRequest(String request) throws IOException {
        DatagramPacket packet = new DatagramPacket(request.getBytes(), request.length(), InetAddress.getByName(this.host), this.port);
        System.out.println(request);
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
}