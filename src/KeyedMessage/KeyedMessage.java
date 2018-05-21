package KeyedMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eddie on 2018/2/25.
 */
public class KeyedMessage {

    // a keyed message can only have one key, though a log message can be transformed to multiple keyed messages
    public String key;
    public Map<String, String> tags;
    public Double value;
    public Long timeStamp;

    public KeyedMessage(String key, Map<String, String> tags, Double value, Long timeStamp) {
        this.key = key;
        this.tags = new HashMap<>(tags);
        this.value = value;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        String res = "time: " + timeStamp.toString() + " key: " + key + " value: " + value.toString() + " tags: " + tags;

        return res;
    }
}
