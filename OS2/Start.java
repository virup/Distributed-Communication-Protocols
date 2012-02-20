import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

class RemotePeerInfo{
	String nodeid;
	String hostname;
	int port;
	
	
	
	public RemotePeerInfo()
	{
		nodeid = null;
		hostname = null;
		port = 9999;
		
		
	}
	
	public RemotePeerInfo(String nodeID, String hostName, int port)
	{
		this.nodeid = nodeID;
		this.hostname = hostName;
		this.port = port;
	}
}

public class Start {
	public Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
	public Vector<Process> peerProcesses = new Vector<Process>();
	
	int dimension = 0;
	String inputFile = null;
	int exponent = 0;

	
	public void getConfiguration()
	{
		String st;
		System.out.println(System.getProperty("user.dir"));
		try 
		{
			BufferedReader in = new BufferedReader(new FileReader("config.ini"));
			String tempNodeID = null ;
			String tempHostName = null;
			String tempPort = null;
			String tempDimension = null;
			String tempExponent = null;
			String tempInputFile = null;
			
			boolean flag = false;
			
			
			
			while((st = in.readLine()) != null) 
			{	
				
				if(st.charAt(0)== '#')
					continue;
				if(st.length() == 0)
					continue;
				
				 String[] tokens = st.split("\\s+");
				 
				 System.out.println(st);
				 if(flag == false)
				 {
					 if(tokens[0].equalsIgnoreCase("input"))
					 {	

						 
						 tempInputFile = tokens[3];
						 inputFile = tempInputFile;
						 System.out.println(inputFile);
						 
					 }
					 if(tokens[0].equalsIgnoreCase("dimension"))
					 {
						 tempDimension = tokens[4];
						 dimension = Integer.parseInt(tempDimension);
						 System.out.println(dimension);
					 }
					 if(tokens[0].equalsIgnoreCase("exponent"))
					 {
						 tempExponent = tokens[2];
						 exponent = Integer.parseInt(tempExponent);
						 System.out.println(exponent);
						 flag = true;
					 }
				 }
				 else
				 {
					 tempNodeID = tokens[0];
					 tempHostName = tokens[1];
					 tempPort = tokens[2];
					 
					 peerInfoVector.addElement(new RemotePeerInfo(tempNodeID, tempHostName, Integer.parseInt(tempPort)));
					// peerList.put(tempNodeID, new RemotePeerInfo(tempNodeID, tempHostName, Integer.parseInt(tempPort)));
					 
					 tempNodeID = null;
					 tempHostName = null;
					 tempPort = null;
				 }				 
			}				 
			in.close();
		}
		catch (Exception ex) 
		{
			System.out.println(ex.toString());
		}

	}
	
	
	
	
	
	public static void main(String[] args)
	{
		try 
		{
			Start myStart = new Start();
			
			myStart.getConfiguration();
					
			// get current path
			String path = System.getProperty("user.dir");
						
			// start clients at remote hosts
			for (int i = myStart.peerInfoVector.size()-1; i >=0 ; i--) 
			{
				RemotePeerInfo pInfo = (RemotePeerInfo) myStart.peerInfoVector.elementAt(i);
				
				//System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
				String command = "ssh " + pInfo.hostname + " cd " + path + "; java ComputingAgent " + pInfo.nodeid + " " + pInfo.port;; 
				
				myStart.peerProcesses.add(Runtime.getRuntime().exec(command));
				
				System.out.println(command);
			}		
		
			//String command = "ssh " + "localhost" + " cd " + path + "; java CoordinatingAgent";
			System.out.println("Starting all remote peers has done." );
			
			String command = "java CoordinatingAgent >temp";
			System.out.println(command);
			Process p = Runtime.getRuntime().exec(command);
			//command = "pwd";
			//Runtime.getRuntime().exec(command);
			
			System.out.println("Wait for = " + p.waitFor());
			
			
			System.out.println("END OF PROJECT 2 EXECUTION");
		}
		catch (Exception ex) 
		{
			System.out.println(ex);
		}
	}
}
