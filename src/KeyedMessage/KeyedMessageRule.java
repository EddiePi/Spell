package KeyedMessage;

import NPL.PartOfSpeech;
import spell.LCSObject;

import java.util.*;

/**
 * Created by Eddie on 2018/2/25.
 */
public class KeyedMessageRule {
    String regex = "";

    public Set<String> keys;
    public Set<String> idGroup;
    public Set<String> valueGroup;
    public Boolean isFinish;

    Map<String, String> idTokenMap;
    Map<String, String> valueTokenMap;

    public KeyedMessageRule() {
        idTokenMap = IdTokenMap.getInstance().tokenMap;
        valueTokenMap = ValueTokenMap.getInstance().tokenMap;
        idGroup = new HashSet<>();
        valueGroup = new HashSet<>();
    }

    public void buildRule(LCSObject lcsObject) {
        keys = new HashSet<>(lcsObject.getKeys());
        String[] LCSseq = lcsObject.getLCSseq();
        String[] POSseq = lcsObject.getPOSseq();
        String nextPartToAppend;
        for (int i = 0; i < LCSseq.length; i++) {
            boolean replaced = false;
            String token = LCSseq[i].toLowerCase().replaceAll("\\.", "\\\\.");
            token = token.replaceAll("\\(", "\\\\(");
            token = token.replaceAll("\\)", "\\\\)");
            token = token.replaceAll("\\[", "\\\\[");
            token = token.replaceAll("\\]", "\\\\]");
            token = token.replaceAll("\\<", "\\\\<");
            token = token.replaceAll("\\>", "\\\\>");
            token = token.replaceAll("\\{", "\\\\{");
            token = token.replaceAll("\\}", "\\\\}");
            nextPartToAppend = token;
            // put things such as containerId into the id group
            if (token.matches("container_d\\+.*")) {
                nextPartToAppend = addIdGroupName(token, "container");
                replaced = true;
            }
            if (token.matches("application_d\\+.*")) {
                nextPartToAppend = addIdGroupName(token, "application");
                replaced = true;
            }
            if (token.matches("appattempt_d\\+.*")) {
                nextPartToAppend = addIdGroupName(token, "appattempt");
                replaced = true;
            }
            if (token.matches("attempt_d\\+.*")) {
                nextPartToAppend = addIdGroupName(token, "attempt");
                replaced = true;
            }
            if (token.matches("blockmanagerid\\(\\*\\)")) {
                nextPartToAppend = addIdGroupName(token, "BlockManagerId");
                replaced = true;
            }
            if (token.matches("jvm_d\\+.*")) {
                nextPartToAppend = addIdGroupName(token, "jvm");
            }

            // put things such as task, stage, shuffle, into the id group
            if (token.equals("*")) {
//                for (Map.Entry<String, String> entry : idTokenMap.entrySet()) {
//                    String tokenToReplace = entry.getKey();
//                    String groupName = entry.getValue();
//                    Integer wordNumberInToken = tokenToReplace.split("\\s+").length;
//
//                    String wordsInLCS = "";
//                    if (i - wordNumberInToken >= 0) {
//                        for (int j = wordNumberInToken; j > 0; j--) {
//                            wordsInLCS = wordsInLCS + LCSseq[i - j] + " ";
//                        }
//                        wordsInLCS = wordsInLCS.trim().toLowerCase();
//
//                        if (wordsInLCS.equals(tokenToReplace)) {
//                            nextPartToAppend = addGroupName(token, groupName);
//                        }
//                    }
//                }
                // if the following word indicates a unit, then regard this token as value
                if (i + 1 < LCSseq.length && !replaced) {
                    String unit = LCSseq[i + 1].toLowerCase();
                    if (unit.matches("(kb)|(byte)|(bytes)|(ms)|(mb)|(gb)")) {
                        nextPartToAppend = addValueGroupName(token);
                        replaced = true;
                    }
                }


                if (i - 1 >= 0 && !replaced) {
                    // assign the previous word as the group name
                    String previousWord = LCSseq[i - 1].toLowerCase();
                    String previousPOS = "";
                    if (i - 1 < POSseq.length) {
                        previousPOS = POSseq[i - 1];
                    }
                    if (!previousPOS.trim().matches(".*\\w+.*")) {
                        if (i - 2 >= 0) {
                            previousPOS = POSseq[i - 2];
                            previousWord = LCSseq[i - 2].toLowerCase();
                        }
                    }
                    if (valueTokenMap.containsKey(previousWord)) {
                        nextPartToAppend = addValueGroupName(token, previousWord);
                    } else if (PartOfSpeech.NONE.contains(previousPOS) || idTokenMap.containsKey(previousWord)) {
                        if (previousWord.matches(".*\\w+.*")) {
                            Integer groupIndex = 1;
                            String additionalGroupName = previousWord;
                            while (idGroup.contains(additionalGroupName)) {
                                additionalGroupName = previousWord + groupIndex;
                                groupIndex++;
                            }
                            nextPartToAppend = addIdGroupName(token, additionalGroupName);
                            replaced = true;
                        }
                    } else {
                        nextPartToAppend = addValueGroupName(token);
                    }
                }
            }

            nextPartToAppend = nextPartToAppend.replaceAll("d\\+", "\\\\d+");
            nextPartToAppend = nextPartToAppend.replaceAll("\\*", ".*");

            regex = regex + nextPartToAppend + " ";

        }
        regex = regex.trim();
    }

    private String addIdGroupName(String originalToken, String idGroupName) {
        String res = "(?<" + idGroupName + ">" + originalToken + ")";
        idGroup.add(idGroupName);
        return res;
    }

    private String addValueGroupName(String originalToken) {
        Integer valueNum = valueGroup.size() + 1;
        String valueTag = "value" + valueNum;
        String res = "(?<" + valueTag + ">" + originalToken + ")";
        valueGroup.add(valueTag);
        return res;
    }

    private String addValueGroupName(String originalToken, String valueGroupName) {
        String valueTag = valueGroupName;
        Integer valueNum = 1;
        while (valueGroup.contains(valueTag)) {
            valueTag = valueGroupName + valueNum;
            valueNum++;
        }
        String res = "(?<" + valueTag + ">" + originalToken + ")";
        valueGroup.add(valueTag);
        return res;
    }

    @Override
    public String toString() {
        return regex;
    }
}
