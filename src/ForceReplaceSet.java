import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Eddie on 2018/2/14.
 */
public class ForceReplaceSet {

    public static ForceReplaceSet getInstance() {
        if (instance == null) {
            instance = new ForceReplaceSet();
        }
        return instance;
    }

    private static ForceReplaceSet instance = null;

    public Map<String, String> replaceMap;

    private ForceReplaceSet() {
        replaceMap = new HashMap<>();
        addRule("appattempt_\\d+_\\d+_\\d+");
        addRule("application_\\d+_\\d+");
        addRule("container_\\d+_\\d+_\\d+_\\d+");
        addRule("BlockManagerId(.*)");
        addRule("attempt_\\d+_\\d+_[mr]_\\d+_\\d+");
        addRule("\\(", "\\( ");
        addRule("\\)", " \\)");
    }


    public void addRule(String regex) {
        replaceMap.put(regex, regex);
    }

    public void addRule(String origin, String target) {
        replaceMap.put(origin, target);
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
