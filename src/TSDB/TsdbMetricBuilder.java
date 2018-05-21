package TSDB;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eddie on 2017/6/24.
 */
public class TsdbMetricBuilder {
    private final List<TsdbMetric> metrics = new ArrayList<TsdbMetric>();
    private transient Gson mapper;

    private TsdbMetricBuilder() {
        GsonBuilder builder = new GsonBuilder();
        mapper = builder.create();
    }

    /**
     * Returns a new metric builder.
     *
     * @return metric builder
     */
    public static TsdbMetricBuilder getInstance() {
        return new TsdbMetricBuilder();
    }

    /**
     * Adds a metric to the builder.
     *
     * @param metricName
     *            metric name
     * @return the new metric
     */
    public TsdbMetric addMetric(String metricName) {
        TsdbMetric metric = new TsdbMetric(metricName);
        synchronized (metrics) {
            metrics.add(metric);
        }
        return metric;
    }

    /**
     * Returns a list of metrics added to the builder.
     *
     * @return list of metrics
     */
    public List<TsdbMetric> getMetrics() {
        return metrics;
    }

    /**
     * Returns the JSON string built by the builder. This is the JSON that can
     * be used by the client add metrics.
     *
     * @return JSON
     * @throws IOException
     *             if metrics cannot be converted to JSON
     */
    public String build(boolean needClear) throws IOException {
        for (TsdbMetric metric : metrics) {
            // verify that there is at least one tag for each metric
            if(metric.getTags().size() <= 0 || metric.getName() == null) {
                System.out.printf("metric must have at least one tag.\n");
            }
        }
        String resultJson = mapper.toJson(metrics);
        if(needClear) {
            clear();
        }
        return resultJson;
    }

    public void clear() {
        synchronized (metrics) {
            metrics.clear();
        }
    }
}
