import java.io.BufferedReader;
import java.io.FileReader;


public class Message {
	
	/* 
	 * Message format: 
	 * 
	 * First 4 bytes: Length of the message
	 * Next 4 bytes: Type of message
	 * Each 4 bytes = one number
	 */

	public final int ARRAY = 1;
	public final int DESTROY = 0;
	public final int SENDNEXT = 2;
	public byte[] byteMessage;
	public Matrix matrix = null;
	public int messageType;
	public int messageLength;
	
	
	public Message()
	{
		messageType = -1;
		messageLength = 0;
	}
	
	
	public Message(String message)
	{
		if(message.equalsIgnoreCase("DESTROY"))
		{
			messageType = DESTROY;
			messageLength = 1;
			byte[] byteMessageLength = ConversionUtil.intToByteArray(messageLength);
			byte[] byteMessageType = ConversionUtil.intToByteArray(messageType);
			
			byteMessage = new byte[messageLength * 8];
			System.arraycopy(byteMessageLength, 0, byteMessage, 0, 4);
			System.arraycopy(byteMessageType, 0, byteMessage, 4, 4);			
		}
		if(message.equalsIgnoreCase("SENDNEXT"))
		{
			messageType = SENDNEXT;
			messageLength = 2;
			
			byte[] byteMessageLength = ConversionUtil.intToByteArray(messageLength);
			byte[] byteMessageType = ConversionUtil.intToByteArray(messageType);
			
			byteMessage = new byte[messageLength * 8];
			System.arraycopy(byteMessageLength, 0, byteMessage, 0, 4);
			System.arraycopy(byteMessageType, 0, byteMessage, 4, 4);	
		}
		
		
	}
	
	public Message(byte[] byteMessage)
	{
		this.byteMessage = byteMessage;
	}
	
	public Message(Matrix A)
	{
		//System.out.println("A.M === " + A.M);
		int totalLength = A.M * A.M * 8 + 4 + 4	 ;
		messageLength = (totalLength) / 8;
		messageType = ARRAY;
		
		//System.out.println("MessageLength sending = "+messageLength);
		byte[] byteMessageLength = new byte[4];
		byteMessageLength = ConversionUtil.intToByteArray(messageLength);
		byte[] byteMessageType = new byte[4];
		byteMessageType = ConversionUtil.intToByteArray(messageType);
		
		byteMessage = new byte[totalLength];
		//System.out.println("Total length = " + totalLength);
		System.arraycopy(byteMessageLength, 0, byteMessage, 0, 4);
		System.arraycopy(byteMessageType, 0, byteMessage, 4, 4);			
	
		double[] valueArray = A.toArray();
		int pos = 8;
		
		byte[] valueByte = new byte[8];
		for(int i = 0; i < A.M * A.M; i++)
		{
			String value = "" + valueArray[i];
			/*if(value.length() < 8)
				value = " "+ valueArray[i];
			*/
			while(value.length() < 8)
				value = " " + value;
			
			
			valueByte = value.getBytes();
		
			System.arraycopy(valueByte, 0, byteMessage, pos, valueByte.length);
			pos += 8;
			
		}
		valueByte = null;
	//	System.out.println("Encoding done");
		
	}
	
	
	
	public Matrix decodeMessage(byte[] byteMessage)
	{
		Matrix A;
		
		
		//System.out.println("ByteMessageLength=" + byteMessage.length);
		byte[] byteMessageLength = new byte[4];
		byte[] byteMessageType = new byte[4];
		
		System.arraycopy(byteMessage, 0, byteMessageLength, 0, 4);
		System.arraycopy(byteMessage, 4, byteMessageType, 0, 4);
		messageLength = ConversionUtil.byteArrayToInt(byteMessageLength);
		messageType = ConversionUtil.byteArrayToInt(byteMessageType);
		
		//System.out.println("MessageLength = " + messageLength+",  MessageType =" + messageType);
		
		double[] valueArray = new double[messageLength - 1];
		byte[] value = new byte[8];
		
		for(int i = 0 ; i < messageLength - 1 ; i++)
		{
			//System.out.println("i = " + i);
			System.arraycopy(byteMessage, (i+1) * 8, value, 0, 8);
			String valueStr = new String(value);
			valueArray[i] = Double.parseDouble(valueStr);
			valueStr = null;
			
		}
		A = new Matrix(valueArray);
		valueArray = null;
		value = null;
		return A;

	}	
	
	public Matrix decodeMessage()
	{
		Matrix A;
		
		
		//System.out.println("ByteMessageLength=" + byteMessage.length);
		byte[] byteMessageLength = new byte[4];
		byte[] byteMessageType = new byte[4];
		
		System.arraycopy(byteMessage, 0, byteMessageLength, 0, 4);
		System.arraycopy(byteMessage, 4, byteMessageType, 0, 4);
		messageLength = ConversionUtil.byteArrayToInt(byteMessageLength);
		messageType = ConversionUtil.byteArrayToInt(byteMessageType);
		
		//System.out.println("MessageLength = " + messageLength+",  MessageType =" + messageType);
		int n = (int)(messageLength*4 );
		
		double[] valueArray = new double[messageLength - 1];
		byte[] value = new byte[8];
		
		for(int i = 0 ; i < messageLength - 1 ; i++)
		{
		//	System.out.println("i = " + i);
			System.arraycopy(byteMessage, (i+1) * 8, value, 0, 8);
			String valueStr = new String(value);
			valueArray[i] = Double.parseDouble(valueStr);	
			valueStr = null;
		}
		A = new Matrix(valueArray);
		valueArray = null;
		value = null;
			
		return A;
	}	
	
	public byte[] encodeMessage()
	{
		return byteMessage;
	}
	
	public static  Matrix getMatrix(String inputFile, int dimension)
	{
		Matrix A;
		String st;
		double[] matArray = new double[dimension * dimension];
		int matCounter = 0 ;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			while((st = in.readLine())!= null)
			{
			//	System.out.println("-->" + st);
				 String[] tokens = st.split("[ ]");//("\\s+");
				 for(int i = 0 ; i < dimension; i ++)
				 {
					 matArray[matCounter++] = Double.parseDouble(tokens[i]);
					// System.out.println(matArray[matCounter - 1]);
				 }
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		A = new Matrix(matArray);
		matArray = null;
		
		return A;
	}
	
	
	public static void main(String[] args)
	{
		
		Matrix A = getMatrix("A.dat",4);
		A.print();
		Message m = new Message(A);
		
		Matrix B = m.decodeMessage();
		B.print();
		
		
	}

}
