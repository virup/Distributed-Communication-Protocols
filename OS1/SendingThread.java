import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class SendingThread implements Runnable 
{

	public Socket sendingSocket;
	public int sendingPort;
	public String sendingMessage;
	public String sendingHostname;
	public String ownNodeID;
	
	
	public SendingThread( String ownNodeID, String sendingHostname, int sendingPort, String sendingMessage)
	{
		this.ownNodeID = ownNodeID;
		this.sendingHostname = sendingHostname;
		this.sendingPort = sendingPort;
		this.sendingMessage = sendingMessage;
	}
	
	
	//@Override
	public void run() {
		// Send the actual message and exit.
		
		try {
			/*System.out.println("Sending Thread: " + sendingHostname + ":" + sendingPort);
			System.out.println("Sending message ="+ sendingMessage);
			*/
			sendingSocket = new Socket(sendingHostname, sendingPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//TODO: Send the message
		
		/*
		 * 
		 * first 2 bytes, the nodeID
		 * next bytes : message
		 */
		
		//Create the message.
		String nodeID = ownNodeID;
		int nodeIDlength = nodeID.length();
		int messageLength = sendingMessage.length();
		
		
		
		
		byte []byteNodeIDLength = ConversionUtil.intToByteArray(nodeIDlength);
		byte []byteMessageLength = ConversionUtil.intToByteArray(messageLength);
		//System.out.println("NodeIDLength = "+ strNodeIDLength);
		byte[] finalMessage = new byte[byteNodeIDLength.length + nodeIDlength + byteMessageLength.length + messageLength];

		
		//System.out.println("byteNodeIDLength = "+byteNodeIDLength.length);
		//System.out.println("byteMessageLength = "+ byteMessageLength.length);
		
		//System.out.println("Sending message  = "+ new String(sendingMessage.getBytes()));
		System.arraycopy(byteNodeIDLength, 0, finalMessage, 0, 4);
		System.arraycopy(nodeID.getBytes(),0,finalMessage, 4, nodeIDlength);
		System.arraycopy(byteMessageLength, 0, finalMessage, 4+nodeIDlength, 4);
		System.arraycopy(sendingMessage.getBytes(),0, finalMessage,4+nodeIDlength+4,messageLength);
		
		OutputStream out = null;
		try {
			if(sendingSocket.isConnected())
			out = sendingSocket.getOutputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			if(sendingSocket.isConnected())
				out.write(finalMessage);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		 	
		
		try {
			sendingSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
