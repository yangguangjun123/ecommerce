package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.BSONWritable;
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
 * In phase one, PairMapper/PairReducer crunch pairs collection to product most_popular_pairs collection.
 * in phase two, ActivityWeightMapper/ActivityWeightReducer crunch activity collections(timed series) to
 * extract the weight values for product items.
 */
public class ActivityWeightMapper extends Mapper<Object, BSONObject, Text, IntWritable>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONWritable, Text, IntWritable> {

    private final Text keyText;
    private final IntWritable valueText;
    private static final Logger logger = LoggerFactory.getLogger(ActivityWeightMapper.class);

    public ActivityWeightMapper() {
        super();
        keyText = new Text();
        valueText = new IntWritable();
    }

    @Override
    public void map(Object key, BSONObject value, final Context context)
            throws IOException, InterruptedException {
        logger.info("Map processing with Context class");
        String keyOut = (String) ((BSONObject) value.get("data")).get("itemId");
        keyText.set(keyOut);
        valueText.set((Integer) ((BSONObject) value.get("data")).get("weight"));
        context.write(keyText, valueText);
    }

    @Override
    public void map(Object key, BSONWritable bsonWritable, OutputCollector<Text, IntWritable> output,
                    Reporter reporter) throws IOException {
        logger.info("Map processing with OutputCollector class");
        BSONObject doc = bsonWritable.getDoc();
        String keyOut = (String) ((BSONObject) doc.get("data")).get("itemId");
        keyText.set(keyOut);
        valueText.set((Integer) ((BSONObject) doc.get("data")).get("weight"));
        output.collect(keyText, valueText);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
