package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MapredMongoConfigUtil;
import com.mongodb.hadoop.util.MongoConfigUtil;
import com.mongodb.hadoop.util.MongoTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class LastHourUniqueXMLConfig extends MongoTool {
    private static final Log logger = LogFactory.getLog(LastHourUniqueXMLConfig.class);

    public LastHourUniqueXMLConfig() {
        this(new Configuration());
    }

    public LastHourUniqueXMLConfig(final Configuration conf) {
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

    public static void main(final String[] pArgs) throws Exception {
        logger.info("pArgs: " + Arrays.stream(pArgs).collect(joining(",")));
        System.exit(ToolRunner.run(new LastHourUniqueXMLConfig(), Arrays.stream(pArgs)
                .map(s -> s.replace("'", "")).toArray(String[]::new)));
    }
}
