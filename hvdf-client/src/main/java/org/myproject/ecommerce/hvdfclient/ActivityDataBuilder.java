package org.myproject.ecommerce.hvdfclient;

import java.time.LocalDateTime;
import java.util.List;

public class ActivityDataBuilder {
    private String userId;
    private int geoCode;
    private String sessionId;
    private Activity.Device device;
    private Activity.Type type;
    private String itemId;
    private int weight;
    private String sku;
    private Activity.Order order;
    private List<Double> locations;
    private List<String> tags;
    private LocalDateTime time;
    private long timeStamp;

    public ActivityDataBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ActivityDataBuilder setGeoCode(int geoCode) {
        this.geoCode = geoCode;
        return this;
    }

    public ActivityDataBuilder setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ActivityDataBuilder setDevice(Activity.Device device) {
        this.device = device;
        return this;
    }

    public ActivityDataBuilder setType(Activity.Type type) {
        this.type = type;
        return this;
    }

    public ActivityDataBuilder setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public ActivityDataBuilder setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public ActivityDataBuilder setSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ActivityDataBuilder setOrder(Activity.Order order) {
        this.order = order;
        return this;
    }

    public ActivityDataBuilder setLocations(List<Double> locations) {
        this.locations = locations;
        return this;
    }

    public ActivityDataBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public ActivityDataBuilder setTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public ActivityDataBuilder setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public Activity.Data createActivity() {
        return new Activity.Data(userId, geoCode, sessionId, device, type, itemId, weight, sku, order,
                locations, tags, time, timeStamp);
    }
}