import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.acl.Owner;
import java.util.Vector;


class LogHandler
{
	
	FileOutputStream fout;
	PrintWriter pw;
	
	LogHandler()
	{
	}
	
	LogHandler(String filename)
	{
		
		try
		{
			fout=new FileOutputStream(filename);
	    }
	    catch (Exception e)
	    {
	    	System.out.println( e.getMessage());
	    }
		pw=new PrintWriter(fout);
			
	}
	
	public void write(String s)
	{
		pw.println(s);
		pw.println("");
		pw.flush();
		
	}

public void writeft(FingerTableClass ft)
{

   
    for(int i=0;i<ft.fingerTable.size();i++)
	 {
		 pw.print("start: " + ft.fingerTable.get(i).startID+" ; ");
		 pw.print("interval: [" + ft.fingerTable.get(i).startID+","+ft.fingerTable.get(i).endID+");");
		 pw.print("succ :");
		
		 pw.print(ft.fingerTable.get(i).node.nodeID);
         pw.println();
         pw.flush();
	 }

}
	
	public void write(int s)
	{
		pw.println(s);
		//pw.println("");
		pw.flush();
		
	}
	

public void writeVector(Vector<Integer> v, int ownNodeID, int lookupKey)
{
   
   pw.print(ownNodeID +" Lookup " + lookupKey + ": routing path ");
   for(int i=v.size() -1;i>=1;i--)
   {
      pw.print(v.elementAt(i)+" -> ");
   }
   pw.print(v.elementAt(0));
   pw.println();
   pw.flush();
}


      public void close()
{
    try{
    
     fout.close();
     }
     catch (Exception e)
	    {
	    	System.out.println( e.getMessage());
	    }
     
}
	
	
}
