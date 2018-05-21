package KeyedMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eddie on 2018/3/7.
 */
public class ValueTokenMap {

    public static ValueTokenMap getInstance() {
        if (instance == null) {
            instance = new ValueTokenMap();
        }
        return instance;
    }

    private static ValueTokenMap instance = null;

    public Map<String, String> tokenMap;

    private ValueTokenMap() {
        tokenMap = new HashMap<>();
        addRule("decomp");
        addRule("len");
    }

    public void addRule(String token) {
        tokenMap.put(token, token);
    }

    public void addRule(String origin, String target) {
        tokenMap.put(origin, target);
    }
}
