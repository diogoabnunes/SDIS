import java.io.IOException;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        new Server(args).start();
    }
}
