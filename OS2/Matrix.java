import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;


public class Matrix {
	
	public int M;
	public double[][] data;
	
	   public Matrix(int M) {
	        this.M = M;
	        data = new double[M][M];
	    }
	   
	   
	   public Matrix(double[] data) {
	      M = (int) Math.sqrt(data.length);
	        this.data = new double[M][M];

	       // System.out.println("M="+ M);
	        for (int i = 0; i < M*M; i++)
	        {
	        		
	        		 
                    this.data[i/M][i%M] = data[i];
	        	
	        }
	    }
	   
	   public Matrix(Matrix A)
	   {
		   this.M = A.M;
		   this.data = new double[M][M];
		   for(int i = 0 ; i < M; i++)
			   for(int j = 0 ; j < M; j++)
				   this.data[i][j] = A.data[i][j];
	   }

	   public Matrix plus(Matrix B) {
		   MathContext mc = new MathContext(4);

	        Matrix A = this;
	        Matrix C = new Matrix(M);

	        for (int i = 0; i < M; i++)
	            for (int j = 0; j < M; j++)
	               // C.data[i][j] = A.data[i][j] + B.data[i][j];
	            	C.data[i][j] = (BigDecimal.valueOf(A.data[i][j]).add(BigDecimal.valueOf(B.data[i][j]))).round(mc).doubleValue();
	        return C;
	    }
	   
	   public Matrix minus(Matrix B) {
		   MathContext mc = new MathContext(4);

	        Matrix A = this;
	        Matrix C = new Matrix(M);

	        for (int i = 0; i < M; i++)
	            for (int j = 0; j < M; j++)
	            	C.data[i][j] = (BigDecimal.valueOf(A.data[i][j]).subtract(BigDecimal.valueOf(B.data[i][j]))).round(mc).doubleValue();
	        return C;
	    }
	   
	   public Matrix times(Matrix B) {
		   MathContext mc = new MathContext(4);
	        Matrix A = this;
	       // if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");

	        Matrix C = new Matrix(A.M);
	        for (int i = 0; i < C.M; i++)
	            for (int j = 0; j < C.M; j++)
	                for (int k = 0; k < A.M; k++)
	                {
	                	//C.data[i][j] += (A.data[i][k] * B.data[k][j]);
	                	C.data[i][j] = (BigDecimal.valueOf(C.data[i][j]).add(BigDecimal.valueOf(A.data[i][k]).multiply(BigDecimal.valueOf(B.data[k][i])))).round(mc).doubleValue();
	                }
	        return C;
	    }
	   
	   public Matrix[][] sliceMatrix()
	   {
		   Matrix[][] subMatrix = {{new Matrix(M/2), new Matrix(M/2)},
				   					{new Matrix(M/2), new Matrix(M/2)}};
		  
		   
	  
		   
		for(int i = 0; i < M/2; i++)
			   for(int j = 0; j < M/2; j++)
				   subMatrix[0][0].data[i][j] = data[i][j];
		
		for(int i = 0; i < M/2; i++)
			for(int j = M/2; j< M; j++)
				subMatrix[0][1].data[i][j-M/2] = data[i][j];
		   
		   
		for(int i = M/2; i < M; i++)
			for(int j = 0; j < M/2; j++)
				subMatrix[1][0].data[i-M/2][j] = data[i][j];
		
		for(int i = M/2; i < M; i++)
			for(int j = M/2; j< M; j++)
				subMatrix[1][1].data[i-M/2][j-M/2] = data[i][j];
		
		return subMatrix;
		   
	   }


	   public static Matrix joinMatrix(Matrix[][] C)
	   {
		   Matrix result = new Matrix(C[0][0].M * 2);
		   int M = result.M/2;
		   
		   for(int i = 0 ; i < M; i++)
			   for(int j = 0 ; j < M; j++)
				   result.data[i][j] = C[0][0].data[i][j];
		   
		   for(int i = 0 ; i < M; i++)
			   for(int j = 0 ; j < M; j++)
				   result.data[i][j+M] = C[0][1].data[i][j];
		   
		   for(int i = 0 ; i < M; i++)
			   for(int j = 0 ; j < M; j++)
				   result.data[i+M][j] = C[1][0].data[i][j];
		   
		   for(int i = 0 ; i < M; i++)
			   for(int j = 0 ; j < M; j++)
				   result.data[i+M][j+M] = C[1][1].data[i][j];
		   
		   return result;
		   
			   
	   }

	    public double[] toArray()
	    {
	    	double[] matArray = new double[M*M];
	    	for(int i = 0; i < M * M ; i++)	
	    		matArray[i] = data[i/M][i%M];
	    	return matArray;
	    }
	    
	    public static Matrix random(int M) {
	        Matrix A = new Matrix(M);
	        double count = 0.0;
	        Random r = new Random();
	        MathContext mc = new MathContext(2);
	        for (int i = 0; i < M; i++)
	            for (int j = 0; j < M; j++)
	            {
	            	A.data[i][j] = r.nextInt(99)/100.0;
	            	count += A.data[i][j];
	            	if(count > 1)
	            	{
	            		A.data[i][j] = -r.nextInt(99)/100.0;
	            		count += A.data[i][j];
	            	}
	            }
	        if(count > 1)
	        {
	        	A.data[M-1][M-1] = (BigDecimal.valueOf(1 - count).round(mc)).doubleValue();
	        }
	        else
	        {
	        	A.data[M-1][M-1] = (BigDecimal.valueOf(1 - count).round(mc)).doubleValue();
	        }
	        return A;
	    }
	    
	    public void print()
	    {
	    	/*
	    	for(int i = 0; i < M; i++)
	    	{
	    		for(int j = 0; j < M; j++)
	    			System.out.print(" " + data[i][j]);
	    		System.out.println("");
	    	}
		*/
	    	
	    }
	    public void printToFile(BufferedWriter out)
	    {
	    	for(int i = 0; i < M; i++)
	    	{
	    		for(int j = 0; j < M; j++)
					try {
						out.write(" " + data[i][j]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		try {
					out.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	    
	    public String getStr()
	    {
	    	String matString = new String(); 
	    	
	    	for(int i = 0; i < M; i++)
	    	{
	    		for(int j = 0; j < M; j++)
	    			matString.concat(" " + data[i][j]);
	    		matString.concat("\n");
	    	}
	    	matString.concat("\n");
	    	return matString;
	    }

	    

		public static Matrix createUnitMat(int m2) {
			// TODO Auto-generated method stub
			Matrix C = new Matrix(m2);
			for(int i =0; i < m2; i++)
				for(int j = 0 ; j < m2; j++)
				{
					C.data[i][j] = 0;
					if(i==j)
						C.data[i][j] = 1;
				}
			return C;
		}
}
