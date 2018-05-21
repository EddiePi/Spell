package spell;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eddie on 2018/2/14.
 */
public class ForceReplaceMap {

    public static ForceReplaceMap getInstance() {
        if (instance == null) {
            instance = new ForceReplaceMap();
        }
        return instance;
    }

    private static ForceReplaceMap instance = null;

    public Map<String, String> idReplaceMap;

    public Map<String, String> punctReplaceMap;

    private ForceReplaceMap() {
        idReplaceMap = new HashMap<>();
        addIdRule("appattempt_\\d+_\\d+_\\d+");
        addIdRule("application_\\d+_\\d+");
        addIdRule("container_\\d+_\\d+_\\d+_\\d+");
        addIdRule("BlockManagerId(.*)", "BlockManagerId*");
        addIdRule("attempt_\\d+_\\d+_m_\\d+_\\d+");
        addIdRule("attempt_\\d+_\\d+_r_\\d+_\\d+");
        addIdRule("jvm_\\d+_\\d+_r_\\d+");
        addIdRule("jvm_\\d+_\\d+_m_\\d+");

        punctReplaceMap = new HashMap<>();
        addPunctRule("\\(", " \\( ");
        addPunctRule("\\)", " \\) ");
        addPunctRule("=", " = ");
        addPunctRule(":", " : ");
        addPunctRule("<", " < ");
        addPunctRule(">", " > ");
        addPunctRule("\\[", " \\[ ");
        addPunctRule("\\]", " \\] ");
        addPunctRule("#", " # ");
        addPunctRule(",", " ,");
        addPunctRule(";", " ; ");
    }

    public void addIdRule(String regex) {
        idReplaceMap.put(regex, regex);
    }

    public void addIdRule(String origin, String target) {
        idReplaceMap.put(origin, target);
    }

    public void addPunctRule(String regex) {
        punctReplaceMap.put(regex, regex);
    }

    public void addPunctRule(String origin, String target) {
        punctReplaceMap.put(origin, target);
    }

    private class ReplaceItem {
        String regex;
        String target;

        public ReplaceItem(String regex) {
            this(regex, "*");

        }

        public ReplaceItem(String regex, String target) {
            this.regex = regex;
            this.target = target;
        }


    }
}
