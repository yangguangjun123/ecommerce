package  org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {
    @BsonId
    @JsonProperty("_id")
    @JsonDeserialize(using = ActivityIdJsonDeserializer.class)
    private ActivityId id;

    private String source;

    @JsonProperty("ts")
    @BsonProperty("ts")
    private long timeStamp;

    private Data data;

    public Activity() {
    }

    public Activity(String source, long timeStamp, Data data) {
        this.source = source;
        this.timeStamp = timeStamp;
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
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

    public ActivityId getId() {
        return id;
    }

    public void setId(ActivityId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        if (timeStamp != activity.timeStamp) return false;
        if (id != null ? !id.equals(activity.id) : activity.id != null) return false;
        if (source != null ? !source.equals(activity.source) : activity.source != null) return false;
        return data != null ? data.equals(activity.data) : activity.data == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", timeStamp=" + timeStamp +
                ", data=" + data +
                '}';
    }

    public static class Data {
        private String userId;
        private int geoCode;
        private String sessionId;
        private Device device;
        private Type type;
        private String itemId;
        private String sku;
        private Order order;
        private List<Double> locations;
        private List<String> tags;
        private LocalDateTime time;

        @JsonProperty("ts")
        @BsonProperty("ts")
        private long timeStamp;

        public Data() {
        }

        public Data(String userId, int geoCode, String sessionId, Device device,
                    Type type, String itemId, String sku, Order order, List<Double> locations,
                    List<String> tags, LocalDateTime time, long timeStamp) {
            this.userId = userId;
            this.geoCode = geoCode;
            this.sessionId = sessionId;
            this.device = device;
            this.type = type;
            this.itemId = itemId;
            this.sku = sku;
            this.order = order;
            this.locations = locations;
            this.tags = tags;
            this.time = time;
            this.timeStamp = timeStamp;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getGeoCode() {
            return geoCode;
        }

        public void setGeoCode(int geoCode) {
            this.geoCode = geoCode;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public List<Double> getLocations() {
            return locations;
        }

        public void setLocations(List<Double> locations) {
            this.locations = locations;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
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

            Data data = (Data) o;

            if (geoCode != data.geoCode) return false;
            if (timeStamp != data.timeStamp) return false;
            if (userId != null ? !userId.equals(data.userId) : data.userId != null) return false;
            if (sessionId != null ? !sessionId.equals(data.sessionId) : data.sessionId != null) return false;
            if (device != null ? !device.equals(data.device) : data.device != null) return false;
            if (type != data.type) return false;
            if (itemId != null ? !itemId.equals(data.itemId) : data.itemId != null) return false;
            if (sku != null ? !sku.equals(data.sku) : data.sku != null) return false;
            if (order != null ? !order.equals(data.order) : data.order != null) return false;
            if (locations != null ? !locations.equals(data.locations) : data.locations != null) return false;
            if (tags != null ? !tags.equals(data.tags) : data.tags != null) return false;
            return time != null ? time.equals(data.time) : data.time == null;
        }

        @Override
        public int hashCode() {
            int result = userId != null ? userId.hashCode() : 0;
            result = 31 * result + geoCode;
            result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
            result = 31 * result + (device != null ? device.hashCode() : 0);
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
            result = 31 * result + (sku != null ? sku.hashCode() : 0);
            result = 31 * result + (order != null ? order.hashCode() : 0);
            result = 31 * result + (locations != null ? locations.hashCode() : 0);
            result = 31 * result + (tags != null ? tags.hashCode() : 0);
            result = 31 * result + (time != null ? time.hashCode() : 0);
            result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "userId='" + userId + '\'' +
                    ", geoCode=" + geoCode +
                    ", sessionId='" + sessionId + '\'' +
                    ", device=" + device +
                    ", type=" + type +
                    ", itemId='" + itemId + '\'' +
                    ", sku='" + sku + '\'' +
                    ", order=" + order +
                    ", locations=" + locations +
                    ", tags=" + tags +
                    ", time=" + time +
                    ", timeStamp=" + timeStamp +
                    '}';
        }
    }

    public static class Device {
        private String id;
        private String type;
        private String userAgent;

        public Device() {
        }

        public Device(String id, String type, String userAgent) {
            this.id = id;
            this.type = type;
            this.userAgent = userAgent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Device device = (Device) o;

            if (id != null ? !id.equals(device.id) : device.id != null) return false;
            if (type != null ? !type.equals(device.type) : device.type != null) return false;
            return userAgent != null ? userAgent.equals(device.userAgent) : device.userAgent == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "id='" + id + '\'' +
                    ", type='" + type + '\'' +
                    ", userAgent='" + userAgent + '\'' +
                    '}';
        }
    }

    public enum Type {
        VIEW, CART_ADD, CART_REMOVE, ORDER;
    }

    public static class Order {
        private String id;
        private int total;

        public Order() {
        }

        public Order(String id, int total) {
            this.id = id;
            this.total = total;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Order order = (Order) o;

            if (total != order.total) return false;
            return id != null ? id.equals(order.id) : order.id == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + total;
            return result;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "id='" + id + '\'' +
                    ", total=" + total +
                    '}';
        }
    }
}