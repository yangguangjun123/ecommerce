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
import org.myproject.ecommerce.core.utilities.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final MongoDBService mongoDBService;
    private final RestTemplate restTemplate;
    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    private ObjectMapper objectMapper;
    private final String serviceUrl;
    private final String querySample;

    private static final String CONFIG_URL = "/feed/{feed}/{channel}/config";
    private static final String POST_SAMPLE_URL = "/feed/{feed}/{channel}/data";
    private static final Logger logger = LoggerFactory.getLogger(HVDFClientService.class);

    @Autowired
    public HVDFClientService(@Qualifier("hvdfServiceUrl") String serviceUrl, MongoDBService mongoDBService,
                             RestTemplate restTemplate,
                             MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        Objects.requireNonNull(serviceUrl);
        this.serviceUrl = serviceUrl;
        querySample = serviceUrl + "/feed/ecommerce/activity/data";
        this.mongoDBService = mongoDBService;
        this.restTemplate = restTemplate;
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }

    @PostConstruct
    public void initialise() {
        objectMapper = mappingJackson2HttpMessageConverter.getObjectMapper();
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
        }
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(querySample);
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
        LoggingUtils.info(logger, "HVDF configuration(json format): " + configJsonString);
        try {
            String requestUrl = serviceUrl + CONFIG_URL;
            requestUrl = StringUtils.replace(requestUrl, "{feed}", feed);
            requestUrl = StringUtils.replace(requestUrl, "{channel}", channel);

            // Java 9 feature
//            send(requestUrl, Map.of("value", configJsonString), HttpMethod.PUT);
            send(requestUrl, new HashMap() {{ put("value", configJsonString); }}, HttpMethod.PUT);
        } catch(Exception e) {}
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