import java.util.List;
import java.util.Vector;


public class FingerTableClass {

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
		//**************************8
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
	
	
	Node closestPrecedingNode(int id)
	 {		 
		Node n = null;
		 for(int i = fingerTable.size() -1; i >= 0; i--)
		 {
			 /*if(fingerTable.get(i).isPresent(id))
			 {
				 fingerTable.get(i).node.print();
			 
				 return fingerTable.get(i).node;
			}
			*/
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
}
