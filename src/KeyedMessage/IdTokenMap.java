package KeyedMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eddie on 2018/2/25.
 */
public class IdTokenMap {

    public static IdTokenMap getInstance() {
        if (instance == null) {
            instance = new IdTokenMap();
        }
        return instance;
    }

    private static IdTokenMap instance = null;

    public Map<String, String> tokenMap;

    private IdTokenMap() {
        tokenMap = new HashMap<>();
        addRule("shuffle");
        addRule("block");
        addRule("task");
        addRule("tid");
        addRule("stage");
        addRule("variable");
        addRule("fetcher");
        addRule("port");
        addRule("id");
    }

    public void addRule(String token) {
        tokenMap.put(token, token);
    }

    public void addRule(String origin, String target) {
        tokenMap.put(origin, target);
    }
}
