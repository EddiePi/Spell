package Log;

import spell.ForceReplaceMap;

import java.io.*;
import java.util.Map;

/**
 * Created by Eddie on 2018/3/5.
 */
public class LogPreprocessor {

    String logFilePath;
    BufferedReader br;
    boolean EOF = false;

    public LogPreprocessor(String filePath) {
        this.logFilePath = filePath;
        resetFilePosition();
    }


    public String getNextLine() {
        String line = "";
        try {
            line = br.readLine();
            if (line == null) {
                EOF = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public void resetFilePosition() {
        File logFile = new File(logFilePath);
        try {
            br = new BufferedReader(new FileReader(logFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String addSpace(String logMessage) {
        String res = logMessage;
        Map<String, String> replaceMap = ForceReplaceMap.getInstance().punctReplaceMap;
        for (Map.Entry<String, String> strToRplc: replaceMap.entrySet()) {
            String origin = strToRplc.getKey();
            String target = strToRplc.getValue();
            res = res.replaceAll(origin, target);
            res = res.replaceAll("\\s+", " ").trim();
        }

        return res;
    }
}
