import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {
    int register(String DNS, String IP) throws RemoteException;
    String lookup(String DNS) throws RemoteException;
}