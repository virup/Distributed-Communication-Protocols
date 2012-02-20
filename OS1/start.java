import java.io.*;
import java.util.*;

class RemotePeerInfo{
	String nodeid;
	String hostname;
	int port;
	String successor;
	String generateToken;
	
	
	
	public RemotePeerInfo()
	{
		nodeid = null;
		hostname = null;
		port = 9999;
		successor = null;
		generateToken = null;
		
	}
	
	public RemotePeerInfo(String nodeID, String hostName, int port, String successor, String generateToken)
	{
		this.nodeid = nodeID;
		this.hostname = hostName;
		this.port = port;
		this.successor = successor;
		this.generateToken = generateToken;
	}

	
	
	
}

public class start {
	public Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
	public Vector<Process> peerProcesses = new Vector<Process>();
	public static Hashtable<String, RemotePeerInfo> peerList = new Hashtable<String, RemotePeerInfo>();

	public void getConfiguration()
	{
		String st;
		System.out.println(System.getProperty("user.dir"));
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader("config.ini"));
			int i =0;
			String tempNodeID = null ;
			String tempHostName = null;
			String tempPort = null;
			String succ = null;
			String tempGenerateTok = null;
			
			while((st = in.readLine()) != null) 
			{	
				System.out.println("l");
				if(st.charAt(0)== '#')
					continue;
				if(st.length() == 0)
					continue;
				 String[] tokens = st.split("\\s+");
				 if(tokens[0].equalsIgnoreCase("node-id:"))
				 {
					 tempNodeID = tokens[1];
					 i++;
				 }
				 if(tokens[0].equalsIgnoreCase("host-name:"))
				 {
					 tempHostName = tokens[1];
					 i++;
				 }
				 if(tokens[0].equalsIgnoreCase("port:"))
				 {
					tempPort = tokens[1];
					i++;
				 }
				 if(tokens[0].equalsIgnoreCase("successor:") || tokens[0].equalsIgnoreCase("successor-node:" ))
				 {
					 succ = tokens[1];
					 i++;
				 }
				 if(tokens[0].equalsIgnoreCase("generate-token:"))
				 {
					 tempGenerateTok  = tokens[1];
					 i++;
				 }
				 
				 if(i == 5)
				 {
					 int port = Integer.parseInt(tempPort);
					 //boolean gen = (tempGenerateTok.equalsIgnoreCase("true"))?true:false;
							
					peerInfoVector.addElement(new RemotePeerInfo(tempNodeID, tempHostName, port, succ, tempGenerateTok)); 
				    peerList.put(tempNodeID, new RemotePeerInfo(tempNodeID, tempHostName, port, succ, tempGenerateTok));
				    
				    tempNodeID = null ;
					tempHostName = null;
					tempPort = null;
					succ = null;
					tempGenerateTok = null;
					i = 0;
		        }
			}
			
			in.close();
		}
		catch (Exception ex) 
		{
			System.out.println(ex.toString());
		}
	}
	
	/**
	 * @param args
	 **/
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
				String command = "ssh " + pInfo.hostname + " cd " + path + "; java peerProcess " + pInfo.nodeid + " " + pInfo.port + " " + peerList.get(pInfo.successor).hostname +" " +peerList.get(pInfo.successor).port + " " + pInfo.generateToken + " "+pInfo.successor; 
				//System.out.println(command);
				//TODO: START THIS PART
				myStart.peerProcesses.add(Runtime.getRuntime().exec(command));
				
				System.out.println(command);
			}		
			System.out.println("Starting all remote peers has done." );
			Thread.sleep(5000);
			System.out.println("END OF PROJECT 1 EXECUTION");
		}
		catch (Exception ex) 
		{
			System.out.println(ex);
		}
	}


}
