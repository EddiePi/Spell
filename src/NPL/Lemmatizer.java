package NPL;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;

import java.io.*;
import java.util.List;

/**
 * Created by Eddie on 2018/2/22.
 */
public class Lemmatizer {
    private static Lemmatizer instance = null;

    public static Lemmatizer getInstance() {
        if (instance == null) {
            instance = new Lemmatizer();
        }
        return instance;
    }

    DictionaryLemmatizer lemmatizer;

    private Lemmatizer () {
        try {
            File modelFile = new File("./lib/en-lemmatizer.dict");
            if (!modelFile.exists()) {
                modelFile = new File("../lib/en-lemmatizer.dict");
            }
            if (!modelFile.exists()) {
                System.out.println("model does not exist. exit!");
                throw new FileNotFoundException(modelFile.getAbsolutePath());
            }
            InputStream modelIn = new FileInputStream(modelFile);
            lemmatizer = new DictionaryLemmatizer(modelIn);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] lemmatize (String[] tokens, String[] POSs) {
        String[] lemmatized = lemmatizer.lemmatize(tokens, POSs);
        return lemmatized;
    }

    public static String mayBeLemmatizeToSingular(String word) {
        String temp;
        String[] wordInSeq = new String[1];
        String[] posInSeq = new String[1];
        wordInSeq[0] = word;
        posInSeq[0] = "NNS";
        temp = Lemmatizer.getInstance().lemmatize(wordInSeq, posInSeq)[0];
        if (temp.length() == 1) {
            return word;
        } else {
            return temp;
        }
    }
}
