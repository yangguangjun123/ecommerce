package  org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity {
    private String userId;
    private String source;
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
    private long timeStamp;

    public Activity() {
    }

    public Activity(String userId, String source, int geoCode, String sessionId, Device device, Type type,
                    String itemId, String sku, Order order, List<Double> locations, List<String> tags,
                    LocalDateTime time, long timeStamp) {
        this.userId = userId;
        this.source = source;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        if (geoCode != activity.geoCode) return false;
        if (timeStamp != activity.timeStamp) return false;
        if (userId != null ? !userId.equals(activity.userId) : activity.userId != null) return false;
        if (source != null ? !source.equals(activity.source) : activity.source != null) return false;
        if (sessionId != null ? !sessionId.equals(activity.sessionId) : activity.sessionId != null) return false;
        if (device != null ? !device.equals(activity.device) : activity.device != null) return false;
        if (type != activity.type) return false;
        if (itemId != null ? !itemId.equals(activity.itemId) : activity.itemId != null) return false;
        if (sku != null ? !sku.equals(activity.sku) : activity.sku != null) return false;
        if (order != null ? !order.equals(activity.order) : activity.order != null) return false;
        if (locations != null ? !locations.equals(activity.locations) : activity.locations != null) return false;
        if (tags != null ? !tags.equals(activity.tags) : activity.tags != null) return false;
        return time != null ? time.equals(activity.time) : activity.time == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
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
        return "Activity{" +
                "userId='" + userId + '\'' +
                ", source='" + source + '\'' +
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

        public Order() {
        }

        public Order(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Order order = (Order) o;

            return id != null ? id.equals(order.id) : order.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }
}