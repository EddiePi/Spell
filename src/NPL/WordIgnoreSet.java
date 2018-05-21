package NPL;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eddie on 2018/2/21.
 */
public class WordIgnoreSet {

    private static WordIgnoreSet instance = null;

    private Set<String> ignoredWordSet;

    public static WordIgnoreSet getInstance() {
        if (instance == null) {
            instance = new WordIgnoreSet();
        }
        return instance;
    }

    public Set<String> getIgnoredWordSet() {
        return ignoredWordSet;
    }

    private WordIgnoreSet() {
        ignoredWordSet = new HashSet<>();
        File file = new File("./conf/word-ignore.conf");
        if (!file.exists()) {
            file = new File ("../conf/word-ignore.conf");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                ignoredWordSet.add(line.trim().toLowerCase());
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
