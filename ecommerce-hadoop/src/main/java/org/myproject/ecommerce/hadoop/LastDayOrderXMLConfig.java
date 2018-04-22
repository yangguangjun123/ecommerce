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
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.util.ToolRunner;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class LastDayOrderXMLConfig extends MongoTool {
    private static final Log logger = LogFactory.getLog(LastDayOrderXMLConfig.class);

    public LastDayOrderXMLConfig() {
        this(new Configuration());
    }

    public LastDayOrderXMLConfig(final Configuration conf) {
        setConf(conf);

        if (MongoTool.isMapRedV1()) {
            MapredMongoConfigUtil.setInputFormat(conf, com.mongodb.hadoop.mapred.MongoInputFormat.class);
            MapredMongoConfigUtil.setOutputFormat(conf, com.mongodb.hadoop.mapred.MongoOutputFormat.class);
        } else {
            MongoConfigUtil.setInputFormat(conf, MongoInputFormat.class);
            MongoConfigUtil.setOutputFormat(conf, MongoOutputFormat.class);
        }
        MongoConfigUtil.setMapper(conf, LastDayOrderMapper.class);
        MongoConfigUtil.setMapperOutputKey(conf, Text.class);
        MongoConfigUtil.setMapperOutputValue(conf, IntWritable.class);


        MongoConfigUtil.setReducer(conf, LastDayOrderReducer.class);
        MongoConfigUtil.setOutputKey(conf, Text.class);
        MongoConfigUtil.setOutputValue(conf, BSONWritable.class);

        MongoConfigUtil.setSortComparator(conf, WritableComparator.class);
    }

    public static void main(final String[] pArgs) throws Exception {
        logger.info("pArgs: " + Arrays.stream(pArgs).collect(joining(",")));
        System.exit(ToolRunner.run(new LastDayOrderXMLConfig(), Arrays.stream(pArgs)
                .map(s -> s.replace("'", "")).toArray(String[]::new)));
    }

}
