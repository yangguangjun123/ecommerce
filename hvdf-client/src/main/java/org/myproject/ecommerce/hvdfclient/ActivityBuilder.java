package org.myproject.ecommerce.hvdfclient;

import java.time.LocalDateTime;
import java.util.List;

public class ActivityBuilder {
    private String userId;
    private String source;
    private int geoCode;
    private String sessionId;
    private Activity.Device device;
    private Activity.Type type;
    private String itemId;
    private String sku;
    private Activity.Order order;
    private List<Double> locations;
    private List<String> tags;
    private LocalDateTime time;
    private long timeStamp;

    public ActivityBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ActivityBuilder setSource(String source) {
        this.source = source;
        return this;
    }

    public ActivityBuilder setGeoCode(int geoCode) {
        this.geoCode = geoCode;
        return this;
    }

    public ActivityBuilder setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ActivityBuilder setDevice(Activity.Device device) {
        this.device = device;
        return this;
    }

    public ActivityBuilder setType(Activity.Type type) {
        this.type = type;
        return this;
    }

    public ActivityBuilder setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }

    public ActivityBuilder setSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ActivityBuilder setOrder(Activity.Order order) {
        this.order = order;
        return this;
    }

    public ActivityBuilder setLocations(List<Double> locations) {
        this.locations = locations;
        return this;
    }

    public ActivityBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public ActivityBuilder setTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public ActivityBuilder setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public Activity createActivity() {
        return new Activity(userId, source, geoCode, sessionId, device, type, itemId, sku, order,
                locations, tags, time, timeStamp);
    }
}