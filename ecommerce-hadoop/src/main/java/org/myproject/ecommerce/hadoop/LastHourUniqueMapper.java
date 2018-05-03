package org.myproject.ecommerce.hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * LastHourUniqueMapper/LastHourUniqueReducer classes crunch activity collections to produce
 * the number activities for unique users
 */
public class LastHourUniqueMapper extends Mapper<Object, BSONObject, Text, IntWritable>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONObject, Text, IntWritable> {
    private final Text keyText;
    private final IntWritable valueIntWritable;
    private static final Logger logger = LoggerFactory.getLogger(LastHourUniqueMapper.class);

    public LastHourUniqueMapper() {
        keyText = new Text();
        valueIntWritable = new IntWritable(1);
    }

    @Override
    public void map(Object key, BSONObject value, final Context context) throws IOException, InterruptedException {
        logger.info("Map processing with Context class");
        keyText.set((String) ((BSONObject) value.get("data")).get("userId"));
        context.write(keyText, valueIntWritable);
    }

    @Override
    public void map(Object key, BSONObject value, OutputCollector<Text, IntWritable> output,
                    Reporter reporter) throws IOException {
        logger.info("Map processing with OutputCollector class(Hadoop V1)");
        keyText.set((String) ((BSONObject) value.get("data")).get("userId"));
        output.collect(keyText, valueIntWritable);
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(JobConf job) {
    }
}
