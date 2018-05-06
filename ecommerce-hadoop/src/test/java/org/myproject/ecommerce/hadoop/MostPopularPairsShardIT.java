package org.myproject.ecommerce.hadoop;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.mapred.output.MongoOutputCommitter;
import com.mongodb.hadoop.splitter.ShardMongoSplitter;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.hadoop.utils.MapReduceJob;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mongodb.hadoop.util.MongoConfigUtil.INPUT_MONGOS_HOSTS;
import static com.mongodb.hadoop.util.MongoConfigUtil.MONGO_SPLITTER_CLASS;
import static com.mongodb.hadoop.util.MongoConfigUtil.SPLITS_USE_SHARDS;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class MostPopularPairsShardIT extends BaseHadoopTest {
    private MongoDBService mongoDBService;
    private String mongoHost;
    private int mongoPort;

    private final MongoClientURI inputUri;
    private final MongoClientURI outputUri;
    private final File ECOMMERC_HADOOP_HOME;
    private final File JOBJAR_PATH;

    private static final Log logger = LogFactory.getLog(MostPopularPairsShardIT.class);

    public MostPopularPairsShardIT() {
        ECOMMERC_HADOOP_HOME = new File(PROJECT_HOME, "ecommerce-hadoop");
        logger.info("ECOMMERC_HADOOP_HOME: " + ECOMMERC_HADOOP_HOME);
        JOBJAR_PATH = findProjectJar(ECOMMERC_HADOOP_HOME);
        logger.info("JOBJAR_PATH: " + JOBJAR_PATH);

        logger.info("mongodb_host: " + System.getProperty("mongodb_host"));

        inputUri = authCheck(System.getProperty("mongodb_host") != null ?
                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
                        .collection("ecommerce", "most_popular_pairs") :
                new MongoClientURIBuilder()
                        .collection("ecommerce", "most_popular_pairs"))
                .build();
        outputUri = authCheck(System.getProperty("mongodb_host") != null ?
                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
                        .collection("ecommerce", "most_popular_pairs") :
                new MongoClientURIBuilder()
                        .collection("ecommerce", "most_popular_pairs"))
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
    }

    @Before
    public void setUp() {
        checkConfiguration();
        mongoDBService.dropCollection("ecommerce", "most_popular_pairs");
        Bson cmd = new BasicDBObject("shardCollection", "ecommerce.most_popular_pairs").
                append("key", new BasicDBObject("_id", "hashed"));
        mongoDBService.runAdminCommand(cmd);
    }

    public void checkConfiguration() {
        Assume.assumeFalse(System.getProperty("os.name").toLowerCase().startsWith("win"));
//        assumeFalse(isSharded(inputUri));
    }

    @Test
    public void shouldPerformItemPairMapReduceJob() {
        // when
        MapReduceJob pairJob =
                new MapReduceJob(ItemPairXMLConfig.class.getName())
                        .jar(JOBJAR_PATH)
                        .param("mongo.input.notimeout", "true")
                        .param(INPUT_MONGOS_HOSTS, "mongodb://" + System.getProperty("mongodb_host"))
                        .param(MONGO_SPLITTER_CLASS, ShardMongoSplitter.class.getName())
                        .param(SPLITS_USE_SHARDS, "true")
                        .inputUris(inputUri)
                        .outputUri(outputUri);
        if (isHadoopV1()) {
            logger.info("isHadoopV1: " + isHadoopV1());
            pairJob.outputCommitter(MongoOutputCommitter.class);
        }
        logger.info("mostPopularPairJob: " + pairJob.toString());
        logger.info("isRunTestInVm: " + isRunTestInVm());
        logger.info("jar: " + pairJob.getJarPath().getAbsolutePath());
        logger.info("inputUri: " + pairJob.getInputUris().stream().collect(joining(",")));
        logger.info("outputUri: " + pairJob.getOutputUri());
        logger.info("params: " + pairJob.getParams());
        mongoDBService.deleteAll("ecommerce", "most_popular_pairs");

        // given
        pairJob.execute(isRunTestInVm());

        // verify
        List<Document> documents = mongoDBService.readAll("ecommerce",
                "most_popular_pairs", Document.class);
        assertTrue(documents.size() > 0);
        documents.stream()
                .map(d -> (List<Document>) d.get("recom"))
                .flatMap(List::stream)
                .forEach(d -> {
                    assertTrue(d.getInteger("itemId") > 0);
                    assertTrue(d.getInteger("count") > 0);
                });
    }

}