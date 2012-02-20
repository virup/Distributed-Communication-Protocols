import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Vector;


public class CoordinatingAgent {


	public Vector<RemotePeerInfo> peerInfoVector = new Vector<RemotePeerInfo>();
	public Vector<Process> peerProcesses = new Vector<Process>();

	int dimension = 0;
	String inputFile = null;
	int exponent = 0;

	public Matrix getMatrix(String inputFile, int dimension)
	{
		Matrix A;
		String st;
		double[] matArray = new double[dimension * dimension];
		int matCounter = 0 ;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			while((st = in.readLine())!= null)
			{
				//System.out.println("-->" + st);
				String[] tokens = st.split("\\s+");
				for(int i = 0 ; i < dimension; i ++)
				{
					matArray[matCounter++] = Double.parseDouble(tokens[i]);
					//System.out.println(matArray[matCounter-1]);
				}

			}
		} catch (Exception e) {
			
			// Generate Matrix Randomly
			A = new Matrix(Matrix.random(dimension));
			//System.out.println("Random Mat:");
			//A.print();
			return A;
		}
		A = new Matrix(matArray);
		//A.print();

		return A;
	}


	public void getConfiguration()
	{
		String st;
		//System.out.println(System.getProperty("user.dir"));

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
				//System.out.println(st);
				if(st.charAt(0)== '#')
					continue;
				if(st.length() == 0)
					continue;

				String[] tokens = st.split("\\s+");

				if(flag == false)
				{
					if(tokens[0].equalsIgnoreCase("input"))
					{	
						tempInputFile = tokens[3];
						inputFile = tempInputFile;

					}
					if(tokens[0].equalsIgnoreCase("dimension"))
					{
						tempDimension = tokens[4];
						dimension = Integer.parseInt(tempDimension);
					}
					if(tokens[0].equalsIgnoreCase("exponent"))
					{
						tempExponent = tokens[2];
						exponent = Integer.parseInt(tempExponent);
						flag = true;
					}
				}
				else
				{
					tempNodeID = tokens[0];
					tempHostName = tokens[1];
					tempPort = tokens[2];

					// System.out.println(" " + tempNodeID + "  " +  tempHostName + "  " +  Integer.parseInt(tempPort));
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
			//System.out.println(ex.toString());
		}

	}




	public static void main(String[] args)
	{
		//StoreMatrix  m = new StoreMatrix();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		CoordinatingAgent ca = new CoordinatingAgent();


		FileWriter fstream = null;
		try {
			fstream = new FileWriter("CoordinatingAgent.log");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		BufferedWriter fout = new BufferedWriter(fstream);


		//Extract the arguments;
		ca.getConfiguration();

		//	System.out.println("InputFile="+ca.inputFile + "   Dimension = " + ca.dimension);
		Matrix A = new Matrix(ca.getMatrix(ca.inputFile, ca.dimension));
		Matrix B = new Matrix(A);
		Matrix[][] subMatrixA;
		Matrix[][] subMatrixB;





		int p = 2 ;
		try {
			fout.write("Coordinating Agent Started [OK]\n");
			fout.write("Matrix read [OK]\n");
			fout.write("Exponent="+ca.exponent+"\n\n");
			fout.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(ca.exponent==0)
		{
			//	System.out.println("In p == 0");
			B = new Matrix(Matrix.createUnitMat(A.M));
		}
		else if(ca.exponent == 1)
		{
			//System.out.println("Exponent = 1");
		}
		else
		{
			while(p <= ca.exponent)
			{
				//System.out.println("p = " + p);

//				try {
//					fout.write("p = " + p+"\n");
//					fout.flush();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}


				subMatrixA = A.sliceMatrix();
				subMatrixB = B.sliceMatrix();


//				try {
//					fout.write("Sending the threads\n");
//					fout.flush();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				Matrix t1 = subMatrixA[0][0].plus(subMatrixA[1][1]);
				Matrix t2 = subMatrixB[0][0].plus(subMatrixB[1][1]);
				Thread sendingThread1 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(0).hostname,
						ca.peerInfoVector.elementAt(0).port ,
						t1,
						t2,
						1));



				t1 = subMatrixA[1][0].plus(subMatrixA[1][1]);
				Thread sendingThread2 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(1).hostname,
						ca.peerInfoVector.elementAt(1).port ,
						t1,
						subMatrixB[0][0],
						2));

				t2 = subMatrixB[0][1].minus(subMatrixB[1][1]);
				Thread sendingThread3 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(2).hostname,
						ca.peerInfoVector.elementAt(2).port ,
						subMatrixA[0][0],
						t2,
						3));

				t2 = subMatrixB[1][0].minus(subMatrixB[0][0]);
				Thread sendingThread4 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(3).hostname,
						ca.peerInfoVector.elementAt(3).port ,
						subMatrixA[1][1],
						t2,
						4));

				t1 = subMatrixA[0][0].plus(subMatrixA[0][1]);
				Thread sendingThread5 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(4).hostname,
						ca.peerInfoVector.elementAt(4).port ,
						t1,
						subMatrixB[1][1],
						5));

				t1 = subMatrixA[1][0].minus(subMatrixA[0][0]);
				t2 = subMatrixB[0][0].plus(subMatrixB[0][1]);
				Thread sendingThread6 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(5).hostname,
						ca.peerInfoVector.elementAt(5).port ,
						t1,
						t2,
						6));

				t1 = subMatrixA[0][1].minus(subMatrixA[1][1]);
				t2 = subMatrixB[1][0].plus(subMatrixB[1][1]);
				Thread sendingThread7 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(6).hostname,
						ca.peerInfoVector.elementAt(6).port ,
						t1,
						t2,
						7));

				try {
					fout.write("Sending matrix subparts.\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				sendingThread1.start();
				sendingThread2.start();
				sendingThread3.start();
				sendingThread4.start();
				sendingThread5.start();
				sendingThread6.start();
				sendingThread7.start();

				try {
					fout.write("Waiting for result to come.\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					sendingThread1.join();
					sendingThread2.join();
					sendingThread3.join();
					sendingThread4.join();
					sendingThread5.join();
					sendingThread6.join();
					sendingThread7.join();


				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					fout.write("SubMatrix results received[OK].\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				p++;


				/*System.out.println("M1:");StoreMatrix.M1.print();
				System.out.println("M2:");StoreMatrix.M2.print();
				System.out.println("M3:");StoreMatrix.M3.print();
				System.out.println("M4:");StoreMatrix.M4.print();
				System.out.println("M5:");StoreMatrix.M5.print();
				System.out.println("M6:");StoreMatrix.M6.print();
				System.out.println("M7:");StoreMatrix.M7.print();
				 */

				Matrix[][] C = new Matrix[2][2];

				Matrix Temp = new Matrix(StoreMatrix.M1.plus(StoreMatrix.M4));
				//Temp.print();
				Temp = new Matrix(Temp.minus(StoreMatrix.M5));
				//Temp.print();
				Temp = new Matrix(Temp.plus(StoreMatrix.M7));
				//Temp.print();

				try {
					fout.write("Constructing C.\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				C[0][0] = new Matrix(Temp); //new Matrix(StoreMatrix.M1.plus(StoreMatrix.M4).minus(StoreMatrix.M5).plus(StoreMatrix.M7));


				C[0][1] = new Matrix(StoreMatrix.M3.plus(StoreMatrix.M5));
				C[1][0] = new Matrix(StoreMatrix.M2.plus(StoreMatrix.M4));

				Temp = new Matrix(StoreMatrix.M1.minus(StoreMatrix.M2));
				Temp = new Matrix(Temp.plus(StoreMatrix.M3));
				Temp = new Matrix(Temp.plus(StoreMatrix.M6));
				C[1][1] = new Matrix(Temp); //new Matrix(StoreMatrix.M1.minus(StoreMatrix.M2).plus(StoreMatrix.M3).plus(StoreMatrix.M6));
				//C[1][1].print();

				Temp = null;
				B = null;
				try {
					fout.write("C constructed.\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				B = new Matrix(Matrix.joinMatrix(C));
				try {
					fout.write("C:	\n");
					B.printToFile(fout);
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//System.out.println("========");
				//B.print();

				try {
					fout.write("This round is over\n\n\n\n");
					fout.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		//TODO: Send Destroy message
		//System.out.println("Making first destry thread");;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Thread sendingThread1 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(0).hostname,
				ca.peerInfoVector.elementAt(0).port ,
		"DESTROY"));

		Thread sendingThread2 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(1).hostname,
				ca.peerInfoVector.elementAt(1).port ,
		"DESTROY"));
		Thread sendingThread3 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(2).hostname,
				ca.peerInfoVector.elementAt(2).port ,
		"DESTROY"));
		Thread sendingThread4 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(3).hostname,
				ca.peerInfoVector.elementAt(3).port ,
		"DESTROY"));
		Thread sendingThread5 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(4).hostname,
				ca.peerInfoVector.elementAt(4).port ,
		"DESTROY"));
		Thread sendingThread6 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(5).hostname,
				ca.peerInfoVector.elementAt(5).port ,
		"DESTROY"));
		Thread sendingThread7 = new Thread(new SendingThread(ca.peerInfoVector.elementAt(6).hostname,
				ca.peerInfoVector.elementAt(6).port ,
		"DESTROY"));

		try {
			fout.write("Sending the DESTROY messages to Computing Agents [OK]\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		sendingThread1.start();
		sendingThread2.start();
		sendingThread3.start();
		sendingThread4.start();
		sendingThread5.start();
		sendingThread6.start();
		sendingThread7.start();

		try {
			sendingThread1.join();
			sendingThread2.join();
			sendingThread3.join();
			sendingThread4.join();
			sendingThread5.join();
			sendingThread6.join();
			sendingThread7.join();


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Show the final Matrix

		//System.out.println("=== Final =======");
		//B.print();

		try {
			fout.write("\n\nFinal Result Matrix:\n");
			fout.write("==================\n");
			//fout.write(B.getStr());
			B.printToFile(fout);
			fout.write("\n");
			fout.write("Exiting program");
			fout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}



}
