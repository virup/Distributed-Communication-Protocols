import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Node {
	
	int nodeID;
	int key;
	String hostname;
	int portNo;
	int clientNo;
	
	public Node() 
	{
		// TODO Auto-generated constructor stub
	}
	
	Node(int clientNo, int nodeID, String hostName, int port)
	{
		this.clientNo = clientNo;
		this.nodeID = nodeID;
		this.hostname = new String(hostName);
		this.portNo = port;
		this.key = nodeID;

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
	
		
FileWriter fstream = null;
		try {
			fstream = new FileWriter("connection.log");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
			BufferedWriter fout = new BufferedWriter(fstream);
			


		try {
			//TODO: CHANGE THE PORT NUMBER TO node.port
			//===================================
			registryClient = LocateRegistry.getRegistry(hostname, portNo);
		} catch (RemoteException e1) {
try{
			fout.write(e1.toString());
			fout.flush();
			}
			catch(Exception exe)
			{}
			e1.printStackTrace();
		}
	     
	     RemoteLookupInterface rmiServer = null;
		try {
			rmiServer = (RemoteLookupInterface)(registryClient.lookup("rmiServervk4"+nodeID));
		} catch (AccessException e) {
			try{
			fout.write(e.toString());
			fout.flush();
			}
			catch(Exception exe)
			{}
			e.printStackTrace();
		} catch (RemoteException e) {
			try{
			fout.write(e.toString());
			fout.flush();
			}
			catch(Exception exe)
			{}
			e.printStackTrace();
		} catch (NotBoundException e) {
			try{
			fout.write(e.toString());
			fout.flush();
			}
			catch(Exception exe)
			{}
			e.printStackTrace();
		}
				
		try{
			fout.close();
			}
			catch(Exception exe)
			{}
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
