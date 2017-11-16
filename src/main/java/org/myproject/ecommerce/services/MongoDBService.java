package org.myproject.ecommerce.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Updates.*;
import static java.util.stream.Collectors.toList;

@Service("mongoDBService")
@Qualifier("mongoDBService")
public class MongoDBService {
    private MongoClient mongoClient;

    public MongoDBService() {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        mongoClient = new MongoClient("localhost", MongoClientOptions.builder()
                .codecRegistry(pojoCodecRegistry).build());
    }

    public <T> void write(String databaseName, String collectionName, Class<T> clazz, T document) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        collection.insertOne(document);
    }

    public <T> List<T> readByEqualFiltering(String databaseName, String collectionName, Class<T> clazz,
                               Map<String, Object> filter) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        List<Bson> filters = filter.keySet()
                .stream()
                .map(key -> Filters.eq(key, filter.get(key)))
                .collect(Collectors.toList());
        List<T> result = new ArrayList<>();
        Consumer<? super T> consumer = t -> result.add(t);
        collection.find(combine(filters)).forEach(consumer);
        return result;
    }

    public <T> long getDocumentCount(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        return collection.count();
    }

    public <T> boolean addOne(String databaseName, String collectionName, Class<T> clazz,
                              Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        return process(databaseName, collectionName, clazz, queryFilterMap,
                valueMap, this::convert);
    }

    public <T> boolean removeOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        return process(databaseName, collectionName, clazz, queryFilterMap,
                valueMap, this::convert);
    }

    public <T> boolean updateOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        return process(databaseName, collectionName, clazz, queryFilterMap,
                valueMap, this::convert);
    }

    public void delete(String databaseName, String collectionName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        collection.deleteMany(new Document());
    }

    public void writeJson(String databaseName, String collectionName, String jsonString) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        collection.insertOne(Document.parse(jsonString));
    }

    public <T> T readJson(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        String serialize = JSON.serialize(collection.find());
        ObjectMapper objectMapper = new ObjectMapper();
        T result = null;
        try {
            result = objectMapper.readValue(serialize, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("dispose of mongoClient");
        mongoClient.close();
    }

    private <T> boolean process(String databaseName, String collectionName, Class<T> clazz,
                                           Map<String, Object> queryFilterMap, Map<String, Object> valueMap,
                                           Function<Map<String, Object>, List<Bson>> convert) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        List<Document> documents = readByEqualFiltering("ecommerce",
                "product", Document.class, queryFilterMap);
        Document document  = documents.get(0);
        queryFilterMap.put("_id", document.get("_id"));
        List<Bson> filters = queryFilterMap.keySet()
                .stream()
                .map(key -> Filters.eq(key, queryFilterMap.get(key)))
                .collect(toList());
        List<Bson>  updates = convert.apply(valueMap);
        collection.updateOne(Filters.and(filters), combine(updates));
        return true;
    }

    private List<Bson> convert(Map<String, Object> valueMap) {
        List<Bson> addOrRemoveOperators = convertAddOrRemove(Optional.ofNullable((Map<String, Object>)
                valueMap.get("addOrRemove")));

        List<Bson> increaseOperators = convertIncreaseOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("increase")));

        List<Bson> decreaseOperators = convertDecreaseOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("decrease")));

        List<Bson> pullOperators = convertIncreaseOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("pull")));

        List<Bson> combined = new ArrayList<>();
        combined.addAll(addOrRemoveOperators);
        combined.addAll(increaseOperators);
        combined.addAll(decreaseOperators);
        combined.addAll(pullOperators);
        return combined;
    }

    private List<Bson> convertAddOrRemove(Optional<Map<String, Object>> valueMapOptional) {
        if(!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> {
                    if (valueMap.get(key) instanceof List) {
                        if(((List) valueMap.get(key)).size() > 0) {
                            return pushEach(key, (List) valueMap.get(key));
                        } else {
                            return popLast(key);
                        }
                    } else {
                        if(valueMap.get(key) instanceof Optional && !((Optional) valueMap.get(key)).isPresent()) {
                            return unset(key);
                        } else {
                            return set(key, valueMap.get(key));
                        }
                    }
                })
                .collect(toList());
    }

    private List<Bson> convertIncreaseOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> inc(key, (Integer) valueMap.get(key)))
                .collect(toList());
    }

    private List<Bson> convertDecreaseOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> inc(key, Math.negateExact((Integer) valueMap.get(key))))
                .collect(toList());
    }

    private List<Bson> convertPullOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> pull(key, valueMap.get(key)))
                .collect(toList());
    }
}
