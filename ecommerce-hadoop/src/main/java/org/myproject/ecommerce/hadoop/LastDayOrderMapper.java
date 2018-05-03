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
 * LastDayOrderMapper/LastDayOrderReducer classes crunch lastDayOrders to product the pairs
 * collection(compute the number of occurrences of each item pair).
 */
public class LastDayOrderMapper extends Mapper<Object, BSONObject, Text, IntWritable>
            implements org.apache.hadoop.mapred.Mapper<Object, BSONWritable, Text, IntWritable> {
    private final Text keyText;
    private final IntWritable valueIntWritable;
    private static final Logger logger = LoggerFactory.getLogger(LastDayOrderMapper.class);

    public LastDayOrderMapper() {
        keyText = new Text();
        valueIntWritable = new IntWritable(1);
    }

    @Override
    public void map(Object key, BSONObject value, final Context context) throws IOException, InterruptedException {
        logger.info("Map processing with Context class");
        keyText.set((String) ((BSONObject) value.get("data")).get("userId"));
        valueIntWritable.set(Integer.parseInt((String) ((BSONObject) value.get("data")).get("itemId")));
        context.write(keyText, valueIntWritable);
    }

    @Override
    public void map(Object key, BSONWritable value, OutputCollector<Text, IntWritable> output,
                    Reporter reporter) throws IOException {
        logger.info("Map processing with OutputCollector class(Hadoop V1)");
        BSONObject pValue = value.getDoc();
        keyText.set((String) ((BSONObject) pValue.get("data")).get("userId"));
        valueIntWritable.set(Integer.parseInt((String) ((BSONObject) pValue.get("data")).get("itemId")));
        output.collect(keyText, valueIntWritable);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
