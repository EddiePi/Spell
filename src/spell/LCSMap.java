package Spell;

import java.util.ArrayList;
import java.util.List;

public class LCSMap {
	
	private List<LCSObject> LCSObjects = new ArrayList<LCSObject>();
	private int lineId = 0;
	
	public LCSMap() {
		
	}
	
	//Insert a log entry into the LCSMap
	public void insert(String entry) {
		String seq[] = entry.trim().split("[\\s]+");
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
			if(obj.length() < seq.length / 2 || obj.length() > seq.length * 2) {
				continue;
			}
			
			//Get LCS and see if it's a match
			int l = obj.getLCS(seq);
			if(l >= seq.length / 2 && l > bestMatchLength) {
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
			temp = temp + "\tObject " + i + ":\n\t\t" + objectAt(i).toString() + "\n";
			entryCount += objectAt(i).count();
		}
		
		temp = temp + "\n\t" + entryCount + " total entries found, " + lineId + " expected.";
		
		return temp;
	}

}
