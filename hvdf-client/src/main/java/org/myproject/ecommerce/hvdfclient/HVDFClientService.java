package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.joining;

@Service
public class HVDFClientService {
    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    private ObjectMapper objectMapper;

    private final String serviceUrl;
    private static final String CONFIG_URL = "/feed/{feed}/{channel}/config";
    private static final Logger logger = LoggerFactory.getLogger(HVDFClientService.class);

    public HVDFClientService() {
        this("http://localhost:8080");
    }

    public HVDFClientService(String serviceUrl) {
        Objects.requireNonNull(serviceUrl);
        this.serviceUrl = serviceUrl;
    }

    @PostConstruct
    public void initialise() {
        objectMapper = springMvcJacksonConverter.getObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        SimpleModule simpleModule = new SimpleModule();
        customeJsonmapping(simpleModule);
        objectMapper.registerModule(simpleModule);

        if(!isHVDFServiceConfigured("ecommerce", "hvdf_config")) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject hvdfJsonConfig = (JSONObject) parser.parse(IOUtils.toString(getClass()
                        .getResourceAsStream("/hvdf-config.json"), StandardCharsets.UTF_8.name()));
                mongoClient.getDatabase("ecommerce")
                        .getCollection("activity").drop();
                mongoClient.getDatabase("ecommerce").getCollection("hvdf_config").drop();
                configure("ecommerce", "hvdf_config", hvdfJsonConfig.toJSONString());

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        populateActivities();
    }

    private void populateActivities() {
        ActivityBuilder builder = new ActivityBuilder();
        builder.setUserId("u123").setSource("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185"))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime now = LocalDateTime.now();
        builder.setTime(now)
                .setTimeStamp(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        record(builder.createActivity());
        builder = new ActivityBuilder();
        builder.setUserId("user457").setSource("user457").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185"))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        now = LocalDateTime.now();
        builder.setTime(now)
                .setTimeStamp(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        record(builder.createActivity());
    }

    public void record(Activity activity) {
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("sample", objectMapper.writeValueAsString(activity));
            send("http://localhost:8080/feed/ecommerce/activity/data", paramsMap, HttpMethod.POST);
        } catch (Exception e) {
            logger.error("unable to record activity: " + activity.toString());
            e.printStackTrace();
        }
    }

    public void record(List<Activity> activities) {
        ArrayNode array = objectMapper.createArrayNode();
        activities.stream()
                  .forEach(a -> array.add(objectMapper.createArrayNode().addPOJO(a)));
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("sample", objectMapper.writeValueAsString(array));
            send("http://localhost:8080/feed/ecommerce/activity/data", paramsMap, HttpMethod.POST);
        } catch (Exception e) {
            logger.error("unable to record activities: "
                    + activities.stream()
                                .map(Activity::toString)
                                .collect(joining(",")));
            e.printStackTrace();
        }
    }

    public List<Activity> query(Map<String, Object> criteriaMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                "http://localhost:8080/feed/ecommerce/activity/data");
        criteriaMap.keySet().stream()
                            .forEach(key -> builder.queryParam(key, criteriaMap.get(key)));
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                String.class);
        if(response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
            try {
                return objectMapper.readValue(response.getBody(),
                        new TypeReference<List<Activity>>(){});
            } catch (IOException e) {
                logger.error("unable to query activity: " + criteriaMap);
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    private void configure(String databaseName, String collectionName, String configJsonString) {
        logger.info("HVDF configuration(json format): " + configJsonString);
        try {
            String requestUrl = serviceUrl + CONFIG_URL;
            requestUrl = StringUtils.replace(requestUrl, "{feed}", databaseName);
            requestUrl = StringUtils.replace(requestUrl, "{channel}", collectionName);
            ResponseEntity<String> response = send(requestUrl, Map.of("value", configJsonString), HttpMethod.PUT);
            if(response.getStatusCode() == HttpStatus.NO_CONTENT) {
                MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
                MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                collection.insertOne(Document.parse(configJsonString));
            }
        } catch(Exception e) {
            if(!Objects.isNull(mongoClient.getDatabase(databaseName)) &&
                    Objects.isNull(mongoClient.getDatabase(databaseName).getCollection(collectionName))) {
                mongoClient.getDatabase(databaseName).getCollection(collectionName).drop();
            }
        }
    }

    private ResponseEntity<String> send(String requestUrl, Map<String, String> paramsMap,
                                                  HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
        paramsMap.keySet().stream()
                .forEach(key -> {
                    try {
                        builder.queryParam(key, URLEncoder.encode(paramsMap.get(key),
                                StandardCharsets.UTF_8.name()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
        HttpEntity<?> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(
                builder.build(true).toUri(),
                method,
                entity,
                String.class);
    }

    private boolean isHVDFServiceConfigured(String databaseName, String collectionName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        return collection.count() > 0;
    }

    private void customeJsonmapping(SimpleModule simpleModule) {
        simpleModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException, JsonProcessingException {
                return LocalDateTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });

        simpleModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(
                    LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                    throws IOException {
                ZonedDateTime ldtZoned = localDateTime.atZone(ZoneId.systemDefault());
                ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
                jsonGenerator.writeString(Date.from(utcZoned.toInstant()).toInstant().toString());
            }
        });
    }
}