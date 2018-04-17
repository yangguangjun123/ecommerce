package org.myproject.ecommerce.hadoop.utils;

import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.util.MongoClientURIBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.hadoop.BaseHadoopTest;
import org.myproject.ecommerce.hvdfclient.HVDFClientPropertyService;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RunWith(BlockJUnit4ClassRunner.class)
public class LastDayOrdersStandaloneIT extends BaseHadoopTest {
    private MongoDBService mongoDBService;
    private HVDFClientPropertyService hvdfClientPropertyService;
    private String mongoHost;
    private int mongoPort;

    //    private final MongoClientURI inputUri;
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
    public void shouldPerformLastDayOrdersMapReduceJob() {
        // given


        // when


        // verify

    }
}
