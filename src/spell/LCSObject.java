package spell;

import NPL.Lemmatizer;
import NPL.PartOfSpeech;
import NPL.Tagger;
import NPL.WordIgnoreSet;

import java.util.*;

public class LCSObject {
	private List<Integer> lineIds = new ArrayList<Integer>(); //Holds line ids
	private String LCSseq[]; //Token Sequence
	private String POSseq[] = null;
	private List<String> constantFieldList = null;
	private String constantFieldPOSseq[] = null; //POS Sequence

	// keys assigned to the LCSObject. this is also the key in keyed message
	private Set<String> keys;
	private Integer wcThreshold = 700;
	private Integer maxKeyNumber = 3;

	private Set<String> ignoreSet = WordIgnoreSet.getInstance().getIgnoredWordSet();


	private Map<String, String> idReplaceMap = ForceReplaceMap.getInstance().idReplaceMap;
	private Map<String, String> punctReplaceMap = ForceReplaceMap.getInstance().punctReplaceMap;
	
	public LCSObject(String[] seq, int lineId) {
		keys = new HashSet<>();
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
				if(seq[j].equals(LCSseq[i])) {
					lastMatch = j;
					count++;
					break;
				} else if (idReplaceMap.containsKey(LCSseq[i]) || punctReplaceMap.containsKey(LCSseq[i])) {
					if (seq[j].matches(LCSseq[i])) {
						lastMatch = j;
						count++;
						break;
					}
				}
			}
		}
		
		return count;
	}
	
	//Insert a line into the spell.LCSObject
	public void insert(String[] seq, int lineId) {		
		lineIds.add(lineId);
		String temp = "";

		if (POSseq == null) {
			POSseq = Tagger.getInstance().tag(seq);
		}
		
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
				if(seq[j].equals(LCSseq[i])) {
					placeholder = false;
					temp = temp + LCSseq[i] + " ";
					lastMatch = j;
					break;
				}
//				else if (replaceSet.contains(LCSseq[i])) {
//					if (seq[j].matches(LCSseq[i])) {
//						placeholder = false;
//						temp = temp + LCSseq[i] + " ";
//						lastMatch = j;
//						break;
//					}
//				}
				else if(!placeholder) {
					temp = temp + "* ";
					placeholder = true;
				}
			}
		}
		
		//Set sequence based of the common sequence found
		LCSseq = temp.trim().split("[\\s]+");
	}

	public List<String> getConstantField() {
		constantFieldList = new LinkedList<>(Arrays.asList(LCSseq));

		// TEST
//		if (constantFieldList.get(0).contains("attempt")) {
//			System.out.print("stop");
//		}
		int i = -1;
		boolean isReplaced;
		while(true) {
			isReplaced = false;
			i++;
			if (i >= constantFieldList.size()) {
				break;
			}
			String token = constantFieldList.get(i);
			if (token.equals("*")) {
				constantFieldList.remove(i);
				i--;
				continue;
			}
			for (String value: idReplaceMap.values()) {
				if (token.contains(value.replaceAll("\\\\d", "d"))) {
					constantFieldList.remove(i);
					i--;
					isReplaced = true;
					break;
				}
			}
			if (isReplaced) {
				continue;
			}
//			int tokenLength = token.length();
//			if (tokenLength > 0) {
//				if (token.substring(tokenLength - 1).matches("\\p{Punct}\\pP")) {
//					token = token.substring(0, tokenLength - 1);
//				}
//				if (token.substring(0, 1).matches("\\p{Punct}\\pP")) {
//					token = token.substring(1, token.length());
//				}
//			}
			constantFieldList.set(i, token.replaceAll("[\\p{Punct}\\pP]", ""));
		}
		return constantFieldList;
	}

	public String[] getConstantPOSseq() {
		if (constantFieldPOSseq == null) {
			if (constantFieldList == null) {
				getConstantField();
			}
			Tagger tagger = Tagger.getInstance();
			String[] constantFieldSeq = new String[constantFieldList.size()];
			constantFieldList.toArray(constantFieldSeq);
			constantFieldPOSseq = tagger.tag(constantFieldSeq);
		}
		return constantFieldPOSseq;
	}

	public String[] getPOSseq() {
		return POSseq;
	}

	public Set<String> getKeys () {
		return keys;
	}

	public void assignKey(List<Map.Entry<String, Integer>> sortedWordcountMap) {
		List<String> constantField = getConstantField();
		String[] contentSeq = new String[constantField.size()];
		constantField.toArray(contentSeq);
		String[] POSSeq = getConstantPOSseq();
		int length = contentSeq.length;
		if (length != POSSeq.length) {
			return;
		}
		Set<String> keyCandidate = new HashSet<>();
		for(int i = 0; i < length; i++) {
			// this is the normal execution using part of speech.
			if (PartOfSpeech.NONE.contains(POSSeq[i]) && !ignoreSet.contains(contentSeq[i].trim().toLowerCase())) {
				String singularForm = Lemmatizer.mayBeLemmatizeToSingular(contentSeq[i].toLowerCase());
				keyCandidate.add(singularForm);
			}
		}
		for (Map.Entry<String, Integer> entry: sortedWordcountMap) {
			String entryKey = entry.getKey();
			if (keys.size() < maxKeyNumber) {
				if (keyCandidate.contains(entryKey)) {
					keys.add(entryKey);
				}
			}
		}

		// force assign key if there are POS such as application_d+_d+_d+_d+
		for (String str: LCSseq) {
			if (str.matches("container_d\\+.*")) {
				keys.add("container");
			}
			if (str.matches("application_d\\+.*")) {
				keys.add("application");
			}
			if (str.matches("appattempt_d\\+.*")) {
				keys.add("appattempt");
			}
			if (str.matches("attempt_d\\+.*")) {
				keys.add("attempt");
			}
		}



//		for(int i = 0; i < length; i++) {
//			// this is the normal execution using part of speech.
//			if (PartOfSpeech.NONE.contains(POSSeq[i]) && !ignoreSet.contains(contentSeq[i].trim().toLowerCase())) {
//				String singularForm = Lemmatizer.mayBeLemmatizeToSingular(contentSeq[i].toLowerCase());
//				Integer count = wordcountMap.get(singularForm);
//				if (count == null) {
//					continue;
//				}
//				if (maxCount == wordcountMap.get(singularForm)) {
//					keys.add(singularForm);
//				}
//			}
//		}
	}
	
	//Length for pruning
	public int length() {
		return LCSseq.length;
	}
	
	//Count of lineIds in this spell.LCSObject
	public Integer count() {
		return lineIds.size();
	}

	public String[] getLCSseq() {
		return LCSseq;
	}

	public String getLCSString() {
		String LCSStr = "";
		for (String elem: LCSseq) {
			LCSStr += " " + elem;
		}
		return LCSStr;
	}
	
	//To String method for testing
//	public String toString() {
//		String temp = "";
//
//		for(String s : LCSseq) {
//			temp = temp + s + " ";
//		}
//
//		temp = temp + "\n\t\t{";
//
//		for(int i : lineIds) {
//			temp = temp + (i + 1) + ", ";
//		}
//
//		return temp.substring(0, temp.length() - 2) + "}";
//	}

	public String toString() {
		String res = "";

		for (String s: LCSseq) {
			res = res + s+ " ";
		}
		res = res + "\n\t\t";
		res = res + "keys: " + keys.toString();
		res = res + "\n\t\t";
		res = res + "# of obj: " + lineIds.size();

		return res;
	}
}
