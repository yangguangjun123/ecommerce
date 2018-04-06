package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.core.utilities.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class HVDFClientPropertyService {
    private final MongoDBService mongoDBService;

    private String channelPrefix = "activity_";
    private long period = 0L;
    private boolean isConfigued;

    private static final Map<String, Long> timeValues = new HashMap<>();
    static {
        // All valid time units used in config
        timeValues.put("milliseconds", 1L);
        timeValues.put("millisecond", 1L);
        timeValues.put("seconds", 1000L);
        timeValues.put("second", 1000L);
        timeValues.put("minutes", 60 * 1000L);
        timeValues.put("minute", 60 * 1000L);
        timeValues.put("hours", 60 * 60 * 1000L);
        timeValues.put("hour", 60 * 60 * 1000L);
        timeValues.put("days", 24 * 60 * 60 * 1000L);
        timeValues.put("day", 24 * 60 * 60 * 1000L);
        timeValues.put("weeks", 7 * 24 * 60 * 60 * 1000L);
        timeValues.put("week", 7 * 24 * 60 * 60 * 1000L);
        timeValues.put("years", 365 * 24 * 60 * 60 * 1000L);
        timeValues.put("year", 365 * 24 * 60 * 60 * 1000L);
    }

    private static final Logger logger = LoggerFactory.getLogger(HVDFClientPropertyService.class);

    // # TODO: fixed schedule of reading configuration
    @Autowired
    public HVDFClientPropertyService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostConstruct
    public void initialise() {
        readConfiguration();
    }

    private void readConfiguration() {
        Optional<Document> configOptional = mongoDBService.readOne("config",
                "hvdf_channels_ecommerce", Document.class, new HashMap<>());
        if (configOptional.isPresent()) {
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
                if (period == 0) {
                    throw new IllegalArgumentException("time slicing period cannot be zero");
                }
                LoggingUtils.info(logger,"period: " + period);
                isConfigued = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getPeriod() {
        return period;
    }

    public boolean isConfigued() {
        return isConfigued;
    }

    public String getChannelPrefix() {
        return channelPrefix;
    }
}
