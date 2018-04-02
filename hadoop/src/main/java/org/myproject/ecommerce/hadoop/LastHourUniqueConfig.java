package org.myproject.ecommerce.hadoop;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.splitter.MultiCollectionSplitBuilder;
import com.mongodb.hadoop.splitter.MultiMongoCollectionSplitter;
import com.mongodb.hadoop.util.MapredMongoConfigUtil;
import com.mongodb.hadoop.util.MongoConfigUtil;
import com.mongodb.hadoop.util.MongoTool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.hvdfclient.HVDFClientPropertyService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.LongStream;

import static java.util.Comparator.reverseOrder;

public class LastHourUniqueConfig extends MongoTool {
    public LastHourUniqueConfig() {
        this(new Configuration());
    }

    public LastHourUniqueConfig(final Configuration conf) {
        setMultiCollectionInput(conf);
        setConf(conf);

        if (MongoTool.isMapRedV1()) {
            MapredMongoConfigUtil.setInputFormat(conf, com.mongodb.hadoop.mapred.MongoInputFormat.class);
            MapredMongoConfigUtil.setOutputFormat(conf, com.mongodb.hadoop.mapred.MongoOutputFormat.class);
        } else {
            MongoConfigUtil.setInputFormat(conf, MongoInputFormat.class);
            MongoConfigUtil.setOutputFormat(conf, MongoOutputFormat.class);
        }
        MongoConfigUtil.setMapper(conf, LastHourUniqueMapper.class);
        MongoConfigUtil.setMapperOutputKey(conf, Text.class);
        MongoConfigUtil.setMapperOutputValue(conf, IntWritable.class);

        MongoConfigUtil.setReducer(conf, LastHourUniqueReducer.class);
        MongoConfigUtil.setOutputKey(conf, Text.class);
        MongoConfigUtil.setOutputValue(conf, BSONWritable.class);
    }

    private void setMultiCollectionInput(final Configuration conf) {
        MongoDBService mongoDBService = new MongoDBService(Collections.emptyList());
        HVDFClientPropertyService hvdfClientPropertyService = new HVDFClientPropertyService(mongoDBService);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);
        long startTime = before.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant().toEpochMilli();
        long endTime = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant().toEpochMilli();

        BasicDBObject query = new BasicDBObject("data.ts", new BasicDBObject("$gt", startTime));
        Map<String, Object> filterMap = new HashMap<>();
        HashMap<String, Object> startTimeQueryMap = new HashMap<>();
        startTimeQueryMap.put("data.ts", startTime);
        filterMap.put("$gt", startTimeQueryMap);

        MultiCollectionSplitBuilder builder = new MultiCollectionSplitBuilder();
        String mongoDBHost = System.getProperty("mongodb_host") == null ? "mongodb://localhost:27017/ecommerce." :
                ("mongodb://" + System.getProperty("mongodb_host") + "/ecommerce.");
        LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                endTime / hvdfClientPropertyService.getPeriod()).boxed()
                .sorted(reverseOrder())
                .map(time -> mongoDBHost + hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .forEach(url -> builder.add(new
                                MongoClientURI(url), null, true, null, null, query,
                        false, null));

        conf.set(MultiMongoCollectionSplitter.MULTI_COLLECTION_CONF_KEY, builder.toJSON());
    }

    public static void main(final String[] pArgs) throws Exception {
        System.exit(ToolRunner.run(new LastHourUniqueConfig(), pArgs));
    }
}
