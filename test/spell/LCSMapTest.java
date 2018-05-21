package spell;

import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Created by Eddie on 2018/4/25.
 */
public class LCSMapTest {
    LCSMap map;
    @Before
    public void setUp() throws Exception {
        map = new LCSMap();

        File f = new File("/Users/Eddie/gitRepo/log-preprocessor/data/spark-only-content.log");
        //File f = new File("/home/eddie/log-preprocessor/data/spark-only-content.log");
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            String line;
            while((line = br.readLine()) != null) {
                map.insert(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void writeResult() throws Exception {
        String fileName="/Users/Eddie/gitRepo/log-preprocessor/data/spark-log-keys.txt";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for (LCSObject lcsObject: map.getAllLCSObjects()) {
                String key = lcsObject.getLCSString();
                bw.write(key + "\n");
            }
            bw.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

}