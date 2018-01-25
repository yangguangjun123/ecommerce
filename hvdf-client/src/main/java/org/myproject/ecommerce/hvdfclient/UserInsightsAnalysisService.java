package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserInsightsAnalysisService {
    private final MongoDBService mongoDBService;

    private String channelPrefix = "activity_";
    private long period = 0L;

    private static final Map<String,Long> timeValues = new HashMap<>();
    static {
        // All valid time units used in config
        timeValues.put("milliseconds",  1L);
        timeValues.put("millisecond",   1L);
        timeValues.put("seconds",       1000L);
        timeValues.put("second",        1000L);
        timeValues.put("minutes",       60*1000L);
        timeValues.put("minute",        60*1000L);
        timeValues.put("hours",         60*60*1000L);
        timeValues.put("hour",          60*60*1000L);
        timeValues.put("days",          24*60*60*1000L);
        timeValues.put("day",           24*60*60*1000L);
        timeValues.put("weeks",         7*24*60*60*1000L);
        timeValues.put("week",          7*24*60*60*1000L);
        timeValues.put("years",         365*24*60*60*1000L);
        timeValues.put("year",          365*24*60*60*1000L);
    }

    private static final Logger logger = LoggerFactory.getLogger(UserInsightsAnalysisService.class);

    @Autowired
    public UserInsightsAnalysisService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostConstruct
    public void initilaise() {
        Optional<Document> configOptional =  mongoDBService.readOne("config", "hvdf_channels_ecommerce",
                Document.class, new HashMap<>());
        if(configOptional.isPresent()) {
            String configJsonString = configOptional.get().toJson();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readValue(configJsonString, JsonNode.class);
                Objects.requireNonNull(rootNode.get("storage"));
                Objects.requireNonNull(rootNode.get("storage").get("type"));
                String storageType = rootNode.get("storage").get("type").textValue();
                channelPrefix = channelPrefix + storageType + "_";
                Objects.requireNonNull(rootNode.get("time_slicing"));
                Objects.requireNonNull(rootNode.get("time_slicing").get("config"));
                Objects.requireNonNull(rootNode.get("time_slicing").get("config").get("period"));
                JsonNode periodNode = rootNode.get("time_slicing").get("config").get("period");
                periodNode.fields().forEachRemaining(entry ->
                        period = period + timeValues.get(entry.getKey()) * entry.getValue().asInt());
                if(period == 0) {
                    throw new IllegalArgumentException("time slicing period cannot be zero");
                }
                logger.info("period: " + period);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Activity> findUserActivities(String userId, long startTime, long endTime) {
        List<Activity> result = new ArrayList<>();
        result.addAll(findUserActivities(userId, startTime, endTime,
                channelPrefix + endTime/period));
        if(!(channelPrefix + endTime/period).equals(channelPrefix + startTime/period)) {
            result.addAll(findUserActivities(userId, startTime, endTime,
                    channelPrefix + startTime/period));
        }
        return result;
    }

    private List<Activity> findUserActivities(String userId, long startTime, long endTime,
                                              String collectionName) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(collectionName);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("data.userId", "u123");
        HashMap<String, Object> startTimeQueryMap = new HashMap<>();
        startTimeQueryMap.put("ts", startTime);
        filterMap.put("$gt", startTimeQueryMap);
        HashMap<String, Object> endTimeQueryMap = new HashMap<>();
        endTimeQueryMap.put("ts", endTime);
        filterMap.put("$lt", endTimeQueryMap);
        Map<String, Integer> sortMap = new LinkedHashMap<>();
        sortMap.put("data.time", -1);
        return mongoDBService.readAll("ecommerce", collectionName,
                Activity.class, filterMap, Optional.of(sortMap));
    }
}
