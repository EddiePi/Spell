package NPL;

import spell.LCSObject;

import java.util.*;

/**
 * Created by Eddie on 2018/2/20.
 */
public class FrequencyCalculator {


    Map<String, Integer> wcMap;

    List<Map.Entry<String, Integer>> sortedAllWords;

    List<Map.Entry<String, Integer>> sortedKeyWords;

    Set<String> wordIgnoreSet;

    Double keyThresholdPercent = 0.15;


    public FrequencyCalculator() {
        wcMap = new HashMap<>();
        wordIgnoreSet = WordIgnoreSet.getInstance().getIgnoredWordSet();
    }

    public void parseLCSObject(LCSObject lcsObject) {
        List<String> constantField = lcsObject.getConstantField();
        String[] contentSeq = new String[constantField.size()];
        constantField.toArray(contentSeq);

        String[] POSSeq = lcsObject.getConstantPOSseq();
        Integer objNumber = lcsObject.count();
        int length = contentSeq.length;
        if (length != POSSeq.length) {
            return;
        }

        for(int i = 0; i < length; i++) {

            // this is the normal execution using part of speech.
            if (PartOfSpeech.NONE.contains(POSSeq[i]) && !wordIgnoreSet.contains(contentSeq[i].trim().toLowerCase())) {
                String singularForm = Lemmatizer.mayBeLemmatizeToSingular(contentSeq[i]);
                if (singularForm.trim().length() == 0 || !singularForm.toLowerCase().matches(".*[a-z]+.*")) {
                    continue;
                }
                addOne(singularForm, objNumber);
            }

            // this statement is to test not using the part of speech feature.
//            if (!wordIgnoreSet.contains(contentSeq[i].trim().toLowerCase())) {
//                addOne(contentSeq[i], objNumber);
//            }
        }
    }

    private void addOne(String word, Integer objNumber) {
        wcMap.compute(word.toLowerCase(), (k, v) -> (v == null) ? objNumber : v + objNumber);
    }

    public List<Map.Entry<String, Integer>> getSortedAllWords() {
        sortedAllWords = new ArrayList<>(wcMap.entrySet());
        //然后通过比较器来实现排序
        //升序排序
        Collections.sort(sortedAllWords, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return sortedAllWords;
    }

    public List<Map.Entry<String, Integer>> getSortedKeyWords() {
        sortedKeyWords = new ArrayList<>();
        if (sortedAllWords == null) {
            getSortedAllWords();
        }

        int index = 0;
        int threshold = (int)(wcMap.size() * keyThresholdPercent);
        for(Map.Entry<String, Integer> entry: sortedAllWords) {
            if (index < threshold) {
                sortedKeyWords.add(entry);
                index++;
                continue;
            }
            break;
        }
        return sortedKeyWords;
    }

    public void report() {
        if (sortedAllWords == null) {
            getSortedAllWords();
        }

        int sum = 0;
        double squareSum = 0;
        int count = wcMap.size();
        double var;
        double mean;
        double stdvar;
        int index = 1;
        int threshold = (int)(wcMap.size() * keyThresholdPercent);
        for (Map.Entry<String, Integer> entry: sortedAllWords) {
            int value = entry.getValue();
            sum += value;
            squareSum += (value * value);
            if (index <= threshold) {
                System.out.printf("key word: %s\tcount: %d\n", entry.getKey(), value);
            } else {
                System.out.printf("Not key word: %s\tcount: %d\n", entry.getKey(), value);
            }
            index++;
        }

        mean = sum * 1.0 / count;
        var = squareSum / count - mean;
        stdvar = Math.sqrt(var);

        System.out.printf("mean: %f, stdvar: %f\n", mean, stdvar);
    }
}
