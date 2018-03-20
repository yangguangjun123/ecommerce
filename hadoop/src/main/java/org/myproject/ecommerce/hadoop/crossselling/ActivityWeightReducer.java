package org.myproject.ecommerce.hadoop.crossselling;

import com.mongodb.hadoop.io.MongoUpdateWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

/**
 * In phase one, PairMapper/PairReducer crunch pairs collection to product most_popular_pairs collection.
 * in phase two, ActivityWeightMapper/ActivityWeightReducer crunch activity collections(timed series) to
 * extract the weight values for product items.
 */
public class ActivityWeightReducer extends Reducer<Text, IntWritable, NullWritable, MongoUpdateWritable>
            implements org.apache.hadoop.mapred.Reducer<Text, IntWritable, NullWritable, MongoUpdateWritable> {
    private MongoUpdateWritable reduceResult;
    private static final Logger logger = LoggerFactory.getLogger(ActivityWeightReducer.class);

    public ActivityWeightReducer() {
        super();
        reduceResult = new MongoUpdateWritable();
    }

    @Override
    public void reduce(Text pKey, Iterable<IntWritable> pValues, Context pContext)
            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        String itemId = pKey.toString();
        int weight = pValues.iterator().next().get();
        BasicBSONObject weightQuery = new BasicBSONObject("itemId", itemId);
        BasicBSONObject arrayEleQuery = new BasicBSONObject("$elemMatch", weightQuery);
        BasicBSONObject query = new BasicBSONObject("value.recom", arrayEleQuery);
        BasicBSONObject update = new BasicBSONObject("$set", new BasicBSONObject("value.recom.$.count", weight));
        reduceResult.setQuery(query);
        reduceResult.setModifiers(update);
        pContext.write(null, reduceResult);
    }

    @Override
    public void reduce(Text key, Iterator<IntWritable> values,
                            OutputCollector<NullWritable, MongoUpdateWritable> output,
                                Reporter reporter) throws IOException {
        logger.info("Reduce OutputCollector with Context class");
        String itemId = key.toString();
        int weight = values.next().get();
        BasicBSONObject weightQuery = new BasicBSONObject("itemId", itemId);
        BasicBSONObject arrayEleQuery = new BasicBSONObject("$elemMatch", weightQuery);
        BasicBSONObject query = new BasicBSONObject("value.recom", arrayEleQuery);
        BasicBSONObject update = new BasicBSONObject("$set", new BasicBSONObject("value.recom.$.count", weight));
        reduceResult.setQuery(query);
        reduceResult.setModifiers(update);
        output.collect(null, reduceResult);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
