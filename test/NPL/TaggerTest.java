package NPL;

import org.junit.Before;
import org.junit.Test;
import spell.LCSMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eddie on 2018/2/20.
 */
public class TaggerTest {
    Tagger tagger;
    Chunker chunker;
    Lemmatizer lemmatizer;
    List<String[]> keys;
    List<String[]> POSs;
    FrequencyCalculator fc;
    LCSMap map;
    @Before
    public void setUp() throws Exception {
        fc = new FrequencyCalculator();
        tagger = Tagger.getInstance();
        chunker = Chunker.getInstance();
        lemmatizer = Lemmatizer.getInstance();
        map = new LCSMap();

        //File f = new File("/Users/Eddie/gitRepo/log-preprocessor/data/yarn-only-content.log");
        File f = new File("/Users/Eddie/gitRepo/log-preprocessor/data/spark-log-keys.txt");
        FileReader fr;
        BufferedReader br;
        keys = new ArrayList<>();
        POSs = new ArrayList<>();
        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            String line;
            while((line = br.readLine()) != null) {
                //map.insert(line);
                String[] elem = line.split("\\s+");
                keys.add(elem);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        int size = map.size();
//        for(int i = 0; i < size; i++) {
//            List<String> constantField = map.objectAt(i).getConstantField();
//            keys[i] = new String[constantField.size()];
//            constantField.toArray(keys[i]);
//            POSs[i] = tagger.tag(keys[i]);
//        }
    }

    @Test
    public void tag() throws Exception {
        for (String[] key: keys) {
            String[] tagged = tagger.tag(key);
            POSs.add(tagged);
            System.out.printf("key: %s\t", Arrays.toString(key));
            System.out.println(Arrays.toString(tagged));
        }
        writeTaggedLine();
    }

    public void writeTaggedLine() throws Exception {
        FileWriter fw = new FileWriter(new File("/Users/Eddie/gitRepo/log-preprocessor/data/spark-keys-tags.txt"));
        for (int i = 0; i < keys.size(); i++) {
            String keyTag = "";
            String[] key = keys.get(i);
            String[] tag = POSs.get(i);
            for (int j = 0; j < key.length; j++) {
                keyTag += key[j] + "_" + tag[j] + " ";
            }
            fw.write(keyTag + "\n");
        }
        fw.close();
    }

    @Test
    public void wordcount() throws Exception {

        for (int i = 0; i < map.size(); i++) {
            fc.parseLCSObject(map.objectAt(i));
        }
        fc.report();

        for (int i = 0; i < map.size(); i++) {
            map.objectAt(i).assignKey(fc.getSortedAllWords());
        }
        System.out.println(map.toString());
    }

    @Test
    public void chunk() throws Exception {
        for (int i = 0; i < keys.size(); i++) {
            String[] chunked = chunker.chunk(keys.get(i), POSs.get(i));
            String[] key_tag = new String[keys.get(i).length];
            for (int j = 0; j < keys.get(i).length; j++) {
                key_tag[j] = keys.get(i)[j] + "_" + chunked[j];
            }
            System.out.println(Arrays.toString(key_tag));
        }
    }

    @Test
    public void lemmatize() throws Exception {
        for (int i = 0; i < keys.size(); i++) {
            String[] lemmatized = lemmatizer.lemmatize(keys.get(i), POSs.get(i));
            System.out.println(Arrays.toString(keys.get(i)) + "\t" + Arrays.toString(lemmatized));

        }
    }

}