package KeyedMessage;

import Log.LogPreprocessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eddie on 2018/2/26.
 */
public class KeyedMessageBuilder {

    List<KeyedMessageRule> ruleList;

    private static KeyedMessageBuilder instance = null;

    private Map<String, String> tempTagMap;

    private Map<String, Double> tempValueMap;

    public static KeyedMessageBuilder getInstance() {
        if (instance == null) {
            instance = new KeyedMessageBuilder();
        }
        return instance;
    }

    private KeyedMessageBuilder() {
        ruleList = new ArrayList<>();
    }

    public void addRulesToList(List<KeyedMessageRule> newRules) {
        ruleList.addAll(newRules);
    }

    public List<KeyedMessage> build(String logMessage) {
        List<KeyedMessage> resKeyedMessage = new ArrayList<>();
        Pattern timePatter = Pattern.compile("(?<cid>container_\\d+_\\d+_\\d+_\\d+) (?<timestamp>(\\d{4}|\\d{2})[-/]\\d{2}[-/]\\d{2} \\d{2}:\\d{2}:\\d{2}[,\\.]\\d{3}) (?<content>.*)");
        Matcher timeMatcher = timePatter.matcher(logMessage.toLowerCase());
        if (!timeMatcher.matches()) {
            System.out.printf("format not match. log: %s\n", logMessage);
            return resKeyedMessage;
        }
        String timestampStr = timeMatcher.group("timestamp");
        String content = timeMatcher.group("content");
        String cid = timeMatcher.group("cid");
        content = LogPreprocessor.addSpace(content);
        Long timestamp = timeStrToLong(timestampStr);
        tempTagMap = new HashMap<>();
        tempValueMap = new HashMap<>();

        tempTagMap.put("container", cid);

        for (KeyedMessageRule rule: ruleList) {
            Pattern pattern = Pattern.compile(rule.regex);
            Matcher matcher = pattern.matcher(content);
            if (matcher.matches()) {
                //Map<String, String> tags = new HashMap<>();

                // extract the group in idGroup
                for (String idGroupName: rule.idGroup) {
                    String strValue = matcher.group(idGroupName);
                    Double value = mayBeTransferToValue(strValue);
                    if (!Double.isNaN(value)) {
                        tempValueMap.put(idGroupName, value);
                    } else {
                        tempTagMap.put(idGroupName, matcher.group(idGroupName));
                    }
                }

                // extract the group in valueGroup
                for (String valueGroupName: rule.valueGroup) {
                    String valueStr = matcher.group(valueGroupName);
                    Double value = Double.NaN;
                    if (valueStr.matches("[0-9]+(\\.[0-9]+)?")) {
                        value = Double.parseDouble(valueStr);
                    } else {
                        value = mayBeTransferToValue(valueStr);
                    }
                    if (!Double.isNaN(value)) {
                        tempValueMap.put(valueGroupName, value);
                    }
                }

                for (String key: rule.keys) {
                    if (tempValueMap.size() == 0) {
                        KeyedMessage newKeyedMessage = new KeyedMessage(key, tempTagMap, 1.0, timestamp);
                        resKeyedMessage.add(newKeyedMessage);
                    } else if (tempValueMap.size() == 1) {
                        Double value = 1.0;
                        for (Map.Entry<String, Double> entry: tempValueMap.entrySet()) {
                            value = entry.getValue();
                            break;
                        }
                        KeyedMessage newKeyedMessage = new KeyedMessage(key, tempTagMap, value, timestamp);
                        resKeyedMessage.add(newKeyedMessage);
                    } else {
                        Integer valueIndex = 1;
                        for (Map.Entry<String, Double> entry: tempValueMap.entrySet()) {
                            Double value = entry.getValue();
                            String entryKey = entry.getKey();
                            Map<String, String> multiValueTagMap = new HashMap<>(tempTagMap);
                            multiValueTagMap.put("value", entryKey);
                            KeyedMessage newKeyedMessage = new KeyedMessage(key, multiValueTagMap, value, timestamp);
                            resKeyedMessage.add(newKeyedMessage);
                            valueIndex++;
                        }
                    }
                }
            }
        }
        return resKeyedMessage;
    }

    private Double mayBeTransferToValue(String tagValue) {
        Pattern pattern = Pattern.compile("(?<digit>[0-9]+(\\.[0-9]+)?)\\s*(?<unit>kb|mb|gb|bytes|byte)");
        Matcher matcher = pattern.matcher(tagValue.toLowerCase());
        if (matcher.matches()) {
            Double value = Double.parseDouble(matcher.group("digit"));
            String unit = matcher.group("unit");

            switch (unit) {
                case "kb": value *= 1024; break;
                case "mb": value *= 1024 * 1024; break;
                case "gb": value *= 1024 * 1024 * 1024; break;
                default: break;
            }
            return value;
        }

        return Double.NaN;
    }

    public static Long timeStrToLong(String timestampStr) {
        String[] parts = timestampStr.trim().split("\\s+");
        Long timestamp = 0L;
        if (parts.length != 2) {
            return 0L;
        }
        // date part
        String[] date = parts[0].split("[-/]");
        if (date.length != 3) {
            return 0L;
        }
        String yearStr = date[0];
        if (yearStr.length() == 2) {
            yearStr = "20" + yearStr;
        }
        String monthStr = date[1];
        String dayStr = date[2];

        // time part
        String[] time = parts[1].split(":");
        if (time.length < 3) {
            return 0L;
        }
        String hourStr = time[0];
        String minStr = time[1];
        String secStr = "";
        String milliStr = "000";
        String[] secMilli = time[2].split("[\\.,]");
        secStr = secMilli[0];
        if (secMilli.length == 2) {
            milliStr = secMilli[1];
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date d;

        String formattedTimeStampStr = yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minStr + ":" + secStr + "." + milliStr;
        try {
            d = format.parse(formattedTimeStampStr);
            timestamp = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}
