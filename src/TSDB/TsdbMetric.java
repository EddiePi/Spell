package TSDB;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A metric contains measurements or data points. Each data point has a time
 * stamp of when the measurement occurred and a value that is either a long or
 * double and optionally contains tags. Tags are labels that can be added to
 * better identify the metric. For example, if the measurement was done on
 * server1 then you might add a tag named "host" with a value of "server1". Note
 * that a metric must have at least one tag.
 */
public class TsdbMetric {

    @SerializedName("metric")
    private String name;

    private long timestamp;

    private Object value;

    private Map<String, String> tags = new HashMap<String, String>();

    protected TsdbMetric(String name) {
        this.name = name;
    }

    /**
     * Adds a tag to the data point.
     *
     * @param name
     *            tag identifier
     * @param value
     *            tag value
     * @return the metric the tag was added to
     */
    public TsdbMetric addTag(String name, String value) {
        tags.put(name, value);

        return this;
    }

    /**
     * Adds tags to the data point.
     *
     * @param tags
     *            map of tags
     * @return the metric the tags were added to
     */
    public TsdbMetric addTags(Map<String, String> tags) {
        this.tags.putAll(tags);

        return this;
    }

    /**
     * set the data point for the metric.
     *
     * @param timestamp
     *            when the measurement occurred
     * @param value
     *            the measurement value
     * @return the metric
     */
    protected TsdbMetric innerAddDataPoint(long timestamp, Object value) {
        this.timestamp = timestamp;
        this.value = value;

        return this;
    }

    /**
     * Adds the data point to the metric with a timestamp of now.
     *
     * @param value
     *            the measurement value
     * @return the metric
     */
    public TsdbMetric setDataPoint(long value) {
        return innerAddDataPoint(System.currentTimeMillis(), value);
    }

    public TsdbMetric setDataPoint(long timestamp, long value) {
        return innerAddDataPoint(timestamp, value);
    }

    /**
     * Adds the data point to the metric.
     *
     * @param timestamp
     *            when the measurement occurred
     * @param value
     *            the measurement value
     * @return the metric
     */
    public TsdbMetric setDataPoint(long timestamp, double value) {
        return innerAddDataPoint(timestamp, value);
    }

    /**
     * Adds the data point to the metric with a timestamp of now.
     *
     * @param value
     *            the measurement value
     * @return the metric
     */
    public TsdbMetric setDataPoint(double value) {
        return innerAddDataPoint(System.currentTimeMillis(), value);
    }

    /**
     * Time when the data point was measured.
     *
     * @return time when the data point was measured
     */
    public long getTimestamp() {
        return timestamp;
    }

    public Object getValue() {
        return value;
    }

    public String stringValue() throws DataFormatException {
        return value.toString();
    }

    public long longValue() throws DataFormatException {
        try {
            return ((Number) value).longValue();
        } catch (Exception e) {
            throw new DataFormatException("Value is not a long");
        }
    }

    public double doubleValue() throws DataFormatException {
        try {
            return ((Number) value).doubleValue();
        } catch (Exception e) {
            throw new DataFormatException("Value is not a double");
        }
    }

    public boolean isDoubleValue() {
        return !(((Number) value).doubleValue() == Math.floor(((Number) value)
                .doubleValue()));
    }

    public boolean isIntegerValue() {
        return ((Number) value).doubleValue() == Math.floor(((Number) value)
                .doubleValue());
    }

    /**
     * Returns the metric name.
     *
     * @return metric name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the tags associated with the data point.
     *
     * @return tag for the data point
     */
    public Map<String, String> getTags() {
        return Collections.unmodifiableMap(tags);
    }

}
