package org.myproject.ecommerce.services;

import org.myproject.ecommerce.domain.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class StoreService {
    @Autowired
    private MongoDBService mongoDBService;

    @PostConstruct
    public void initialise() {
        mongoDBService.deleteAll("ecommerce", "store");
        if(mongoDBService.getDocumentCount("ecommerce", "store",
                Store.class) != 1001) {
            List<Store> stores =
                    IntStream.rangeClosed(1, 1000).boxed()
                             .map(i -> new Store(String.valueOf(i), "store" + i))
                             .collect(Collectors.toList());
            mongoDBService.createAll("ecommerce", "store", Store.class, stores);
            Store store = new Store(String.valueOf(1001), "sgroup12");
            store.setStoreGroups(Arrays.asList("store12"));
            mongoDBService.createOne("ecommerce", "store", Store.class, store);
        }
    }

    public List<Store> getAllStores() {
        return mongoDBService.readAll("ecommerce", "store", Store.class);
    }
}
