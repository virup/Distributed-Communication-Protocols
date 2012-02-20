import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class ChordServer {

	class QueryClass{
		String command;
		String toNode;
		String key;
		
		QueryClass(String command, String toNode, String key)
		{
			this.command = command;
			this.toNode = toNode;
			this.key = key;
		}
		
		void print()
		{
			System.out.println("Command = "+command + " tonode = " + toNode + " key = " + key);
		}
	};
	
	HashMap<String, Node> nodeMap = new HashMap<String, Node>(); 
	Vector <QueryClass> queryList = new Vector<QueryClass>();
	LogHandler logHandle = new LogHandler("cs.txt");
	
	public List<Node> getConfiguration()
	{
		String st;
		boolean flag = false;
		
		int rmiPort = 0;
		String serverHostname;
		int numNodes;
		int countNodes = 0;
		int numID;
		List<Node> nodeList = new Vector<Node>();
		
		
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader("system.properties"));


			while((st = in.readLine()) != null) 
			{	

				if(st.length() == 0)
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
					
					Node tempNode = new Node(clientNo, nodeID, hostName, rmiPort);
					nodeList.add(tempNode);
					nodeMap.put(""+nodeID, tempNode);
					countNodes++;
				}

			}

			in.close();
		}
		catch (Exception ex) 
		{
			logHandle.write(ex.toString());
			ex.printStackTrace();
		}

		
		return nodeList;
	}
	
	
	Vector<QueryClass> readCommands(String fileName)
	{
		String st;
		Vector <QueryClass> tempQueryList = new Vector<QueryClass>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			
			while((st = in.readLine()) != null) 
			{
				String command;
				String toNode;
				String key;
				
				String [] tokens = st.split("[ ]");
				if(tokens[0].startsWith("Lookup"))
				{
					command = "Lookup";
					String[] subTokens = tokens[1].split("[=]");
					//System.out.println("subTokens[1]= " + subTokens[1]+ "/");
					toNode = subTokens[1].substring(0, subTokens[1].length() - 1 );
					
					subTokens = tokens[2].split("[=]");
					//System.out.println("subTokens[1]= " + subTokens[1]+ "/");
					key = subTokens[1].substring(0, subTokens[1].length() - 1 );
					tempQueryList.add(new QueryClass(command, toNode,key));
				}
				else if(tokens[0].startsWith("Exit"))
				{
					
					command = "Exit";
					tempQueryList.add(new QueryClass(command, "",""));
				}
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempQueryList;
	}
	
	
	
	
	void process()
	{
		// Read the commands file.
		queryList = readCommands("Commands.txt");
		Vector<Integer> nodeList = new Vector<Integer>();
		
		// loop one by one.
		for(int i = 0; i < queryList.size(); i++)
		{
			if(queryList.get(i).command.equals("Lookup"))
			{
			//queryList.get(i).print();
			Node n = nodeMap.get(queryList.get(i).toNode);
			//System.out.println("To Connect : ");
			//n.print();
			logHandle.write("Connection to " + n.nodeID);
			RemoteLookupInterface r = (RemoteLookupInterface)n.openRegistryConnection();
			int key = Integer.parseInt(queryList.get(i).key	);
			
			nodeList.clear();
			
			if (r == null)
			{
				logHandle.write(" r == null");
			}
			// perform the function.
			try {
				nodeList = r.search(key, nodeList,1);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
//			for(int j = 0 ; j < nodeList.size(); j++)
//				logHandle.write("->" + nodeList.get(j));
			logHandle.write(""+ nodeList);
			}
			else if(queryList.get(i).command.equals("Exit"))
			{
				Set<String> keySet = nodeMap.keySet();
				 Iterator<String> keySetIterator = keySet.iterator();
				 
				    while ( keySetIterator.hasNext() ){
				     RemoteLookupInterface r =  (RemoteLookupInterface) nodeMap.get(keySetIterator.next()).openRegistryConnection();
				     
				     
				     try {
						r.exitProgram();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				    }

				System.exit(0);
				
			}
		}
		
						
		// write to log
	}
	
	
	
	public static void main(String[] args)
	{
		ChordServer cs = new ChordServer();
		cs.getConfiguration();
		cs.process();
	}
	
	

}
