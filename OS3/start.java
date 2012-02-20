import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
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

public class start {
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
		
	
		
	//	List<Node> nodeList = new Vector<Node>();
		
		
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
	
	
	
	
	
	public static void main(String[] args)
	{
		try 
		{
			start myStart = new start();
			
			myStart.getConfiguration();
					
			// get current path
			String path = System.getProperty("user.dir");
						
			// start clients at remote hosts
			for (int i = myStart.peerInfoVector.size()-1; i >=0 ; i--) 
			{
				RemotePeerInfo pInfo = (RemotePeerInfo) myStart.peerInfoVector.elementAt(i);
				
				//System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
				String command = "ssh " + pInfo.hostname + " cd " + path + "; java Client " + pInfo.n.clientNo + " " + pInfo.nodeid + " " + pInfo.hostname + " " +  pInfo.port;; 
				
				myStart.peerProcesses.add(Runtime.getRuntime().exec(command));
				
				System.out.println(command);
			}		
		
			//String command = "ssh " + serverHostname + " cd " + path + "; java CoordinatingAgent";
			System.out.println("Starting all remote peers has done." );
			
			
			Thread.sleep(3000);
			
			//String command = "java ChordServer";
			String command = "ssh " + myStart.serverHostname  + " cd " + path + "; java ChordServer";

			System.out.println(command);
			Process p = Runtime.getRuntime().exec(command);
			//command = "pwd";
			//Runtime.getRuntime().exec(command);
			
			System.out.println("Wait for = " + p.waitFor());
			
			
			System.out.println("END OF PROJECT 3 EXECUTION");
		}
		catch (Exception ex) 
		{
			System.out.println(ex);
		}
	}
}
