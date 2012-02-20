import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;




public class start {

	int rmiPort;
	String serverHostname;
	int numNodes;
	int numID;
	Vector<QueryClass> commandList ;
	Vector<Node> nodeList;
	HashMap< Integer, Node>nodeAddMap ; 
	
	
	class QueryClass{
		String command;
		Integer id;
		String hostname;
		
		QueryClass(String command, String toNode, String key)
		{
			this.command = command;
			this.id = Integer.parseInt(toNode);
			this.hostname = key;
		}
		
		void print()
		{
			System.out.println("Command = "+command + " tonode = " + id + " hostname = " + hostname);
		}
	};
	
	
	start()
	{
		nodeList = new Vector<Node>();
		nodeAddMap = new HashMap<Integer, Node>();
	}
	
	public void getConfiguration()
	{
		String st;
		boolean flag = false;
		
		
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
			}

			in.close();
		}
		catch (Exception ex) 
		{
			System.out.println(ex.toString());
			ex.printStackTrace();
		}

		
	}

	Vector<QueryClass> readCommandFile(String fileName)
	{
				
		String st;
		boolean flag = false;
		QueryClass q;
		commandList = new Vector<QueryClass>();
			
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader(fileName));


			while((st = in.readLine()) != null) 
			{
				if(st.length() == 0 )
					continue;
				if(st.charAt(0)== '#')
					continue;
				if(st.length() == 0)
					continue;

				String[] tokens = st.split("=");
				
				if(tokens[0].equalsIgnoreCase("join.id"))
				{
					int id = Integer.parseInt(tokens[1]);
					st = in.readLine();
					String[] hostnameTok = st.split("=");
					String hostname = hostnameTok[1];
					
					q = new QueryClass("join", ""+id,hostname);
					
				}
				else if(tokens[0].equalsIgnoreCase("leave.id"))
				{
					int id = Integer.parseInt(tokens[1]);
					q = new QueryClass("leave", ""+id, "");
				}
				else 
				{
					q = new QueryClass("exit", "0", "null");
				}
				commandList.add(q);
			}
		}catch(Exception e)
		{	}
		return commandList;
	}

	private void joinNode(QueryClass query)
	{
		String path = System.getProperty("user.dir");
		int oldID = 0;
		String oldHostname = null;
		Node tempNode = null;
	
		
		Iterator<Integer> i = nodeAddMap.keySet().iterator();
		
		if(i.hasNext())
		{
			tempNode = new Node(nodeAddMap.get(i.next()));
			oldID = tempNode.nodeID;
			oldHostname = tempNode.hostname;
		}
		String command = "ssh " + query.hostname + 
			" cd " + path + "; java Client " 
			+ query.id + " " + query.id 
			+ " " + query.hostname 
			+ " " +  rmiPort
			+ " " + nodeList.size()
			+ " " + oldID 
			+ " " + oldHostname;
		
		
		nodeList.add( new Node(query.id, query.id,query.hostname, rmiPort));
		nodeAddMap.put(query.id, new Node(query.id, query.id,query.hostname, rmiPort));
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(command);
		try {
			Thread.sleep(5500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Query id = ");
		System.out.println(query.id);
		printAll("Joining node " + query.id);
	}
	
	
	private void leaveNode(QueryClass query) 
	{
		System.out.println("LeaveNode");
		Node leaveNode = new Node(query.id, query.id, nodeAddMap.get(query.id).hostname, rmiPort);
		RemoteLookupInterface leaveNodeR = (RemoteLookupInterface)leaveNode.openRegistryConnection();
		
		try {
			System.out.println("Leaving Node : " + leaveNode.nodeID);
			leaveNodeR.leave();
		} catch (RemoteException e) {
			
		}
		
		nodeAddMap.remove(query.id);
		
		try {
			Thread.sleep(5500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printAll("Leaving node:" + leaveNode.nodeID);
	}
	
	private void printAll(String headline) 
	{
		RemoteLookupInterface rli;
		
			
		Iterator<Integer> i = nodeAddMap.keySet().iterator();
		
		while(i.hasNext())
		{
			Node tempNode = nodeAddMap.get(i.next());
	    	rli = (RemoteLookupInterface) tempNode.openRegistryConnection();
			
			try {
				rli.printFingerTable(headline);
			} catch (RemoteException e) {
				//e.printStackTrace();
			}
		}
		
	}

	private void exit() 
	{
		System.out.println("Exiting");
		RemoteLookupInterface rli;
		
		
			
		Iterator<Integer> i = nodeAddMap.keySet().iterator();
		
		while(i.hasNext())
		{
			Node tempNode = nodeAddMap.get(i.next());
	    	rli = (RemoteLookupInterface) tempNode.openRegistryConnection();
			
			try {
				System.out.println("Killing node: " + tempNode.nodeID);
				rli.exitProgram();
			} catch (RemoteException e) {
				//e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args)
	{
		start s = new start();
		s.getConfiguration();
		s.readCommandFile("command");
		
		for(int i = 0; i < s.commandList.size();i++)
		{
			if(s.commandList.get(i).command.equalsIgnoreCase("join"))
			{
				s.joinNode(s.commandList.get(i));
			}
			else if(s.commandList.get(i).command.equalsIgnoreCase("leave"))
			{
				s.leaveNode(s.commandList.get(i));
			}
			else if(s.commandList.get(i).command.equalsIgnoreCase("exit"))
			{
				s.exit();
				System.exit(0);
			}
		}
		
	}

	

	

	
}
