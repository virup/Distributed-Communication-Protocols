import java.rmi.*;
import java.util.Vector;


public interface RemoteLookupInterface extends Remote{

	
		public Vector search(int key,Vector<Integer> v, int log) throws RemoteException;
		
		public void exitProgram() throws RemoteException;
		
	}

