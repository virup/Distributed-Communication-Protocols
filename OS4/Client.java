import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.rmi.*;
import java.rmi.registry.*;
import java.io.BufferedReader;
import java.io.FileReader;


public class Client  extends java.rmi.server.UnicastRemoteObject 
implements RemoteLookupInterface {

	/**
	 * Required by Java since it is a Serializable class
	 */
	private static final long serialVersionUID = 1L;


	Node ownNode;
	int fingerTableSize;


	FingerTableClass ownFingerTable;
	Registry registryServer;
	Registry registryClient;
	Node predNode;  
	Node succNode;
	//LogHandler log;
	List<Node> nodeList;
	int numNodes;
	LogHandler logHandle;

	int totalNodesTillNow;

	Client(Node n, int firstClient) throws RemoteException
	{


		this.ownNode = n;
		//logHandle = new LogHandler("query" + ownNode.nodeID+".log");
		logHandle = new LogHandler("finger" + ownNode.nodeID+".log");

		totalNodesTillNow = firstClient;
		join(null);


		//logHandle.writeft(ownFingerTable);
		//logHandle.close();

		// Create the registry and bind
		try {
			makeRegistry();
		} catch (RemoteException e) {
			e.getMessage();
			e.printStackTrace();
		}

	}

	public Client(Node ownNode, Node oldNode, int firstClient) throws RemoteException
	{
		
		logHandle = new LogHandler("finger" + ownNode.nodeID+".log");


		this.ownNode = ownNode;		
		totalNodesTillNow = firstClient;

		// Create the registry and bind
		try {
			makeRegistry();
		} catch (RemoteException e) {
			e.getMessage();
			e.printStackTrace();
		}


		join(oldNode);

		
		//printFingerTable();
	
	}

	public Vector<Integer> search(int key, Vector<Integer> v, int log) throws RemoteException {


		Vector<Integer> result = new Vector<Integer>();
		result = v;

		Node closePrec;

		System.out.println("Search key=" + key);

		if(haveKey(key) || key == this.ownNode.nodeID)
			;
		else if(isInThisInterval(key))
			result.add(succNode.nodeID);

		else
		{
			closePrec = ownFingerTable.closestPrecedingNode(key);
			System.out.println("Key to search = " + key);
			closePrec.print();
			RemoteLookupInterface r = (RemoteLookupInterface) closePrec.openRegistryConnection();


			result.addAll( r.search(key, result,0));

		}
		result.add(ownNode.nodeID);



		// if log == 1 then write to the file.
		if(log == 1)
		{
			System.out.println(result);
			logHandle.writeVector(result, ownNode.nodeID, key);
		}
		return result;
	}



	// ask node n to find id's successor
	public Node find_successor_new(int id)
	{
		Node n = find_predecessor_new(id);
		return n;
	}

	public Node find_predecessor_new(int id)
	{
		Node n = ownNode;

		// get successor of n 
		RemoteLookupInterface nR = (RemoteLookupInterface)n.openRegistryConnection();
		Node succ = null;
		try {
			succ = nR.getFingerTable().fingerTable.get(0).node;
		} catch (RemoteException e1) {

			e1.printStackTrace();
		}


		while(!isInside(id, n.nodeID, succ.nodeID))
		{
			n = closest_preceding_finger(id);

			// get successor of n 
			nR = (RemoteLookupInterface)n.openRegistryConnection();
			try {
				succ = nR.getFingerTable().fingerTable.get(0).node;
			} catch (RemoteException e) {

				e.printStackTrace();
			}
		}

		return n;
	}

	public Node closest_preceding_finger(int id)
	{
		int m = this.fingerTableSize;

		for(int i = m-1; i > 0; i-- )
		{
			if(isInside(ownFingerTable.fingerTable.get(i).node.nodeID,
					ownNode.nodeID, id))
			{
				return ownFingerTable.fingerTable.get(i).node;
			}
		}

		return ownNode;
	}

	public Vector<Node> find_successor(int key, Vector<Node> v) throws RemoteException 
	{


		Vector<Node> result = new Vector<Node>();
		result = v;

		Node closePrec;

		//System.out.println("Search key=" + key);

		if(haveKey(key) || key == this.ownNode.nodeID)
			;
		else if(isInThisInterval(key))
			result.add(succNode);


		else
		{
			closePrec = ownFingerTable.closestPrecedingNode(key);
			System.out.println("Key to search = " + key);
			System.out.print("Closest preceding = ");
			closePrec.print();
			RemoteLookupInterface r = (RemoteLookupInterface) closePrec.openRegistryConnection();


			result.addAll( r.find_successor(key, result));

		}
		result.add(ownNode);



		// if log == 1 then write to the file.
		/*if(log == 1)
		{
			System.out.println(result);
			logHandle.writeVector(result, ownNode.nodeID, key);
		}
		 */
		return result;
	}


	private boolean haveKey(int key) {
		/*	if(key > predNode.nodeID && key < ownNode.nodeID)	
			return true;
		//TODO: make this correct : Check
		else if(key > predNode.nodeID && key > ownNode.nodeID && ownNode.nodeID < predNode.nodeID)
			return true;
		else
		return false;
		 */

		return isInside(key, predNode.nodeID, ownNode.nodeID);
	}

	private boolean isInThisInterval(int key) 
	{

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

	private int getNumNodes()
	{
		String st;
		boolean flag = false;
		int numID = 0;

		try 
		{
			BufferedReader in = new BufferedReader(new FileReader("system.properties"));

			while((st = in.readLine()) != null) 
			{	
				System.out.println(st);

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
						//int rmiPort = Integer.parseInt(tokens[1].trim());
						i++;
					}    
					if(tokens[0].equalsIgnoreCase("Server"))
					{
						//String serverHostname = new String(tokens[1].trim());
					}
					if((tokens[0].equalsIgnoreCase("numberOfNodes")))
						numNodes = Integer.parseInt(tokens[1].trim());
					if(tokens[0].equalsIgnoreCase("numberOfIdentifier"))
					{
						numID = Integer.parseInt(tokens[1].trim());
						flag = true;
					}
				}
			}

			in.close();
		}
		catch (Exception ex) 
		{
			System.out.println(ex.toString());
			ex.printStackTrace();
		}

		return numNodes;
	}

	public void makeRegistry() throws RemoteException
	{
		//registryServer = LocateRegistry.createRegistry(ownNode.portNo);
		registryServer = LocateRegistry.createRegistry(ownNode.portNo + ownNode.nodeID);
		registryServer.rebind("rmiServervk4"+ownNode.nodeID, this);
	}

	public void exitProgram() throws RemoteException {
		System.out.println("Exiting");
		logHandle.write("Complete");
		logHandle.close();
		System.exit(0);

	}


	//////// project 4 functions //// 


	public int updateFingerTable(Node s, int i, int check) throws RemoteException 
	{
		System.out.println("UpdateFingerTable");
		System.out.println("S = "+s.nodeID + "  i = "+i + " Check = " + check);

		if(check == 1)
		{

			for(int t = 0; t < ownFingerTable.fingerTable.size(); t++)
			{
				// S should be smaller than old finger(i) node
			/*	if(isInside(ownFingerTable.fingerTable.get(i).startID, ownNode.nodeID, s.nodeID	) 
						&& (isInside(s.nodeID,  ownFingerTable.fingerTable.get(i).startID, ownFingerTable.fingerTable.get(i).node.nodeID)))
						*/
				
				if((isInside(s.nodeID, ownFingerTable.fingerTable.get(t).startID, ownFingerTable.fingerTable.get(t).node.nodeID)
						&& ownFingerTable.fingerTable.get(t).startID != ownFingerTable.fingerTable.get(t).node.nodeID)
						|| s.nodeID == ownFingerTable.fingerTable.get(t).startID)
					
				{
					FingerTableEntry temp = ownFingerTable.fingerTable.get(t);
					temp.node = s;
					ownFingerTable.fingerTable.set(t, temp);
				}
			}
			succNode = ownFingerTable.fingerTable.get(0).node;
			ownFingerTable.print();
			return 0;
		}
		System.out.print("IsInside: " + s.nodeID + "  " +ownNode.nodeID+ "  " +ownFingerTable.fingerTable.get(i).node.nodeID);

		if(isInside(s.nodeID, ownNode.nodeID, ownFingerTable.fingerTable.get(i)))
			//&& !(isInside(ownFingerTable.fingerTable.get(i).node.nodeID, ownNode.nodeID, s.nodeID))
			//&& (isInside(ownFingerTable.fingerTable.get(i).startID, ownNode.nodeID, s.nodeID)))
		{
			System.out.println(": true");
			System.out.println("S = " + s.nodeID);
			FingerTableEntry temp = ownFingerTable.fingerTable.get(i);
			temp.node = s;
			ownFingerTable.fingerTable.set(i, temp);

			Node p = this.predNode;


			succNode = ownFingerTable.fingerTable.get(0).node;
			System.out.println("predNode = " + predNode.nodeID);
			RemoteLookupInterface pR = (RemoteLookupInterface)p.openRegistryConnection();
			pR.updateFingerTable(s, i, check);
		

		}
		else
			System.out.println(": false");

		succNode = ownFingerTable.fingerTable.get(0).node;
		//System.out.println("Old fingerTable: ");
		ownFingerTable.print();
		return 0;
	}
	
	
	public int updateFingerTableLeave(Node s, int i, Node succ) throws RemoteException 
	{
		System.out.println("UpdateFingerTableLeave");
		System.out.println("S = "+s.nodeID + "  i = "+i );

		if(ownNode.nodeID == s.nodeID) return 0;		
		
		if(ownFingerTable.fingerTable.get(i).node.nodeID == s.nodeID)
		{
			FingerTableEntry temp = ownFingerTable.fingerTable.get(i);
			temp.node = succ;
			ownFingerTable.fingerTable.set(i, temp);
			
			
			
		}
		
		RemoteLookupInterface predR = (RemoteLookupInterface)this.predNode.openRegistryConnection();
		predR.updateFingerTableLeave(s, i, succ);

		succNode = ownFingerTable.fingerTable.get(0).node;
		ownFingerTable.print();

		return 0;
	}


	public Node getPredNode()
	{
		return predNode;
	}

	public void setPredNode(Node n)
	{
		predNode = new Node(n);
	}

	public FingerTableClass getFingerTable() throws RemoteException
	{
		return ownFingerTable;
	}


	public void join(Node oldNode)
	{
		System.out.println("Join");
		numNodes = getNumNodes();
		if(oldNode != null)
		{
			init_finger_table(oldNode);
			update_others();		

		}
		else
		{
			//TODO: Check how to do this part. 
			ownFingerTable = new FingerTableClass(ownNode);
			//numNodes = getNumNodes();
			int m =(int)(Math.log10(numNodes)/Math.log10(2.0));

			fingerTableSize = m;
			int maxID = numNodes;
			int key = ownNode.key;
			for(int i = 0; i < m; i++)
			{
				int startID = (int)Math.pow(2, i);
				startID += key;

				if(startID >= maxID)
					startID = startID % maxID;


				int endID = (int)Math.pow(2, i+1);
				endID += key;

				if(endID >= maxID)
					endID = endID % maxID;

				ownFingerTable.fingerTable.add(i,new FingerTableEntry(ownNode, startID, endID));
			}

			predNode = ownNode;

			succNode = ownFingerTable.fingerTable.get(0).node;
		}
	}


	private void update_others() 
	{
		int m = fingerTableSize;
		Vector<Node> v ;

		System.out.println("Update_others");
		for(int i = 0 ; i < m; i++)
		{
			int lookUpKey = ownNode.nodeID - (int)Math.pow(2, i);
			if(lookUpKey < 0)
				lookUpKey += numNodes;

			Node p = null;	
			//	if(this.totalNodesTillNow == 1)
			//	{
			try {
				v = new Vector<Node>();
				System.out.println("Looking up: " + lookUpKey);
				p = find_successor(lookUpKey, v).get(0);
				System.out.println("Successor = " + p.nodeID);
				if(p.nodeID != lookUpKey)
				{
					RemoteLookupInterface pR = (RemoteLookupInterface)p.openRegistryConnection();
					p = pR.getPredNode();

				}
				System.out.println("PredNode = " + p.nodeID);
			} catch (RemoteException e) 
			{
				e.printStackTrace();
			}

			//	}
			//	else
			//	{		
			//		p = find_predecessor_new(lookUpKey);
			//	}
			System.out.println("i = " + i + " lookUpKey = " + lookUpKey + " Updating node :" + p.nodeID);
			RemoteLookupInterface pR = (RemoteLookupInterface)p.openRegistryConnection();

			try {
				pR.updateFingerTable(ownNode, i, totalNodesTillNow);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		totalNodesTillNow = -1;



	}

	public void init_finger_table(Node oldNode)
	{
		System.out.println("init_finger_table");
		FingerTableClass oldNodeFingerTable = null ;
		ownFingerTable = new FingerTableClass(ownNode);

		int [] fingerStart;
		int [] fingerEnd;
		Vector<Node> v = new Vector<Node>();

		RemoteLookupInterface oldNodeR = (RemoteLookupInterface)oldNode.openRegistryConnection();

		//Get the finger table of oldNode
		try {
			oldNodeFingerTable = oldNodeR.getFingerTable();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		//	int m = oldNodeFingerTable.fingerTable.size();
		int m =(int)(Math.log10(numNodes)/Math.log10(2.0));
		fingerTableSize = m;
		fingerStart = new int[m];
		fingerEnd = new int[m];

		int key = ownNode.nodeID;
		int maxID = (int)Math.pow(2, m);

		for(int i = 0; i < m; i ++)
		{
			int startID = (int)Math.pow(2, i);
			startID += key;

			if(startID >= maxID)
				startID = startID % maxID;

			fingerStart[i] = startID;

			int endID = (int)Math.pow(2, i+1);
			endID += key;

			if(endID >= maxID)
				endID = endID % maxID;
			fingerEnd[i] = endID;
		}

		// run find_successor on oldNode
		Node successor = null;
		System.out.println("Finding successor:");
		try {
			//	if(this.totalNodesTillNow == 1)
			successor = (oldNodeR.find_successor(fingerStart[0],v )).get(0);
			//	else
			//		successor = oldNodeR.find_successor_new(fingerStart[0]);
		} catch (RemoteException e) 
		{
			e.printStackTrace();
		}

		if(isInside(ownNode.nodeID, fingerStart[0], successor.nodeID)
				&& fingerStart[0] != successor.nodeID)
			ownFingerTable.fingerTable.add(0, new FingerTableEntry(ownNode, fingerStart[0],fingerEnd[0]));
		else
			ownFingerTable.fingerTable.add(0, new FingerTableEntry(successor, fingerStart[0],fingerEnd[0]));


		RemoteLookupInterface succRemote = (RemoteLookupInterface)successor.openRegistryConnection();
		try {
			predNode = succRemote.getPredNode();
			succRemote.setPredNode(ownNode);
		} catch (RemoteException e1) 
		{
			e1.printStackTrace();
		}



		for(int i = 0; i < m-1; i++)
		{
			System.out.print("IsInside: " + fingerStart[i+1] + "  " +ownNode.nodeID+ "  " +ownFingerTable.fingerTable.get(i).node.nodeID);
			if(isInside(fingerStart[i+1], ownNode.nodeID, ownFingerTable.fingerTable.get(i)))
			{
				System.out.println(" : true"	);
				ownFingerTable.fingerTable.add(i+1, 
						new FingerTableEntry(ownFingerTable.fingerTable.get(i).node, 
								fingerStart[i+1],
								fingerEnd[i+1]));

			}
			else
			{
				System.out.println(" : False");
				v = new Vector<Node>();
				Node fingerTableNode = null;

				try {
					fingerTableNode = oldNodeR.find_successor(fingerStart[i+1], v ).get(0);
					//	fingerTableNode = oldNodeR.find_successor_new(fingerStart[i+1] );
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			//	if(ownNode.nodeID < fingerTableNode.nodeID && ownNode.nodeID > fingerStart[i+1])
					
				if(isInside(ownNode.nodeID, fingerStart[i+1], fingerTableNode.nodeID) && fingerStart[i+1] != fingerTableNode.nodeID)
					ownFingerTable.fingerTable.add(i+1, 
							new FingerTableEntry(ownNode, fingerStart[i+1],fingerEnd[i+1]));
				else
					ownFingerTable.fingerTable.add(i+1, 
						new FingerTableEntry(fingerTableNode, fingerStart[i+1],fingerEnd[i+1]));

			}
		}
		succNode = ownFingerTable.fingerTable.get(0).node;
	}



	private boolean isInside(int i, int nodeID,	int id) 
	{
		int fingerNodeID = i; 

	if(id == nodeID) return true;
		
		

		if((fingerNodeID > nodeID && fingerNodeID < id)
				|| (fingerNodeID < nodeID && fingerNodeID < id && nodeID > id) 
				|| (fingerNodeID > nodeID && fingerNodeID > id && nodeID > id))
		{
			return true;
		}


		return false;
	}

	private boolean isInside(int i, int nodeID,	FingerTableEntry fingerTableEntry) 
	{
		int fingerNodeID = i; 
		int id = fingerTableEntry.node.nodeID;

		if(id == nodeID) return true;

		if((fingerNodeID > nodeID && fingerNodeID < id)
				|| (fingerNodeID < nodeID && fingerNodeID < id && nodeID > id) 
				|| (fingerNodeID > nodeID && fingerNodeID > id && nodeID > id))
		{
			return true;
		}


		return false;
	}

	public Node sendOwnNode() throws RemoteException
	{
		return ownNode;
	}


	public void leave() throws RemoteException 
	{
		int m = fingerTableSize;

		

		Vector<Node> v ;
		System.out.println("[leave] Update_others");
		System.out.println("[leave] m = " + m );
		for(int i = 0; i < m; i++)
		{

			int lookUpKey = ownNode.nodeID - (int)Math.pow(2, i);
			if(lookUpKey < 0)
				lookUpKey += numNodes;

			Node p = null;	
			try {
				v = new Vector<Node>();
				System.out.println("[leave] Looking up : " + lookUpKey);
				p = find_successor(lookUpKey, v).get(0);
				System.out.println("[leave] Successor = " + p.nodeID);
				if(p.nodeID != lookUpKey)
				{
					RemoteLookupInterface pR = (RemoteLookupInterface)p.openRegistryConnection();
					p = pR.getPredNode();

				}
				System.out.println("[leave] PredNode = " + p.nodeID);
			} catch (RemoteException e) 
			{
				e.printStackTrace();
			}

			
			System.out.println("[leave] i = " + i + " lookUpKey = " + lookUpKey + " Updating node :" + p.nodeID);
			RemoteLookupInterface pR = (RemoteLookupInterface)p.openRegistryConnection();

			try {
				pR.updateFingerTableLeave(this.ownNode, i, this.succNode);

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		totalNodesTillNow = -1;
		
		RemoteLookupInterface succNodeR = (RemoteLookupInterface)succNode.openRegistryConnection();
		succNodeR.setPredNode(this.predNode);

		exitProgram();


	}

	public void printFingerTable(String headline) throws RemoteException
	{
		logHandle.write("\nNew FingerTable: " + headline);
		logHandle.writeft(ownFingerTable);
	}


public static void main(String[] args)
{
	int ownClientNo = Integer.parseInt(args[0]);
	int ownNodeID = Integer.parseInt(args[1]);
	String ownHostname = args[2];
	int ownPort = Integer.parseInt(args[3]);
	int firstClient = Integer.parseInt(args[4]);



	Node oldNode;
	Node ownNode = new Node(ownClientNo, ownNodeID, ownHostname, ownPort);
	ownNode.print();
	Client client = null;




	if(firstClient != 0)
	{
		int id = Integer.parseInt(args[5]);
		String hostname = args[6];
		oldNode = new Node(id,id,hostname, ownPort);

		try {
			client = new Client(ownNode, oldNode, firstClient);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	else
	{

		try {
			client = new Client(ownNode, firstClient);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}


	// wait until exit
}


}