package org.myproject.ecommerce.hadoop;

//import com.mongodb.MongoClientURI;

import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MapredMongoConfigUtil;
import com.mongodb.hadoop.util.MongoConfigUtil;
import com.mongodb.hadoop.util.MongoTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class MostPopularPairXMLConfig extends MongoTool {
    private static final Log logger = LogFactory.getLog(MostPopularPairXMLConfig.class);

    public MostPopularPairXMLConfig() {
        this(new Configuration());
    }

    public MostPopularPairXMLConfig(final Configuration conf) {
        setConf(conf);

        if (MongoTool.isMapRedV1()) {
            MapredMongoConfigUtil.setInputFormat(conf, com.mongodb.hadoop.mapred.MongoInputFormat.class);
            MapredMongoConfigUtil.setOutputFormat(conf, com.mongodb.hadoop.mapred.MongoOutputFormat.class);
        } else {
            MongoConfigUtil.setInputFormat(conf, MongoInputFormat.class);
            MongoConfigUtil.setOutputFormat(conf, MongoOutputFormat.class);
        }
        MongoConfigUtil.setMapper(conf, MostPopularPairMapper.class);
        MongoConfigUtil.setMapperOutputKey(conf, Text.class);
        MongoConfigUtil.setMapperOutputValue(conf, Text.class);

        MongoConfigUtil.setReducer(conf, MostPopularPairReducer.class);
        MongoConfigUtil.setOutputKey(conf, BSONWritable.class);
        MongoConfigUtil.setOutputValue(conf, BSONWritable.class);

//        MongoConfigUtil.setInputMongosHosts(
//                conf, Arrays.asList("mongo.sh01.dc1:27018", "mongo.sh02.dc2:27018"));
//        MongoConfigUtil.setInputURI(
//                conf, new MongoClientURI("mongodb://mongo.dc1:27018,mongo.dc2:27018/hadoop.test"));

//        MongoConfigUtil.setInputMongosHosts(
//                conf, Arrays.asList("192.168.116.1:27017"));
//        MongoConfigUtil.setInputURI(
//                conf, new MongoClientURI("mongodb://192.168.116.1:27017/ecommerce.lastDayOrders"));

        MongoConfigUtil.setSortComparator(conf, WritableComparator.class);
    }

    public static void main(final String[] pArgs) throws Exception {
//        logger.info("pArgs: " + Arrays.stream(pArgs).collect(joining(",")));
//        System.exit(ToolRunner.run(new MostPopularPairXMLConfig(), pArgs));
        logger.info("pArgs: " + Arrays.stream(pArgs).collect(joining(",")));
        System.exit(ToolRunner.run(new MostPopularPairXMLConfig(), Arrays.stream(pArgs)
                .map(s -> s.replace("'", "")).toArray(String[]::new)));
    }

}
