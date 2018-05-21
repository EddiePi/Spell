package NPL;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.*;
import java.util.List;

/**
 * Created by Eddie on 2018/2/22.
 */
public class Chunker {

    private static Chunker instance = null;

    public static Chunker getInstance() {
        if (instance == null) {
            instance = new Chunker();
        }
        return instance;
    }

    ChunkerModel model;
    ChunkerME chunker;

    private Chunker () {
        try {
            File modelFile = new File("./lib/en-chunker.bin");
            if (!modelFile.exists()) {
                modelFile = new File("../lib/en-chunker.bin");
            }
            if (!modelFile.exists()) {
                System.out.println("model does not exist. exit!");
                throw new FileNotFoundException(modelFile.getAbsolutePath());
            }
            InputStream modelIn = new FileInputStream(modelFile);
            model = new ChunkerModel(modelIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        chunker = new ChunkerME(model);
    }

    public String[] chunk(String[] tokens, String[] POSs) {
        String[] chunked = chunker.chunk(tokens, POSs);
        return chunked;
    }
}
