import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class Server {
    private final HashMap<String, String> DNSTable;

    public Server() {
        this.DNSTable = new HashMap<>();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <srvc_port>");
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

    public void handleRequests(int srvcPort) throws IOException {
        System.err.println("Server ON!");

        ServerSocket serverSocket = new ServerSocket(srvcPort);

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

}