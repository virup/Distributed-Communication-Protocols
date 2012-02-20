import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class SendingThread implements Runnable 
{

	public Socket sendingSocket;
	public int sendingPort;
	String sendingHostname;
	Matrix A, B, C ;
	int messageType = 0;
	int matNum;
	
	
	public SendingThread(String sendingHostname, int sendingPort, Matrix A, Matrix B, int c)
	{
		this.sendingHostname = sendingHostname;
		this.sendingPort = sendingPort;
		this.A = A;
		this.B = B;
		this.matNum = c;
		messageType = 1;
	}
	
	public SendingThread(String sendingHostname, int sendingPort, String message)
	{
		this.sendingHostname = sendingHostname;
		this.sendingPort = sendingPort;
		if(message.equalsIgnoreCase("DESTROY"))
			messageType = -1;
	}
	
	
	@Override
	public void run() {
		// Send the actual message and exit.
		
		try {
			sendingSocket = new Socket(sendingHostname, sendingPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Open the output stream
		OutputStream out = null;
		InputStream in = null;
		try {
			if(sendingSocket.isConnected())
			out = sendingSocket.getOutputStream();
			in = sendingSocket.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if(messageType == -1)
		{
			Message des = new Message("DESTROY");
			
			try {
				out.write(des.encodeMessage());
				out.close();
				sendingSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
		else
		{
		//TODO: Send the message
		
		//System.out.println("printing A");
		//A.print();
		
		Message firstMatrixMessage = new Message(A);
		Message secondMatrixMessage = new Message(B);
		
		
				
		
		
		
		//push the message in
		try {
			out.write(firstMatrixMessage.encodeMessage());
			byte[] dummyByte = new byte[1024]; 
			in.read(dummyByte);
			out.write(secondMatrixMessage.encodeMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		 	
		//read the computed matrix back
		
		
		byte[] byteMessage = null;
		
		byte[] tempByteMessage = new byte[1024];
		byte[] tempByteMessageLength = new byte[4];
		
		int messageLength = 1;
		int byteCount = 0;
		int currentReadMessageLength = 0;
		
		while(byteCount < messageLength)
		{
			try 
			{
				
				currentReadMessageLength = in.read(tempByteMessage);
				//System.out.println("curReadLen ="+currentReadMessageLength);
				
				if(messageLength == 1)
				{
					System.arraycopy(tempByteMessage, 0, tempByteMessageLength, 0, 4);
					messageLength = ConversionUtil.byteArrayToInt(tempByteMessageLength);
					messageLength = messageLength * 8;
					//System.out.println("msgLen ="+messageLength);
					byteMessage = new byte[messageLength];
					
				}
				//System.out.println("curReadMsgLen="+currentReadMessageLength + "  byteCount="+byteCount);
				System.arraycopy(tempByteMessage, 0, byteMessage, byteCount, currentReadMessageLength);
				byteCount += currentReadMessageLength;
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.getMessage();
			}
		}
		
		
		
	
		
		Message returnedMatrixMessage = new Message(byteMessage);
		
		switch(matNum)
		{
		case 1: StoreMatrix.M1 = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		case 2:StoreMatrix.M2  = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		case 3:StoreMatrix.M3 = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		case 4:StoreMatrix.M4 = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		case 5:StoreMatrix.M5 = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		case 6:StoreMatrix.M6 = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		case 7:StoreMatrix.M7 = new Matrix(returnedMatrixMessage.decodeMessage(byteMessage));
			break;
		}
				
		
	}}

}
