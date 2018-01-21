package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@Service
public class HVDFClientService {
    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    private ObjectMapper objectMapper;

    private final String serviceUrl;
    private static final String CONFIG_URL = "/feed/{feed}/{channel}/config";
    private static final String POST_SAMPLE_URL = "/feed/{feed}/{channel}/data";
    private static final String QUERY_SAMPLE = "http://localhost:8080/feed/ecommerce/activity/data";
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

        if(!isHVDFServiceConfigured("config", "hvdf_channels_ecommerce")) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject hvdfJsonConfig = (JSONObject) parser.parse(IOUtils.toString(getClass()
                        .getResourceAsStream("/hvdf-channel-config.json"), StandardCharsets.UTF_8.name()));
                configure("ecommerce", "activity", hvdfJsonConfig.toJSONString());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            populateActivities();
        }
    }

    private void populateActivities() {
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185"))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime now = LocalDateTime.now();
        builder.setTime(now)
               .setTimeStamp(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        record(new Activity("u123",
                now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity()));
        builder = new ActivityDataBuilder();
        builder.setUserId("u457").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185"))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        now = LocalDateTime.now();
        builder.setTime(now)
                .setTimeStamp(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        record(new Activity("u457",
                now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity()));
    }

    public boolean record(Activity activity) {
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("sample", objectMapper.writeValueAsString(activity));
            String requestUrl = serviceUrl + POST_SAMPLE_URL;
            requestUrl = StringUtils.replace(requestUrl, "{feed}", "ecommerce");
            requestUrl = StringUtils.replace(requestUrl, "{channel}",
                    activity.getClass().getSimpleName().toLowerCase());
            ResponseEntity<String> response = send(requestUrl, paramsMap, HttpMethod.POST);
            return HttpStatus.OK == response.getStatusCode();
        } catch (Exception e) {
            logger.error("unable to record activity: " + activity.toString());
            e.printStackTrace();
            return false;
        }
    }

    public boolean record(List<Activity> activities, Class clazz) {
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("sample", objectMapper.writeValueAsString(activities));
            String requestUrl = serviceUrl + POST_SAMPLE_URL;
            requestUrl = StringUtils.replace(requestUrl, "{feed}", "ecommerce");
            requestUrl = StringUtils.replace(requestUrl, "{channel}",
                    clazz.getSimpleName().toLowerCase());
            ResponseEntity<String> response = send(requestUrl, paramsMap, HttpMethod.POST);
            return HttpStatus.OK == response.getStatusCode();
        } catch (Exception e) {
            logger.error("unable to record activities: "
                    + activities.stream()
                                .map(Activity::toString)
                                .collect(joining(",")));
            e.printStackTrace();
            return false;
        }
    }

    public List<Activity> query(Map<String, Object> criteriaMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(QUERY_SAMPLE);
        criteriaMap.keySet().stream()
                            .forEach(key -> {
                                if(criteriaMap.get(key) instanceof String) {
                                    builder.queryParam(key, "\"" + criteriaMap.get(key) + "\"");
                                } else {
                                    builder.queryParam(key, criteriaMap.get(key));
                                }
                            });
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
                logger.error("unable to convert query results" );
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    private void configure(String feed, String channel, String configJsonString) {
        logger.info("HVDF configuration(json format): " + configJsonString);
        try {
            String requestUrl = serviceUrl + CONFIG_URL;
            requestUrl = StringUtils.replace(requestUrl, "{feed}", feed);
            requestUrl = StringUtils.replace(requestUrl, "{channel}", channel);
            send(requestUrl, Map.of("value", configJsonString), HttpMethod.PUT);
        } catch(Exception e) {
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
        Optional<Document> configOptional =  mongoDBService.readOne(databaseName, collectionName,
                Document.class, new HashMap<>());
        return configOptional.isPresent();
    }

    private void customeJsonmapping(SimpleModule simpleModule) {
        simpleModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException, JsonProcessingException {
                return Instant.ofEpochMilli(jsonParser.getLongValue()).atZone(ZoneId.of("UTC")).toLocalDateTime();
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