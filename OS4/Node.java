import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Comparator;


public class Node implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int nodeID;
	int key;
	String hostname;
	int portNo;
	int clientNo;
	
	public Node() 
	{
		
	}
	
	Node(int clientNo, int nodeID, String hostName, int port)
	{
		this.clientNo = clientNo;
		this.nodeID = nodeID;
		this.hostname = new String(hostName);
		this.portNo = port;
		this.key = nodeID;

	}
	
	public Node(Node n) {
		this.clientNo = n.clientNo;
		this.nodeID = n.nodeID;
		this.hostname = new String(n.hostname);
		this.portNo = n.portNo;
		this.key = n.nodeID;
	}

	public int compareTo(Node n) {
	       if(nodeID > n.nodeID)
	    	   return 1;
	       if(nodeID == n.nodeID)
	    	   return 0;
	       else
	    	   return -1;
	    }
	
	 Object openRegistryConnection()
	 {
		 
		// get the registry 
	     Registry registryClient = null;
		try {
			//TODO: fix the port number
			
			//System.out.println("Node: Connecting to " + nodeID);
			registryClient = LocateRegistry.getRegistry(hostname, portNo+nodeID);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	     
	     RemoteLookupInterface rmiServer = null;
		try {
			rmiServer = (RemoteLookupInterface)(registryClient.lookup("rmiServervk4"+nodeID));
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
				
	     return rmiServer;
	
		 
	 }



	void print()
	{
		System.out.println("" + clientNo + ", " + nodeID + ", " + hostname+":"+portNo);
	}


}


class NodeComparator implements Comparator {
	  public int compare(Object o1, Object o2) {
	    Node n1 = (Node) o1;
	    Node n2 = (Node) o2;
	    if(n1.nodeID > n2.nodeID)
	    	   return 1;
	       if(n1.nodeID == n2.nodeID)
	    	   return 0;
	       else
	    	   return -1;
	    
	  }
}
