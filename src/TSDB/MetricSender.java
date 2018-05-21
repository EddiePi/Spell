package TSDB;

import java.io.IOException;
import java.util.Map;
import utils.HTTPRequest;

/**
 * Created by Eddie on 2018/3/5.
 */
public class MetricSender {
    TsdbMetricBuilder builder = TsdbMetricBuilder.getInstance();
    String databaseURI;

    public MetricSender() {
        databaseURI = "128.198.180.110:4242";
        if (!databaseURI.matches("http://.*")) {
            databaseURI = "http://" + databaseURI;
        }
        if (!databaseURI.matches(".*/api/put")) {
            databaseURI = databaseURI + "/api/put";
        }
    }

    public void addMessage(Long timestamp, String key, Double value, Map<String, String> tags) {
        builder.addMetric(key)
                .setDataPoint(timestamp, value)
                .addTags(tags);
    }

    public void sendMessage() {
        try {
            String message;
            synchronized (builder) {
                message = builder.build(true);
            }
            // TODO: maintain the connection for performance
            String response = HTTPRequest.sendPost(databaseURI, message);
            if (!response.matches("\\s*")) {
                System.out.printf("Unexpected response: %s\n", response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
