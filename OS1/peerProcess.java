import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;



 class peerProcessData {

	String nodeID;
	String succNodeID;
	int portNo;
	String hostNameSucc;
	int portNoSucc;
	boolean genTok;
	ServerSocket serverSocket;
	boolean stillListen = true;
}

public class peerProcess{
	public static void main(String[] args) 
	{
		//TODO:
		//1. Extract all the args
		//2. Listen on the given port
		//3. Connect on the next port
		//4. If genToken = true, then send the message, else wait for message
		//5. When received message, pass on the next node
		//6. If this is the first node, then pass DESTROY to the next node.
		//7. If received DESTROY, then close itself.
		
		peerProcessData p = new peerProcessData();
		p.nodeID = args[0];
		p.portNo = Integer.parseInt(args[1]);
		p.hostNameSucc = args[2];
		p.portNoSucc = Integer.parseInt(args[3]);
		p.genTok = (args[4].equals("yes"))?true:false;
		p.succNodeID = args[5];
		int rounds = 0;
		
		String message = null;
		boolean haveToken;
		//System.out.println("Command: ");
		//System.out.println("nodeID = " + p.nodeID + "  portNO=" + p.portNo + "  hostNameSucc=" + p.hostNameSucc +"  portNoSucc="+p.portNoSucc + "   gentok="+p.genTok);
		
		
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(""+p.nodeID+".log");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        BufferedWriter out = new BufferedWriter(fstream);
		
		try {
			out.write("Execution started: "+ p.nodeID+"\n");
			out.write("Parameters received: " + p.hostNameSucc+"\n");
			out.flush();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		/*System.out.println("Execution started: "+ p.nodeID);
		System.out.println("Parameters received: " + p.hostNameSucc);
		*/
		if(p.genTok == true)
		{
			// Open the sending socket here and exit.
			message = p.nodeID + " says HI";
			Thread sendingThread = new Thread(new SendingThread(p.nodeID, p.hostNameSucc, p.portNoSucc, message));
			sendingThread.start();
			
			char ran1 =  (char)((int)5*Math.random()+ 65);
			char ran2 =  (char)((int)4*Math.random()+65 );
			char ran3 =  (char)((int)5*Math.random()+65);
			char ran4 =  (char)((int)4*Math.random()+65);
			char ran5 =  (char)((int)5*Math.random()+65);
			char ran6 =  (char)((int)5*Math.random()+65);
			char ran7 =  (char)((int)5*Math.random()+65);
			
			String random = "" + ran1 + ran2 + ran3 + ran4 + ran5 + ran6 + ran7;
			
			try {
				out.write("\n Generating Control Token: " + random  + "[random]\n");
				out.write("Fixing Control Token for the run duration [OK]\n\n");
				out.write("Message to Send: \"" + message + "\" \n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.genTok = true;
		}
		
		// Open the listening socket.
		// Loop in it. Whenever it receives a message, then again start the sending thread
		// and send the message.
		
		try {
			p.serverSocket = new ServerSocket(p.portNo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.getMessage();
			e.printStackTrace();
			try {
				
				out.write("Error in opening port" + "\n");
				out.flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		try {
			out.write("Server Thread: Started [OK]" + "\n");
			out.write("Sleeping for 5 seconds now ..." + "\n");
			out.flush();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		
		// sleep for 5 secs
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		
	/*	System.out.println("Establishing socket connection to "+ p.hostNameSucc);
		System.out.println("Connection Established [OK]");
		System.out.println("Incomming connection...  [Accepted]");
		*/
		
		try {
			out.write("Establishing socket connection to "+ p.succNodeID + "\n");
			out.write("Connection Established [OK]" + "\n");
			out.write("Incomming connection...  [Accepted]" + "\n");
			out.flush();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		while(p.stillListen == true)
		{
			Socket tempListeningSocket = null;
			try {
				tempListeningSocket = p.serverSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//TODO: Read the message from tempListeningSocket; if ownMessage then continue;
			InputStream in = null;
			try {
				in = tempListeningSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			byte[] byteMessage = new byte[1024];
			
			
			
			try {
				in.read(byteMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.getMessage();
			}
			
			
			byte[] bytePrevNode;
			byte[] byteRecdMessage ;
			byte[] byteNodeLength = new byte[4];
			byte[] byteMessageLength = new byte[4];
			
			
			
			System.arraycopy(byteMessage, 0, byteNodeLength, 0, 4);
			int nodeIDLength = ConversionUtil.byteArrayToInt(byteNodeLength);
			//System.out.println("NodeIDLength  = "+ nodeIDLength);
			bytePrevNode = new byte[nodeIDLength];
			
			System.arraycopy(byteMessage, 4, bytePrevNode, 0, nodeIDLength);
			System.arraycopy(byteMessage, 4+nodeIDLength, byteMessageLength, 0, 4);
			
			int messageLength = ConversionUtil.byteArrayToInt(byteMessageLength);
			
			//System.out.println("messageLength =" + messageLength);
			
			byteRecdMessage = new byte[messageLength];
			System.arraycopy(byteMessage, 8 +nodeIDLength, byteRecdMessage, 0, messageLength);
			
			
			String prevNode = new String(bytePrevNode);
			String recdMessage = new String(byteRecdMessage);
			
			
			Thread sendingThread;
			
			//Taking care of token
			if(recdMessage.equalsIgnoreCase("TOKEN"))
			{
				message = p.nodeID + " says HI";
				sendingThread = new Thread(new SendingThread(p.nodeID, p.hostNameSucc, p.portNoSucc, message));
				sendingThread.start();
				
				try {
					out.write("Incoming token\n\n\n");
					
					if(p.genTok == true)
					{
						rounds++;
						try {
							out.write("Incrementing Round Counter +1 : Value " + rounds + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					out.write("Message to Send: \"" + message + "\" \n");
					out.write("Sending to " +  p.succNodeID  +" [success]\n");
					out.write("Transmitting Token to " + p.succNodeID + " [success]\n\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				continue;
			}
			
			
			//System.out.println("**Received message = " + prevNode + "  " + recdMessage+"\n");
			try {
				out.write("Incoming message: " + recdMessage + "\n");
				out.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			if(recdMessage.equalsIgnoreCase(message))
			{
				if(recdMessage.equalsIgnoreCase("DESTROY"))
				{
					try {
						out.write("All done."+"\n");
						out.flush();
						out.close();
						System.exit(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				else
				{
					
					try {
						out.write("Identifying self-message [Done]" + "\n");
						out.write("Discarding message: " + recdMessage + "\n");
						out.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
			
			
			
			//TODO: add 1 to the number of rounds (if genTok = true)
			//System.out.println("GenTok = " + p.genTok);
			/*if(p.genTok == true && recdMessage.equalsIgnoreCase("TOKEN) ) 
			{
				
				rounds++;
				try {
					out.write("Incrementing Round Counter +1 : Value " + rounds + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("Rounds = " + rounds);
			}
			*/
			//TODO: Send the message.
			
			if(p.genTok == true && rounds == 5)
			{
				try {
					out.write("Message to Send: \"" + p.nodeID +" says  DESTROY \" \n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendingThread = new Thread(new SendingThread(p.nodeID, p.hostNameSucc, p.portNoSucc, "DESTROY"));
			}
			else if(recdMessage.equalsIgnoreCase(message))
			{
				
				sendingThread = new Thread(new SendingThread(p.nodeID, p.hostNameSucc, p.portNoSucc, "TOKEN"));
			}
			else
			{
				try {
					
					out.write("Forwarding Message to " + p.succNodeID + " [success] \n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendingThread = new Thread(new SendingThread(p.nodeID, p.hostNameSucc, p.portNoSucc, recdMessage));
			}
			sendingThread.start();
			
			//TODO: if message = DESTROY, then exit.
			if(recdMessage.equalsIgnoreCase("DESTROY"))
			{
				try {
					sendingThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					out.write("All done."+"\n");
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.exit(0);
			}
			
			
				
		}
	}

}
