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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.hvdfclient.HVDFClientPropertyService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.LongStream;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;

public class LastHourUniqueConfig extends MongoTool {
    private static final Log logger = LogFactory.getLog(LastHourUniqueConfig.class);

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
//        String mongoHost = "localhost";
//        int mongoPort = 27017;
        String mongoHost = Optional.ofNullable(System.getProperty("mongodb_host"))
                                   .map(s -> s.split(":"))
                                   .map(Arrays::stream)
                                   .flatMap(s -> s.findFirst())
                                   .orElse("localhost");
        int mongoPort = Optional.ofNullable(System.getProperty("mongodb_host"))
                                .map(s -> s.split(":"))
                                .map(Arrays::stream)
                                .flatMap(s -> s.skip(1).findFirst())
                                .map(s -> Integer.parseInt(s))
                                .orElse(27017);
        logger.info("mongo host: " + mongoHost);
        logger.info("mongo port: " + mongoPort);

//        if(System.getProperty("mongodb_host") != null) {
//            String[] temp = System.getProperty("mongodb_host").split(":");
//            if(temp != null && temp.length == 1 ) {
//                mongoHost = temp[0];
//            }
//            if(temp != null && temp.length == 2 ) {
//                mongoHost = temp[0];
//                mongoPort = Integer.parseInt(temp[1]);
//            }
//        }

        MongoDBService mongoDBService = new MongoDBService(Collections.emptyList(), mongoHost, mongoPort);
        HVDFClientPropertyService hvdfClientPropertyService = new HVDFClientPropertyService(mongoDBService);
        hvdfClientPropertyService.initialise();

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
        logger.info("mongoDBHost: " + mongoDBHost);

        LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                endTime / hvdfClientPropertyService.getPeriod()).boxed()
                .sorted(reverseOrder())
                .map(time -> mongoDBHost + hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .forEach(url -> builder.add(new
                                MongoClientURI(url), null, true, null, null, query,
                        false, null));
        logger.info("MultiCollectionSplitBuilder: " + builder.toJSON());

        conf.set(MultiMongoCollectionSplitter.MULTI_COLLECTION_CONF_KEY, builder.toJSON());
    }

    public static void main(final String[] pArgs) throws Exception {
        logger.info("pArgs: " + Arrays.stream(pArgs).collect(joining(",")));

        Arrays.stream(pArgs)
              .filter(s -> s.contains("-Dmongodb_host="))
              .map(s -> s.split("="))
              .flatMap(Arrays::stream)
              .filter(s -> s.contains(":"))
              .forEach(s -> System.setProperty("mongodb_host", s));

        logger.info("pArgs passed to ToolRunner: " +
                Arrays.stream(pArgs)
                      .filter(s -> !s.contains("-Dmongodb_host=")).collect(joining(",")));
        System.exit(ToolRunner.run(new LastHourUniqueConfig(), Arrays.stream(pArgs)
                .filter(s -> !s.contains("-Dmongodb_host=")).toArray(String[]::new)));
    }
}
