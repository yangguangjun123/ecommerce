package org.myproject.ecommerce.hadoop;

import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.mapred.output.MongoOutputCommitter;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.hadoop.utils.MapReduceJob;

import java.io.File;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class LastHourUniqueStandaloneIT extends BaseHadoopTest {
    private MongoDBService mongoDBService = new MongoDBService(Collections.emptyList());

    //    private final MongoClientURI inputUri;
    private final MongoClientURI outputUri;
    private final File ECOMMERC_HADOOP_HOME;
    private final File JOBJAR_PATH;

    private static final Log logger = LogFactory.getLog(LastHourUniqueStandaloneIT.class);

    public LastHourUniqueStandaloneIT() {
        ECOMMERC_HADOOP_HOME = new File(PROJECT_HOME, "hadoop");
        logger.info("ECOMMERC_HADOOP_HOME: " + ECOMMERC_HADOOP_HOME);
        JOBJAR_PATH = findProjectJar(ECOMMERC_HADOOP_HOME);
        logger.info("JOBJAR_PATH: " + JOBJAR_PATH);

//        inputUri = authCheck(System.getProperty("mongodb_host") != null ?
//                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
//                        .collection("ecommerce", "yield_historical.in") :
//                new MongoClientURIBuilder()
//                        .collection("ecommerce", "yield_historical.in"))
//                .build();
        logger.info("mongodb_host: " + System.getProperty("mongodb_host"));
        outputUri = authCheck(System.getProperty("mongodb_host") != null ?
                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
                        .collection("ecommerce", "lastHourUniques") :
                new MongoClientURIBuilder()
                        .collection("ecommerce", "lastHourUniques"))
                .build();
        logger.info("outputUri: " + outputUri);
    }

    @Before
    public void checkConfiguration() {
//        assumeFalse(isSharded(inputUri));
    }

    @Test
    public void shouldPerformLastHourUniqueMapReduceJob() {
        logger.info("testing shouldPerformLastHourUniqueMapReduceJob");

        // when

        // given
        MapReduceJob lastHourUniqueJob =
                new MapReduceJob(LastHourUniqueConfig.class.getName())
                        .jar(JOBJAR_PATH)
                        .param("mongo.input.notimeout", "true")
//                        .inputUris(getInputUri())
                        .outputUri(outputUri);
        if (isHadoopV1()) {
            logger.info("isHadoopV1: " + isHadoopV1());
            lastHourUniqueJob.outputCommitter(MongoOutputCommitter.class);
        }
        logger.info("lastHourUniqueJob: " + lastHourUniqueJob.toString());
        logger.info("isRunTestInVm: " + isRunTestInVm());
        lastHourUniqueJob.execute(isRunTestInVm());

        // verify
        assertTrue(mongoDBService.count("ecommerce", "lastHourUniques") > 0);



//        compareResults(getClient(getInputUri()).getDB(getOutputUri().getDatabase()).getCollection(getOutputUri().getCollection()),
//                getReference());
    }
}
