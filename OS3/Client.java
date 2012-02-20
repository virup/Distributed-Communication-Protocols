import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.rmi.*;
import java.rmi.registry.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.*;



public class Client  extends java.rmi.server.UnicastRemoteObject 
implements RemoteLookupInterface {

	/**
	 * Required by Java since it is a Serializable class
	 */
	private static final long serialVersionUID = 1L;


	Node ownNode;
	
	
	FingerTableClass ownFingerTable;
	Registry registryServer;
	Registry registryClient;
	Node predNode;
	Node succNode;
	//LogHandler log;
	List<Node> nodeList;
	int numNodes;
	LogHandler logHandle;

	Client(Node n) throws RemoteException
	{
		ownNode = n;
		
		
		//create the finger Table
		nodeList = getConfiguration();
		logHandle = new LogHandler("query" + ownNode.nodeID+".log");
		
		LogHandler logFingerTable = new LogHandler("finger" + ownNode.nodeID+".log");
		// First sort the list
		Collections.sort(nodeList, new NodeComparator());
		
		
		int size =(int)(Math.log10(numNodes)/Math.log10(2.0));
		
		
		ownFingerTable = new FingerTableClass(nodeList, ownNode, size);
		logFingerTable.writeft(ownFingerTable);
		logFingerTable.close();
		
		
		succNode = ownFingerTable.fingerTable.get(0).node;
		
		boolean flag = false;
		
		for(int i = nodeList.size() - 1; i >=0;i--)	
		{
			if(nodeList.get(i).nodeID < ownNode.nodeID)
			{
				predNode = nodeList.get(i);
				flag = true;
				break;
			}
		}
		
		if(flag == false)
		{
			predNode = nodeList.get(nodeList.size() - 1);
		}
		
		
	}
	
	public Vector<Integer> search(int key, Vector<Integer> v, int log) throws RemoteException {
		
		//logHandle.write("search");
		Vector<Integer> result = new Vector<Integer>();
		result = v;

		Node closePrec;
		
		//logHandle.write("Search key=" + key);
		
		if(haveKey(key) || key == this.ownNode.nodeID)
			;
		else if(isInThisInterval(key))
			result.add(succNode.nodeID);
			
		 else
		 {
			 closePrec = ownFingerTable.closestPrecedingNode(key);
			// System.out.println("Key to search = " + key);
			// closePrec.print();
			 RemoteLookupInterface r = (RemoteLookupInterface) closePrec.openRegistryConnection();
			
			 
			 result.addAll( r.search(key, result,0));
			
		 }
		result.add(ownNode.nodeID);
		
	
		
		// if log == 1 then write to the file.
		if(log == 1)
		{
			//System.out.println(result);
			logHandle.writeVector(result, ownNode.nodeID, key);
		}
		return result;
	}
	
	

	private boolean haveKey(int key) {
		if(key > predNode.nodeID && key < ownNode.nodeID)	
			return true;
		else if(key > predNode.nodeID && key > ownNode.nodeID && ownNode.nodeID < predNode.nodeID)
			return true;
		else
		return false;
	}

	private boolean isInThisInterval(int key) {
				
		if( ownNode.nodeID < succNode.nodeID)
		{
			if(key > ownNode.nodeID && key <= succNode.nodeID)
				return true;
		}
		else
		{
			
			if(key > ownNode.nodeID && key > succNode.nodeID)
				return true;
			else if(key < ownNode.nodeID && key <= succNode.nodeID)
				return true;
			else
				return false;
		}
			
		
		return false;
	}

	public List<Node> getConfiguration()
	{
		String st;
		boolean flag = false;
		
		int rmiPort = 0;
		String serverHostname;
	//	int numNodes;     -- change the global variable by this name
		int countNodes = 0;
		int numID;
		
		List<Node> nodeList = new Vector<Node>();
		
		
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader("system.properties"));


			while((st = in.readLine()) != null) 
			{	
				//System.out.println(st);

				if(st.length() == 0 )
					continue;
				if(st.charAt(0)== '#')
					continue;
				if(st.length() == 0)
					continue;

				String[] tokens = st.split("=");

				int i=-1;

				if(flag == false)
				{
					if(tokens[0].equalsIgnoreCase("Rmiregistry.port"))
					{
						rmiPort = Integer.parseInt(tokens[1].trim());
						i++;
					}    
					if(tokens[0].equalsIgnoreCase("Server"))
					{
						serverHostname = new String(tokens[1].trim());
					}
					if((tokens[0].equalsIgnoreCase("numberOfNodes")))
						numNodes = Integer.parseInt(tokens[1].trim());
					if(tokens[0].equalsIgnoreCase("numberOfIdentifier"))
					{
						numID = Integer.parseInt(tokens[1].trim());
						flag = true;
					}
				}
				else
				{
					String[] subToken = tokens[0].split("[.]");
					int clientNo = Integer.parseInt(""+subToken[0].charAt(6));
					int nodeID = Integer.parseInt(tokens[1].trim());

					st = in.readLine();
					tokens = st.split("=");
					String hostName = tokens[1].trim();

					nodeList.add(new Node(clientNo, nodeID, hostName, rmiPort));
					countNodes++;
				}

			}

			in.close();
		}
		catch (Exception ex) 
		{
			//System.out.println(ex.toString());
			ex.printStackTrace();
		}

		return nodeList;
	}
	
	public void makeRegistry() throws RemoteException
	{
		//registryServer = LocateRegistry.createRegistry(ownNode.portNo);
		registryServer = LocateRegistry.createRegistry(ownNode.portNo);
        registryServer.rebind("rmiServervk4"+ownNode.nodeID, this);
	}

	public void exitProgram() throws RemoteException {
		//System.out.println("Exiting");
		logHandle.write("Complete");
		logHandle.close();
		System.exit(0);
		
	}
	
	
	public static void main(String[] args)
	{
		int ownClientNo = Integer.parseInt(args[0]);
		int ownNodeID = Integer.parseInt(args[1]);
		String ownHostname = args[2];
		int ownPort = Integer.parseInt(args[3]);
		
		
		
		Node ownNode = new Node(ownClientNo, ownNodeID, ownHostname, ownPort);
		//ownNode.print();
		Client client = null;
		
		
		try {
			client = new Client(ownNode);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// Create the registry and bind
		try {
			client.makeRegistry();
		} catch (RemoteException e) {
			client.logHandle.write("error in making reg");
			
			client.logHandle.write(e.getMessage());
			e.printStackTrace();
		}
		
		
		
		
		// wait until exit
	}

	
	




}
