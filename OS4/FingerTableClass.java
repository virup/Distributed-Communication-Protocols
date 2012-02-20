import java.io.Serializable;
import java.util.List;
import java.util.Vector;


public class FingerTableClass implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<FingerTableEntry> fingerTable = new Vector<FingerTableEntry>();
	Node ownNode;
	
	FingerTableClass(List<Node> nodeList, Node ownNode, int size)
	{
		this.ownNode = ownNode;
		int key = ownNode.nodeID;
		
		//int size = nodeList.size();
		int maxID = (int)Math.pow(2, size);
		boolean flag = false;
		
		
		//TODO : CHANGE FINGER TABLE SIZE TO THAT IT SHOULD BE
		//+++++++++++++++++++++++++++++++++++++++++++++==
		//***************************
		for(int i = 0; i < size; i++)
		{
			int startID = (int)Math.pow(2, i);
			startID += key;
			
			if(startID >= maxID)
				startID = startID % maxID;
			
			
			
			int endID = (int)Math.pow(2, i+1);
			endID += key;
			
			if(endID >= maxID)
				endID = endID % maxID;
			
			Node succNode = null;
			
			for(int j = 0;j < nodeList.size();j++) // traverse thru the sorted array to find the successor node
			 {
				 
				flag = false;
				
				//MADE A MODIFICATION HERE endID -> startID
				 if(nodeList.get(j).nodeID >= startID)
				 {
					 succNode = nodeList.get(j);
					 flag = true;
					 break;
				 }
			 }
			
			if(flag == false)
			{
				succNode = nodeList.get(0);
			}
			
			fingerTable.add(new FingerTableEntry(succNode, startID, endID));
		}
		
	}
	
	FingerTableClass( Node ownNode)
	{
		this.ownNode = ownNode;
	}
	
	FingerTableClass(Node ownNode, FingerTableEntry fte)
	{
		this.ownNode = ownNode;
		fingerTable.add(0, fte);
	}
	
	void add(int i, FingerTableEntry f)
	{
		fingerTable.add(i, f);
	}
	
	Node closestPrecedingNode(int id)
	 {		 
		Node n = null;
		
		//TODO: if ownNode.nodeID == id: then do what? see isInside function
		 for(int i = fingerTable.size() -1; i >= 0; i--)
		 {
			 int fingerNodeID = fingerTable.get(i).node.nodeID;
			 
			 if((fingerNodeID > ownNode.nodeID && fingerNodeID < id)
					 || (fingerNodeID < ownNode.nodeID && fingerNodeID < id && ownNode.nodeID > id)
					 || (fingerNodeID > ownNode.nodeID && fingerNodeID > id && ownNode.nodeID > id))
			 {
				 fingerTable.get(i).node.print();
				 return fingerTable.get(i).node;
			 }
			 
		 }
		return fingerTable.get(fingerTable.size() -1).node;
	 }
	
	
	public void print()
	{
		System.out.println("------------------------------");
		System.out.println("Finger Table");
		
		for(int i = 0; i < fingerTable.size();i++)
		{
			System.out.println("["+fingerTable.get(i).startID +", "+ 
					fingerTable.get(i).endID +") : " + 
					fingerTable.get(i).node.nodeID);
		}
		System.out.println("-----------------------------");
	}
}
