import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class Client {
    String host;
    String port_number;
    String oper;
    String dnsName;
    String ipAddress;

    private Client(String[] args) {
        this.host = args[0];
        this.port_number = args[1];
        this.oper = args[2];
        this.dnsName = args[3];
        if (this.oper.equals("REGISTER"))
            this.ipAddress = args[4];
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd> *");
            System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd> *");
            System.exit(1);
        }
        else if ((args[2].equals("REGISTER") && args.length != 5) ||
                (args[2].equals("LOOKUP") && args.length != 4) ||
                (!args[2].equals("REGISTER") && !args[2].equals("LOOKUP"))) {
            System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd> *");
            System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd> *");
            System.exit(1);
        }

        Client c = new Client(args);

        String request = c.buildRequest();
        Socket clientSocket = new Socket(args[0], parseInt(args[1]));

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        out.println(request);
        String reply = in.readLine();
        System.out.println("Client: " + request + " : " + reply);

        out.close();
        in.close();
        clientSocket.close();
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
}