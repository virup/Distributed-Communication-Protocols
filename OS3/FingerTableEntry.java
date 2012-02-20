
public class FingerTableEntry {

	Node node;
	int startID;
	int endID;
	
	
	FingerTableEntry(Node node, int startID, int endID)
	{
		this.node = node;
		this.startID = startID;
		this.endID = endID;
	}
	
	
	boolean isPresent(int id)
	{
		if(startID < id && endID >= id)
			return true;
		else if(startID > id && endID <= id && startID > endID )
			return true;
		else
			return false;
	}
	


}
