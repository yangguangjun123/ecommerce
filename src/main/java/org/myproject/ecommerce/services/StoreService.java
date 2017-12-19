package org.myproject.ecommerce.services;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.types.ObjectId;
import org.myproject.ecommerce.domain.Store;
import org.myproject.ecommerce.domain.StoreBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StoreService {
    @Autowired
    private MongoDBService mongoDBService;

    @PostConstruct
    public void initialise() {
        if(mongoDBService.getDocumentCount("ecommerce", "store",
                Store.class) != 1001) {
            mongoDBService.deleteAll("ecommerce", "store");
            List<Store> stores =
                    IntStream.rangeClosed(1, 1000).boxed()
                             .map(i -> createStore(new ObjectId(), "store" + i))
                             .collect(Collectors.toList());
            mongoDBService.createAll("ecommerce", "store", Store.class, stores);

            Store store = new Store(new ObjectId(), "sgroup12");
            store.setStoreGroups(Arrays.asList("store12"));
            mongoDBService.createOne("ecommerce", "store", Store.class, store);

            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("storeId", "store100");
            Store store100 = mongoDBService.readOne("ecommerce", "store",
                    Store.class, filterMap).get();
            Store store100Updated = new StoreBuilder().setId(store100.getId()).setStoreId("store100")
                    .setClassName("catalog.Store").setName("Bessemer Store")
                    .setAddress(new Store.StoreAddress("1 Main St.", "Bessemer",
                            "AL", "12345", "USA"))
                    .setLocation(Arrays.asList(-86.95444, 33.40178))
                    .createStore();
            mongoDBService.replaceOne("ecommerce", "store", Store.class,
                    filterMap, store100Updated);
        }
    }

    public List<Store> getAllStores() {
        return mongoDBService.readAll("ecommerce", "store", Store.class);
    }

    public List<Store> getStores(double[] coordinates, double maxDistance, double minDistance) {
        Point refPoint = new Point(new Position(coordinates[0], coordinates[1]));
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("fieldName", "location");
        valueMap.put("geometry", refPoint);
        valueMap.put("maxDistance", maxDistance);
        valueMap.put("minDistance", minDistance);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("$nearSphere", valueMap);
        return mongoDBService.readAll("ecommerce",  "store", Store.class, filterMap);
    }

    private Store createStore(ObjectId id, String storeId) {
        return new StoreBuilder().setId(id).setStoreId(storeId)
                .setClassName("catalog.Store").setName(storeId)
                .setAddress(new Store.StoreAddress(storeId, storeId,
                        storeId, storeId, "USA"))
                .setLocation(Arrays.asList(-86.95444, 33.40178))
                .createStore();
    }
}
