package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class ActivityId {
    private String source;

    @JsonProperty("ts")
    @BsonProperty("ts")
    private long timeStamp;

    public ActivityId() {
    }

    public ActivityId(String source, long ts) {
        this.source = source;
        this.timeStamp = ts;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityId that = (ActivityId) o;

        if (timeStamp != that.timeStamp) return false;
        return source != null ? source.equals(that.source) : that.source == null;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ActivityId{" +
                "source='" + source + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
