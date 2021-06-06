// java SSLServer <port> <cypher-suite>*

/*
The server program shall be invoked as follows:
java SSLServer <port> <cypher-suite>*
where
    <port> -> is the port number the server shall use to provide the service
    <cypher-suite>* -> is a sequence, possibly empty, of strings specifying the combination of cryptographic algorithms the server should use, in order of preference.
        If no cypher suite is specified, the server shall use any of the cypher-suites negotiated by default by the SSL provider of JSE.

To trace the operation of the server, it should print a messages on its terminal each time it processes a client request. The format of this message shall be:
SSLServer: <oper> <opnd>*
where
    <oper> -> is operation received on the request
    <opnd>* -> is the list of operands receive in the request

IMP. To simplify, in this specification we have omitted from the command line the JVM parameters such as the property java.net.ssl.keystore that you may have to add.
* */

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class SSLServer {
    private final HashMap<String, String> DNSTable;

    public SSLServer() {
        this.DNSTable = new HashMap<>();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java SSLServer <port> <cypher-suite>*");
            return;
        }

        try {
            Server s = new Server();
            s.handleRequests(parseInt(args[0]));

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void handleRequests(int port) throws IOException {
        System.err.println("Server ON!");

        SSLServerSocket serverSocket = createSSLServerSocket(port);
        // Require client authentication
        serverSocket.setNeedClientAuth(true);  // s is an SSLServerSocket

        while (true) {
            Socket clientSocket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String request = in.readLine();
            String reply = processRequest(request);

            out.println(reply);

            out.close();
            in.close();
            clientSocket.close();
        }

    }

    public String processRequest(String data) {
        String[] args = data.split(" ");

        return switch (args[0]) {
            case "REGISTER" -> String.valueOf(this.register(args[1], args[2]));
            case "LOOKUP" -> this.lookup(args[1]);
            default -> "ERROR";
        };
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

    public SSLServerSocket createSSLServerSocket(int port) {

        SSLServerSocket s = null;
        SSLServerSocketFactory ssf = null;

        ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            s = (SSLServerSocket) ssf.createServerSocket(port);
            return s;
        }
        catch( IOException e) {
            System.out.println("Server - Failed to create SSLServerSocket");
            e.getMessage();
            return null;
        }
    }
}
