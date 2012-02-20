import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ComputingAgent {

	public Matrix A;
	public Matrix B;
	public Matrix C;
	
	
		
	public static void main(String[] args)
	{
		
		
		
		
		//TODO: 1. Extract arguments
		int port;
		String nodeID;
		
		nodeID = args[0];
		port = Integer.parseInt(args[1]);
		
		//System.out.println("nodeID = " + nodeID + "  port = " + port);
		
		
		
		FileWriter fstream = null;
		try {
			fstream = new FileWriter(""+nodeID+".log");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        BufferedWriter fout = new BufferedWriter(fstream);
		
		try {
			fout.write("Execution started: "+ nodeID+"\n");
			fout.write("Parameters received: " + port+"\n");
			fout.flush();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
				
		
		
		
		
		
		//TODO: 2. Start Server
		ServerSocket serverSocket = null;
		try {
			
			serverSocket = new ServerSocket(port);
			fout.write("Server Socket Opened\n");
			fout.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.getMessage();
		//	e.printStackTrace();
			
			try {
				fout.write("Server Socket NOT Opened\n");
				fout.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
//		try {
//			fout.write("Waiting for accepting connection");
//			fout.flush();
//		} catch (IOException e4) {
//			// TODO Auto-generated catch block
//			e4.printStackTrace();
//		}
		
		//TODO: 3. Accept Connection
		Socket tempListeningSocket = null;
		
		
		InputStream in = null;
		OutputStream out = null;
		
		while(true)
		{
			//TODO: 4. Accept Data
			
			try {
				tempListeningSocket = serverSocket.accept();
				fout.write("Accepted connection [OK]\n");
				fout.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			try {
				in = tempListeningSocket.getInputStream();
				out = tempListeningSocket.getOutputStream();
//				fout.write("Streams opened\n");
//				fout.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			byte[] byteMessage = null;
						
			byte[] tempByteMessage = new byte[1024];
			byte[] tempByteMessageLength = new byte[4];
			
			int messageLength = 1;
			int byteCount = 0;
			int currentReadMessageLength = 0;
			
			while(byteCount < messageLength)
			{
				try {
					fout.write("Receiving message\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try 
				{
					currentReadMessageLength = in.read(tempByteMessage);
					if(messageLength == 1)
					{
						
//						try {
//							fout.write("MessageLength = 1 \n");
//							fout.flush();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
						
						System.arraycopy(tempByteMessage, 0, tempByteMessageLength, 0, 4);
						messageLength = ConversionUtil.byteArrayToInt(tempByteMessageLength);
						//System.out.println("MessageLength = "+messageLength);
						messageLength = messageLength * 8;
						byteMessage = new byte[messageLength];
						if(messageLength == 8)
						{
							fout.write("Received DESTROY Message.\n");
							fout.write("Closing Input Stream\n");
							fout.flush();
							in.close();
							fout.write("Closing Socket [OK]\n");
							fout.write("Exiting ...");
							tempListeningSocket.close();
							serverSocket.close();
							fout.flush();
							fout.close();
							
							System.exit(1);
						}
						
					}
					
				//	System.out.println("curReadMsgLen=" + currentReadMessageLength);
					System.arraycopy(tempByteMessage, 0, byteMessage, byteCount, currentReadMessageLength);
					byteCount += currentReadMessageLength;
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					e.getMessage();
				}
			}
			
			
			
			
		//	System.out.println("ByteMessageLen = " + byteMessage.length);
			Message firstMatrix = new Message(byteMessage);
			Matrix A = firstMatrix.decodeMessage(byteMessage);
			
			
			try {
				fout.write("Received first Matrix\n");
				A.printToFile(fout);
				fout.write("\n\n");
				fout.flush();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			
			//Tell them to send the second matrix
			try {
				out.write("Send".getBytes());
//				fout.write("Requesting second matrix\n");
//				fout.flush();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			
			
			// Read the second matrix
			byteMessage = null;
			
			tempByteMessage = new byte[1024];
			tempByteMessageLength = new byte[4];
			
			messageLength = 1;
			byteCount = 0;
			currentReadMessageLength = 0;
			
			while(byteCount < messageLength)
			{
				try 
				{
					fout.write("Reading second message\n");
					fout.flush();
					currentReadMessageLength = in.read(tempByteMessage);
					if(messageLength == 1)
					{
						System.arraycopy(tempByteMessage, 0, tempByteMessageLength, 0, 4);
						messageLength = ConversionUtil.byteArrayToInt(tempByteMessageLength);
						messageLength = messageLength * 8;
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
			
			
			
			Message secondMatrix = new Message(byteMessage);
			Matrix B = secondMatrix.decodeMessage();
			
			try {
				fout.write("Received second Matrix\n");
				B.printToFile(fout);
				fout.write("\n\n");
				fout.flush();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			byteMessage = null;
			tempByteMessage = null;
			tempByteMessageLength = null;
			
			try {
				fout.write(A.getStr());
				fout.write(B.getStr());
				fout.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		 //TODO: 5. Compute
			
			try {
				fout.write("Computing result matrix ");
				fout.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Matrix C;
			C = new Matrix(A.times(B));
			//System.out.println("Printing out C Matrix");
			C.print();
			
			//System.out.flush();
			
			try {
				fout.write("[OK].\n");
				fout.write("Result Matrix:\n");
				C.printToFile(fout);
				fout.write("\n\n");
				fout.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		//TODO: 6. Send back data
			Message sendMessage = new Message(C);
			
			
			
			// Send the message now.
			try {
				//System.out.println("Length of returned message= " + sendMessage.encodeMessage().length);
				out.write(sendMessage.encodeMessage());
				fout.write("Sent result matrix\n\n\n");
				fout.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
