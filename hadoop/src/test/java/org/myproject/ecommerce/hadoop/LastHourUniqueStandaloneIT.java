package org.myproject.ecommerce.hadoop;

import com.mongodb.DBObject;
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
import org.myproject.ecommerce.hvdfclient.HVDFClientPropertyService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

@RunWith(BlockJUnit4ClassRunner.class)
public class LastHourUniqueStandaloneIT extends BaseHadoopTest {
    MongoDBService mongoDBService = new MongoDBService(Collections.emptyList());

    //    private final MongoClientURI inputUri;
    private final MongoClientURI outputUri;
    private static final Log LOG = LogFactory.getLog(LastHourUniqueStandaloneIT.class);

    public static final File ECOMMERC_HADOOP_HOME;
    protected static final File JOBJAR_PATH;

    static {
        ECOMMERC_HADOOP_HOME = new File(PROJECT_HOME, "hadoop");
        JOBJAR_PATH = findProjectJar(ECOMMERC_HADOOP_HOME);
    }

    public LastHourUniqueStandaloneIT() {
//        inputUri = authCheck(System.getProperty("mongodb_host") != null ?
//                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
//                        .collection("ecommerce", "yield_historical.in") :
//                new MongoClientURIBuilder()
//                        .collection("ecommerce", "yield_historical.in"))
//                .build();
        outputUri = authCheck(System.getProperty("mongodb_host") != null ?
                new MongoClientURIBuilder().host(System.getProperty("mongodb_host"))
                        .collection("ecommerce", "lastHourUniques") :
                new MongoClientURIBuilder()
                        .collection("ecommerce", "lastHourUniques"))
                .build();
    }

    @Before
    public void checkConfiguration() {
//        assumeFalse(isSharded(inputUri));
    }

    @Test
    public void shouldPerformLastHourUniqueMapReduceJob() {
        LOG.info("testing shouldPerformLastHourUniqueMapReduceJob");

        // when

        // given
        MapReduceJob treasuryJob =
                new MapReduceJob(LastHourUniqueConfig.class.getName())
                        .jar(JOBJAR_PATH)
                        .param("mongo.input.notimeout", "true")
//                        .inputUris(getInputUri())
                        .outputUri(outputUri);
        if (isHadoopV1()) {
            treasuryJob.outputCommitter(MongoOutputCommitter.class);
        }
        treasuryJob.execute(isRunTestInVm());


        // verify
        assertTrue(mongoDBService.count("ecommerce", "lastHourUniques") > 0);



//        compareResults(getClient(getInputUri()).getDB(getOutputUri().getDatabase()).getCollection(getOutputUri().getCollection()),
//                getReference());
    }
}
