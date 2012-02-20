import java.rmi.*;
import java.util.Vector;


public interface RemoteLookupInterface extends Remote{

	
		public Vector<Integer> search(int key,Vector<Integer> v, int log) throws RemoteException;
		public Vector<Node> find_successor(int key,Vector<Node> v) throws RemoteException;
		public FingerTableClass getFingerTable() throws RemoteException;
		public Node getPredNode() throws RemoteException;
		public void setPredNode(Node n) throws RemoteException;
		public void leave() throws RemoteException;
		public void exitProgram() throws RemoteException;
		public int updateFingerTable(Node s, int i, int check) throws RemoteException;
		public Node find_successor_new(int i) throws RemoteException;
		public Node find_predecessor_new(int i) throws RemoteException;
		public int updateFingerTableLeave(Node succNode, int i,
				Node succ) throws RemoteException;
		public void printFingerTable(String heading) throws RemoteException;
		
	}

