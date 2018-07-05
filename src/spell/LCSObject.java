package Spell;

import java.util.ArrayList;
import java.util.List;

public class LCSObject {
	private List<Integer> lineIds = new ArrayList<Integer>(); //Holds line ids
	private String LCSseq[]; //Token Sequence
	
	public LCSObject(String[] seq, int lineId) {
		LCSseq = seq;
		lineIds.add(lineId);
	}
	
	//Get the length of the LCS between sequences
	public int getLCS(String[] seq) {
		int count = 0;
		
		//Loop through current sequence using the simple loop approach described in the paper
		int lastMatch = -1;
		for(int i = 0; i < LCSseq.length; i++) {
			if(LCSseq[i].equals("*")) {
				continue;
			}
			
			for(int j = lastMatch + 1; j < seq.length; j++) {
				if(LCSseq[i].equals(seq[j])) {
					lastMatch = j;
					count++;
					break;
				}
			}
		}
		
		return count;
	}
	
	//Insert a line into the LCSObject
	public void insert(String[] seq, int lineId) {		
		lineIds.add(lineId);
		String temp = "";
		
		//Create the new sequence by looping through it
		int lastMatch = -1;
		boolean placeholder = false; //Decides whether or not to add a * depending if there is already one preceding or not
		for(int i = 0; i < LCSseq.length; i++) {
			if(LCSseq[i].equals("*")) {
				if(!placeholder) {
					temp = temp + "* ";
				}
				placeholder = true;
				continue;
			}
			
			for(int j = lastMatch + 1; j < seq.length; j++) {
				if(LCSseq[i].equals(seq[j])) {
					placeholder = false;
					temp = temp + LCSseq[i] + " ";
					lastMatch = j;
					break;
				}else if(!placeholder) {
					temp = temp + "* ";
					placeholder = true;
				}
			}
		}
		
		//Set sequence based of the common sequence found
		LCSseq = temp.trim().split("[\\s]+"); 
	}
	
	//Length for pruning
	public int length() {
		return LCSseq.length;
	}
	
	//Count of lineIds in this LCSObject
	public int count() {
		return lineIds.size();
	}
	
	//To String method for testing
	public String toString() {
		String temp = "";
		
		for(String s : LCSseq) {
			temp = temp + s + " ";
		}
		
		temp = temp + "\n\t\t{";
		
		for(int i : lineIds) {
			temp = temp + i + ", ";
		}
		
		return temp.substring(0, temp.length() - 2) + "}";
	}
}
