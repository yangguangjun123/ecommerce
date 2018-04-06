package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.BSONWritable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BasicBSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

/**
 * LastHourUniqueMapper/LastHourUniqueReducer classes crunch activity collections to produce
 * the number activities for unique users
 */
public class LastHourUniqueReducer extends Reducer<Text, IntWritable, Text, BSONWritable>
    implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, Text, BSONWritable> {
    private BSONWritable reduceResult;
    private static final Log LOG = LogFactory.getLog(LastHourUniqueReducer.class);

    public LastHourUniqueReducer() {
        super();
        reduceResult = new BSONWritable();
    }

    @Override
    public void reduce(final Text pKey, final Iterable<IntWritable> pValues, final Context pContext)
            throws IOException, InterruptedException {
        LOG.info("reduce with Context class");
        int count = StreamSupport
                .stream(pValues.spliterator(), false)
                .mapToInt(v -> v.get())
                .sum();

        LOG.debug("Count of user activities for " + pKey.toString() + " is " + count);

        BasicBSONObject output = new BasicBSONObject();
        output.put("userId", pKey.toString());
        output.put("count", count);
        reduceResult.setDoc(output);
        pContext.write(pKey, reduceResult);
    }

    @Override
    public void reduce(Text key, Iterator<IntWritable> values,
                       OutputCollector<Text, BSONWritable> output, Reporter reporter) throws IOException {
        LOG.info("reduce with OutputCollector class");
        int count = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                .mapToInt(v -> v.get())
                .sum();

        LOG.debug("Count of user activities for " + key.toString() + " is " + count);

        BasicBSONObject result = new BasicBSONObject();
        result.put("userId", key.toString());
        result.put("count", count);
        reduceResult.setDoc(result);
        output.collect(key, reduceResult);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
