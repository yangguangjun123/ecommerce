package org.myproject.ecommerce.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.result.UpdateResult;
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
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
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

    public <T> void createOne(String databaseName, String collectionName, Class<T> clazz, T document) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        collection.insertOne(document);
    }

    public <T> void createAll(String databaseName, String collectionName, Class<T> clazz, List<T> documents) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        collection.insertMany(documents);
    }

    public <T> List<T> readAll(String databaseName, String collectionName, Class<T> clazz,
                               Map<String, Object> filter) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        List<Bson> filters = filter.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filter))
                .collect(Collectors.toList());
        List<T> result = new ArrayList<>();
        Consumer<? super T> consumer = t -> result.add(t);
        collection.find(combine(filters)).forEach(consumer);
        return result;
    }

    public <T> void readAll(String databaseName, String collectionName, Class<T> clazz,
                            Map<String, Object> filter, Consumer<T> consumer) {
        readAll(databaseName, collectionName, clazz, filter)
                .stream()
                .forEach(consumer);
    }

    public <T> List<T> readAll(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        List<T> result = new ArrayList<>();
        Consumer<? super T> consumer = t -> result.add(t);
        collection.find().forEach(consumer);
        return result;
    }

    public <T> Optional<T> readById(String databaseName, String collectionName, Class<T> clazz, Object id) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("_id", id);
        return this.readOne(databaseName, collectionName, clazz, filter);
    }

    public <T> Optional<T> readOne(String databaseName, String collectionName, Class<T> clazz,
                                            Map<String, Object> filter) {
        List<T> results = readAll(databaseName, collectionName, clazz, filter);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public <T> long getDocumentCount(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        return collection.count();
    }

    public <T> boolean addOne(String databaseName, String collectionName, Class<T> clazz,
                              Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        return process(databaseName, collectionName, clazz, queryFilterMap,
                valueMap, new HashMap<>(), this::convert);
    }

    public <T> boolean removeOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        return process(databaseName, collectionName, clazz, queryFilterMap,
                valueMap, new HashMap<>(), this::convert);
    }

    public <T> boolean updateMany(String databaseName, String collectionName, Map<String, Object> queryFilterMap,
                                  Map<String, Object> valueMap) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = queryFilterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, queryFilterMap))
                .collect(toList());
        List<Bson>  updates = convert(valueMap);
        collection.updateMany(and(filters), combine(updates));
        return true;
    }

    public <T> boolean updateOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> queryFilterMap, Map<String, Object> valueMap,
                                 Map<String, Object> updateOptions) {
        return process(databaseName, collectionName, clazz, queryFilterMap,
                valueMap, updateOptions, this::convert);
    }

    public void deleteAll(String databaseName, String collectionName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        collection.deleteMany(new Document());
    }

    public void deleteMany(String databaseName, String collectionName, Map<String, Object> filterMap) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = filterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filterMap))
                .collect(toList());
        collection.deleteMany(and(filters));
    }

    public void deleteOne(String databaseName, String collectionName, Map<String, Object> filterMap) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = filterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filterMap))
                .collect(toList());
        collection.deleteOne(and(filters));
    }

    public <T> boolean replaceOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> filterMap, T value) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = filterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filterMap))
                .collect(toList());
        UpdateResult result = collection.replaceOne(and(filters), value);
        return result.getModifiedCount() == 1L;
    }

    public <T> long count(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        return collection.count();
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
                                           Map<String, Object> queryFilterMap, Map<String, Object> updateMap,
                                           Map<String, Object> updateOptions,
                                           Function<Map<String, Object>, List<Bson>> convert) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        List<Document> documents = readAll("ecommerce",
                collectionName, Document.class, queryFilterMap);

        if(documents.size() == 0) {
            System.out.println("documents contain no record: " +
                    Objects.toString(queryFilterMap));
            return false;
        }

        if(documents.size() > 1) {
            System.out.println("documents contain more than one record: " +
                    Objects.toString(queryFilterMap));
            return false;
        }

        Document document  = documents.get(0);
        queryFilterMap.put("_id", document.get("_id"));
        List<Bson> filters = queryFilterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, queryFilterMap))
                .collect(toList());
        List<Bson>  updates = convert.apply(updateMap);
        if(updateOptions.containsKey("writeConcern")) {
            collection = collection.withWriteConcern(WriteConcern.valueOf(
                    (String) updateOptions.get("writeConcern")));
        }
        collection.updateOne(and(filters), combine(updates));
        return true;
    }

    private Bson mapBsonFilter(String key, final Map<String, Object>  queryFilterMap) {
        Objects.requireNonNull(key);
        if(key.equals("$eq")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$eq");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return eq(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$gte")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$gte");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return gte(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$lt")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$lt");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return lt(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$in")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$in");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return in(keys.get(0), (List) fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$regex")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$regex");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return regex(keys.get(0), (Pattern) fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$nearSphere")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$nearSphere");
            return nearSphere((String) fieldValueMap.get("fieldName"), (Point) fieldValueMap.get("geometry"),
                    (Double) fieldValueMap.get("maxDistance"), (Double) fieldValueMap.get("minDistance"));
        } else {
            return eq(key, queryFilterMap.get(key));
        }
    }

    private List<Bson> convert(Map<String, Object> valueMap) {
        List<Bson> addOrRemoveOperators = convertAddOrRemove(Optional.ofNullable((Map<String, Object>)
                valueMap.get("addOrRemove")));

        List<Bson> incOperators = convertIncOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("inc")));

        Optional<Bson> pullOperators = convertPullOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("pull")));

        List<Bson> combined = new ArrayList<>();
        combined.addAll(addOrRemoveOperators);
        combined.addAll(incOperators);
        if(pullOperators.isPresent()) {
            combined.add(pullOperators.get());
        }
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

    private List<Bson> convertIncOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> inc(key, (Integer) valueMap.get(key)))
                .collect(toList());
    }

    private Optional<Bson> convertPullOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return Optional.empty();
        }

        Map<String, Object> valueMap = valueMapOptional.get();
        Object[] keys = valueMap.keySet().toArray();
        String key = (String) keys[0];
        Object value = valueMap.get(key);
        String[] fields = key.split("\\.");
        Bson filter = Filters.eq(fields[fields.length - 1], value);
        for(int i = fields.length - 2; i >= 0; i--) {
            filter = Filters.eq(fields[i], filter);
        }

        //Bson filter = Filters.eq("carted", Filters.eq("cart_id", 42));
        return Optional.of(Updates.pullByFilter(filter));
    }
}
