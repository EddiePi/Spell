package spell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LCSMap {
	
	private List<LCSObject> LCSObjects = new ArrayList<LCSObject>();
	private int lineId = 0;
	private Map<String, String> idReplaceMap;
	private Map<String, String> punctReplaceMap;
	
	public LCSMap() {
		idReplaceMap = ForceReplaceMap.getInstance().idReplaceMap;
		punctReplaceMap = ForceReplaceMap.getInstance().punctReplaceMap;
	}
	
	//Insert a log entry into the spell.LCSMap
	public void insert(String entry) {
		String replacedEntry = entry;
		for (Map.Entry<String, String> strToRplc: idReplaceMap.entrySet()) {
			String origin = strToRplc.getKey();
			String target = strToRplc.getValue();
			replacedEntry = replacedEntry.replaceAll(origin, target);
		}

		for (Map.Entry<String, String> strToRplc: punctReplaceMap.entrySet()) {
			String origin = strToRplc.getKey();
			String target = strToRplc.getValue();
			replacedEntry = replacedEntry.replaceAll(origin, target);
		}

		String seq[] = replacedEntry.trim().split("[\\s]+");
		LCSObject obj = getMatch(seq);

		
		//If no existing match create a new spell.LCSObject, otherwise add the line id to an existing one
		if(obj == null) {
			obj = new LCSObject(seq, lineId++);
			LCSObjects.add(obj);
		}else {
			obj.insert(seq, lineId++);
		}
	}
	
	//Find spell.LCSObject that is the closest match
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
	
	//Returns spell.LCSObject at a given index
	public LCSObject objectAt(int index) {
		return LCSObjects.get(index);
	}

	public List<LCSObject> getAllLCSObjects() {
		List<LCSObject> newList = new ArrayList<>(LCSObjects);
		return newList;
	}
	
	//Returns number of LCSObjects made
	public int size() {
		return LCSObjects.size();
	}
	
	//To string method for testing
	public String toString() {
		Collections.sort(LCSObjects, ((o1, o2) -> o2.count().compareTo(o1.count())));
		String temp = "\t" + size() + " Objects in the spell.LCSMap\n\n";
		int entryCount = 0;

		int withKey = 0;
		for(int i = 0; i < size(); i++) {
			LCSObject entry  = objectAt(i);

			// do not report LCS that has no key
//			if (entry.getKeys().size() == 0) {
//				continue;
//			}
			withKey++;
//			if (entry.count() < 10) {
//				continue;
//			}
			temp = temp + "\tObject " + i + ":\n\t\t" + entry.toString() + "\n";
			entryCount += objectAt(i).count();
		}

		temp = temp + "\n\t" + "# of lcs with key: " + withKey + " .";
		temp = temp + "\n\t" + entryCount + " total entries found, " + lineId + " expected.";
		
		return temp;
	}
}
