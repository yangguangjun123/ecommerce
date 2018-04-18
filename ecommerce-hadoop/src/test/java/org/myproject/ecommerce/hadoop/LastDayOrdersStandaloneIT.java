package org.myproject.ecommerce.hadoop;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.mapred.output.MongoOutputCommitter;
import com.mongodb.hadoop.splitter.MultiCollectionSplitBuilder;
import com.mongodb.hadoop.splitter.MultiMongoCollectionSplitter;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.hadoop.utils.MapReduceJob;
import org.myproject.ecommerce.hvdfclient.HVDFClientPropertyService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.LongStream;

import static com.mongodb.hadoop.splitter.MultiMongoCollectionSplitter.MULTI_COLLECTION_CONF_KEY;
import static com.mongodb.hadoop.util.MongoConfigUtil.MONGO_SPLITTER_CLASS;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertTrue;

public class LastDayOrdersStandaloneIT extends BaseHadoopTest {
    private MongoDBService mongoDBService;
    private HVDFClientPropertyService hvdfClientPropertyService;
    private String mongoHost;
    private int mongoPort;

    private final MongoClientURI outputUri;
    private final File ECOMMERC_HADOOP_HOME;
    private final File JOBJAR_PATH;

    private static final Log logger = LogFactory.getLog(LastDayOrdersStandaloneIT.class);

    public LastDayOrdersStandaloneIT() {
        ECOMMERC_HADOOP_HOME = new File(PROJECT_HOME, "ecommerce-hadoop");
        logger.info("ECOMMERC_HADOOP_HOME: " + ECOMMERC_HADOOP_HOME);
        JOBJAR_PATH = findProjectJar(ECOMMERC_HADOOP_HOME);
        logger.info("JOBJAR_PATH: " + JOBJAR_PATH);

        logger.info("mongodb_host: " + System.getProperty("mongodb_host"));
        outputUri = authCheck(System.getProperty("mongodb_host") != null ?
                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
                        .collection("ecommerce", "lastDayOrders") :
                new MongoClientURIBuilder()
                        .collection("ecommerce", "lastDayOrders"))
                .build();

        logger.info("outputUri: " + outputUri);

        initialise();
    }

    private void initialise() {
        mongoHost = Optional.ofNullable(System.getProperty("mongodb_host"))
                .map(s -> s.split(":"))
                .map(Arrays::stream)
                .flatMap(s -> s.findFirst())
                .orElse("localhost");
        mongoPort = Optional.ofNullable(System.getProperty("mongodb_host"))
                .map(s -> s.split(":"))
                .map(Arrays::stream)
                .flatMap(s -> s.skip(1).findFirst())
                .map(s -> Integer.parseInt(s))
                .orElse(27017);
        logger.info("mongo host: " + mongoHost);
        logger.info("mongo port: " + mongoPort);

        mongoDBService = new MongoDBService(Collections.emptyList(), mongoHost, mongoPort);
        hvdfClientPropertyService = new HVDFClientPropertyService(mongoDBService);
        hvdfClientPropertyService.initialise();
    }

    @Before
    public void checkConfiguration() {
        Assume.assumeFalse(System.getProperty("os.name").toLowerCase().startsWith("win"));
//        assumeFalse(isSharded(inputUri));
    }

    @Test
    public void shouldPerformLastDayOrderMapAndReduceJob() {
        // when
        MapReduceJob lastDayOrderJob =
                new MapReduceJob(LastDayOrderXMLConfig.class.getName())
                        .jar(JOBJAR_PATH)
                        .param("mongo.input.notimeout", "true")
                        .param(MONGO_SPLITTER_CLASS, MultiMongoCollectionSplitter.class.getName())
                        .param(MULTI_COLLECTION_CONF_KEY, collectionSettings())
                        .outputUri(outputUri);
        if (isHadoopV1()) {
            logger.info("isHadoopV1: " + isHadoopV1());
            lastDayOrderJob.outputCommitter(MongoOutputCommitter.class);
        }
        logger.info("lastDayOrderJob: " + lastDayOrderJob.toString());
        logger.info("isRunTestInVm: " + isRunTestInVm());
        logger.info("jar: " + lastDayOrderJob.getJarPath().getAbsolutePath());
        logger.info("inputUri: " + lastDayOrderJob.getInputUris().stream().collect(joining(",")));
        logger.info("outputUri: " + lastDayOrderJob.getOutputUri());
        logger.info("mongo.input.multi_uri.json: " + collectionSettings());
        logger.info("params: " + lastDayOrderJob.getParams());
        mongoDBService.deleteAll("ecommerce", "lastDayOrders");

        // given
        lastDayOrderJob.execute(isRunTestInVm());

        // verify
        assertTrue(mongoDBService.count("ecommerce", "lastDayOrders") > 0);
    }

    private String collectionSettings() {
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

        return builder.toJSON();
    }

}
