import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;


class RemotePeerInfo{
	String nodeid;
	String hostname;
	int port;
	Node n;
	
	
	public RemotePeerInfo()
	{
		nodeid = null;
		hostname = null;
		port = 9999;	
	}
	
	public RemotePeerInfo(String nodeID, String hostName, int port, Node n)
	{
		this.nodeid = nodeID;
		this.hostname = hostName;
		this.port = port;
		this.n = n;
	}
}



public class ReadFile {
	
	class QueryClass{
		String command;
		String id;
		String hostname;
		
		QueryClass(String command, String toNode, String key)
		{
			this.command = command;
			this.id = toNode;
			this.hostname = key;
		}
		
		void print()
		{
			System.out.println("Command = "+command + " tonode = " + id + " hostname = " + hostname);
		}
	};

	
	
	
	public Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
	public Vector<Process> peerProcesses = new Vector<Process>();
	
	int dimension = 0;
	String inputFile = null;
	int exponent = 0;
	
	int rmiPort = 0;
	String serverHostname;
	int numNodes;    
	int countNodes = 0;
	int numID;

	
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
				else
				{
					String[] subToken = tokens[0].split("[.]");
					int clientNo = Integer.parseInt(""+subToken[0].charAt(6));
					int nodeID = Integer.parseInt(tokens[1].trim());

					st = in.readLine();
					tokens = st.split("=");
					String hostName = tokens[1].trim();

					peerInfoVector.add(new RemotePeerInfo(""+nodeID, hostName, rmiPort, new Node(clientNo, nodeID, hostName, rmiPort)));
					countNodes++;
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
		Vector<QueryClass> commandList = new Vector<QueryClass>();
			
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
					q = new QueryClass("exit", "", "");
				}
				commandList.add(q);
			}
		}catch(Exception e)
		{
		
		}
		return commandList;
		
	}
	
	public static void main(String[] args)
	{
		ReadFile r = new ReadFile();
		Vector<QueryClass> cList = new Vector<QueryClass>();
		cList = r.readCommandFile("commands.txt");
		
		for(int i = 0; i < cList.size(); i++)
			cList.get(i).print();
	}

}
