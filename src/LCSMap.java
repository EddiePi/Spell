import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LCSMap {
	
	private List<LCSObject> LCSObjects = new ArrayList<LCSObject>();
	private int lineId = 0;
	private Map<String, String> replaceMap;
	
	public LCSMap() {
		replaceMap = ForceReplaceSet.getInstance().replaceMap;
	}
	
	//Insert a log entry into the LCSMap
	public void insert(String entry) {
		String replacedEntry = entry;
		for (Map.Entry<String, String> strToRplc: replaceMap.entrySet()) {
			String origin = strToRplc.getKey();
			String target = strToRplc.getValue();
			replacedEntry = replacedEntry.replaceAll(origin, target);
		}

		String seq[] = replacedEntry.trim().split("[\\s]+");
		LCSObject obj = getMatch(seq);
		
		//If no existing match create a new LCSObject, otherwise add the line id to an existing one
		if(obj == null) {
			obj = new LCSObject(seq, lineId++);
			LCSObjects.add(obj);
		}else {
			obj.insert(seq, lineId++);
		}
	}
	
	//Find LCSObject that is the closest match
	private LCSObject getMatch(String seq[]) {
		LCSObject bestMatch = null;
		int bestMatchLength = 0;
		
		//Find LCS of all existing LCSObjects and determine if they're a match as described in the paper
		for(LCSObject obj : LCSObjects) {
			
			//Use the pruning described in the paper
			if(obj.length() < seq.length / 1.7 || obj.length() > seq.length * 1.7) {
				continue;
			}
			
			//Get LCS and see if it's a match
			int l = obj.getLCS(seq);
			if(l >= seq.length / 1.7 && l > bestMatchLength) {
				bestMatchLength = l;
				bestMatch = obj;
			}
		}
		
		return bestMatch;
	}
	
	//Returns LCSObject at a given index
	public LCSObject objectAt(int index) {
		return LCSObjects.get(index);
	}
	
	//Returns number of LCSObjects made
	public int size() {
		return LCSObjects.size();
	}
	
	//To string method for testing
	public String toString() {
		String temp = "\t" + size() + " Objects in the LCSMap\n\n";
		int entryCount = 0;
		
		for(int i = 0; i < size(); i++) {
			LCSObject entry  = objectAt(i);
//			if (entry.count() < 10) {
//				continue;
//			}
			temp = temp + "\tObject " + i + ":\n\t\t" + entry.toString() + "\n";
			entryCount += objectAt(i).count();
		}
		
		temp = temp + "\n\t" + entryCount + " total entries found, " + lineId + " expected.";
		
		return temp;
	}
}
