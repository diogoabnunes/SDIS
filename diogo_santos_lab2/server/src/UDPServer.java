import java.io.IOException;

public class UDPServer {
    public static void main(String[] args) throws IOException{
        new Server(args).start();
        /*InformOfService thread = new InformOfService(234);
        thread.start();*/
    }
}
