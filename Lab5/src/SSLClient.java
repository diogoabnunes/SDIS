/*
The client program shall be invoked as follows:
java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*
where
    <host> -> is the DNS name (or the IP address, in the dotted decimal format) where the server is running
    <port> -> is the port number where the server is providing service
    <oper> -> is the operation to request from the server
    <opnd>* -> is the list of operands of that operation
    <cypher-suite>* -> is a sequence, possibly empty, of strings specifying the combination of cryptographic algorithms
        the client should use, in order of preference. If no cypher suite is specified, the client shall use any
        of the cypher-suites negotiated by default by the SSL provider of JSE.

The client should print a messages on its terminal to check the operation of the service. The format of this message shall be:
SSLClient: <oper> <opnd>* : <result>
where
<oper> -> is operation requested
<opnd>* -> is the list of operands of the request
<result> -> is result returned by the server or "ERROR", if an error occurs
*/

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.Integer.parseInt;

public class SSLClient {
    String host;
    String port;
    String oper;
    String dnsName;
    String ipAddress;

    private SSLClient(String[] args) {
        this.host = args[0];
        this.port = args[1];
        this.oper = args[2];
        this.dnsName = args[3];
        if (this.oper.equals("REGISTER"))
            this.ipAddress = args[4];
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4 && args.length != 5) {
            System.out.println("Usage: java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
            System.out.println("Usage: java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
            System.exit(1);
        }
        else if ((args[2].equals("REGISTER") && args.length != 5) ||
                (args[2].equals("LOOKUP") && args.length != 4) ||
                (!args[2].equals("REGISTER") && !args[2].equals("LOOKUP"))) {
            System.out.println("Usage: java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
            System.out.println("Usage: java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*");
            System.exit(1);
        }

        SSLClient c = new SSLClient(args);

        String request = c.buildRequest();
        SSLSocket clientSocket = c.createSSLSocket(parseInt(args[1]), args[0]);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        out.println(request);
        String reply = in.readLine(); // bugged
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

    public SSLSocket createSSLSocket(int port, String host) {

        SSLSocket s = null;
        SSLSocketFactory ssf = null;

        ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            s = (SSLSocket) ssf.createSocket(host, port);
            return s;
        }
        catch( IOException e) {
            System.out.println("Server - Failed to create SSLServerSocket");
            e.getMessage();
            return null;
        }
    }
}
