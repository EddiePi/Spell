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

    public Set<String> replaceSet;

    private ForceReplaceSet() {
        replaceSet = new HashSet<>();
        addRule("appattempt_\\d+_\\d+_\\d+");
        addRule("application_\\d+_\\d+");
        addRule("container_\\d+_\\d+_\\d+_\\d+");
    }


    public void addRule(String regex) {
        replaceSet.add(regex);
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
