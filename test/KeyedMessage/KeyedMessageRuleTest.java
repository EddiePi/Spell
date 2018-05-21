package KeyedMessage;

import Log.LogPreprocessor;
import NPL.FrequencyCalculator;
import org.junit.Before;
import org.junit.Test;
import spell.LCSMap;
import spell.LCSObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eddie on 2018/2/25.
 */
public class KeyedMessageRuleTest {
    LCSObject lcsObject;
    KeyedMessageBuilder builder = KeyedMessageBuilder.getInstance();
    List<KeyedMessageRule> ruleList = new ArrayList<>();
    List<String> testLCSs = new ArrayList<>();
    List<String> testLogs = new ArrayList<>();
    List<LCSObject> lcsObjectList;

    FrequencyCalculator fc;
    LCSMap map;
    @Before
    public void setUp() throws Exception {
        // initialize the lcsObject
        testLCSs.add("Finished task * in stage * ( TID * ) . * bytes result sent to driver");
        testLCSs.add("Running task * in stage * ( TID * )");
        testLCSs.add("attempt_d+_d+_m_d+_d+ : Committed");
        testLCSs.add("Don't have map outputs for shuffle * fetching them");
        testLCSs.add("Getting * non-empty blocks out of * blocks");
        testLCSs.add("Started * remote fetches in * ms");
        testLCSs.add("Got assigned task *");
        testLCSs.add("Block * stored as * in memory ( estimated size * free * MB )");
        testLCSs.add("Changing * acls * to:");
        testLCSs.add("Started reading broadcast variable *");
        testLCSs.add("Reading broadcast variable * took * ms");
        testLCSs.add("Doing the fetch; tracker endpoint = NettyRpcEndpointRef ( spark : * : * )");
        testLCSs.add("Registered signal handler for *");

        // initialize simulated  Spark log messages
//
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Finished task 1.0 in stage 0.0 (TID 23). 2345 bytes result sent to driver");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Running task 1.0 in stage 0.0 (TID 23)");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 attempt_1_2_m_3_4 : Committed");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Don't have map outputs for shuffle 12, fetching them");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Getting 0 non-empty blocks out of 1 blocks");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Started 1 remote fetches in 0 ms");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Got assigned task 1");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Block broadcast_9_piece0 stored as bytes in memory ( estimated size 6.2 KB, free 200 MB )");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Started reading broadcast variable 1");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Reading broadcast variable 1 took 111 ms");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Registered signal handler for TERM");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Message RemoteProcessDisconnected(10.0.101.200:45363) dropped. RpcEnv already stopped.");
//        testLogs.add("container_1234567891234_0007_01_000002 18/01/26 18:21:23,345 Block 12 stored as bytes in memory (estimated size 12 kb , free 2000 mb)");

        // initialize simulated MR log messages
        testLogs.add("container_1505150484427_0025_01_000019 2017-09-12 04:58:51,731 fetcher#1 about to shuffle output of map attempt_1505150484427_0025_m_000008_0 decomp: 1964 len: 1968 to MEMORY");

        fc = new FrequencyCalculator();
        map = new LCSMap();

        //File f = new File("/Users/Eddie/gitRepo/log-preprocessor/data/yarn-only-content.log");
        File f = new File("/Users/Eddie/gitRepo/log-preprocessor/data/spark-only-content.log");
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

        lcsObjectList = map.getAllLCSObjects();
    }

    @Test
    public void wordcount() throws Exception {

        for (int i = 0; i < map.size(); i++) {
            fc.parseLCSObject(map.objectAt(i));
        }
        fc.report();

        for (int i = 0; i < map.size(); i++) {
            map.objectAt(i).assignKey(fc.getSortedKeyWords());
        }
        System.out.println(map.toString());
    }

    @Test
    public void buildRule() throws Exception {
        wordcount();
        for (LCSObject lcsObject: lcsObjectList) {
            KeyedMessageRule rule = new KeyedMessageRule();
            //lcsObject.assignKey(fc.getSortedAllWords());
            rule.buildRule(lcsObject);
            ruleList.add(rule);
            System.out.printf("LCS: %s\nregex: %s\n\n", lcsObject.toString(), rule.toString());
        }
        builder.addRulesToList(ruleList);
    }

    @Test
    public void identify() throws Exception {
        buildRule();
        for (String logStr: testLogs) {
            boolean matched = false;
            for (KeyedMessageRule rule: ruleList) {
                if (logStr.toLowerCase().matches(rule.regex)) {
                    matched = true;
                    System.out.printf("matched log: %s\nregex: %s\n\n", logStr, rule.regex);
                }
            }
            if (!matched) {
                System.out.printf("NOT matched log: %s\n", logStr);
            }
        }
    }

    @Test
    public void buildKeyedMessage() throws Exception {
        buildRule();
        List<KeyedMessage> keyedMessageList;
        LogPreprocessor preprocessor = new LogPreprocessor("/Users/Eddie/gitRepo/log-preprocessor/data/one-mr-app-cId-time-content.log");
        KeyedMessageSender sender = new KeyedMessageSender();
        String log;
        while ((log = preprocessor.getNextLine()) != null) {
            keyedMessageList = builder.build(log);
            //sender.sendMessages(keyedMessageList);
            System.out.printf("log: %s\n", log);
            for(KeyedMessage keyedMessage: keyedMessageList) {
                System.out.printf("keyed message: %s\n", keyedMessage);
            }
            keyedMessageList.clear();
            System.out.print("\n");

//
//        for (String logStr: testLogs) {
//            keyedMessageList = builder.build(logStr);
//            System.out.printf("log: %s\n", logStr);
//            for(KeyedMessage keyedMessage: keyedMessageList) {
//                System.out.printf("keyed message: %s\n", keyedMessage);
//            }
//            System.out.print("\n");
        }
    }
}
