package NPL;

import java.io.*;
import java.util.List;
import opennlp.tools.postag.*;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Sequence;

/**
 * Created by Eddie on 2018/2/20.
 */
public class Tagger {

    private static Tagger instance = null;

    public static Tagger getInstance() {
        if (instance == null) {
            instance = new Tagger();
        }
        return instance;
    }

    List<String> taggedSentence;
    POSModel model;
    POSTaggerME tagger;

    private Tagger () {
        try {
            File modelFile = new File("./lib/en-pos-maxent.bin");
            if (!modelFile.exists()) {
                modelFile = new File("../lib/en-pos-maxent.bin");
            }
            if (!modelFile.exists()) {
                System.out.println("model does not exist. exit!");
                throw new FileNotFoundException(modelFile.getAbsolutePath());
            }
            InputStream modelIn = new FileInputStream(modelFile);
            model = new POSModel(modelIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tagger = new POSTaggerME(model);
    }

    public String[] tag(String line) {
        String[] tokenizerLine = WhitespaceTokenizer.INSTANCE.tokenize(line);
        String[] tags = tagger.tag(tokenizerLine);

        return tags;
    }

    public String[] tag(String[] tokens) {
        String[] tags = tagger.tag(tokens);
        return tags;
    }
}
