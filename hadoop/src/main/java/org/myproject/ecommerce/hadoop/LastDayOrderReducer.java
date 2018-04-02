package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.MongoUpdateWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class LastDayOrderReducer extends Reducer<ItemPairWritable, IntWritable, NullWritable, MongoUpdateWritable>
                implements org.apache.hadoop.mapred.Reducer<ItemPairWritable, IntWritable,
                                NullWritable, MongoUpdateWritable> {

    private MongoUpdateWritable reduceResult;
    private static final Logger logger = LoggerFactory.getLogger(LastDayOrderReducer.class);

    public LastDayOrderReducer() {
        super();
        reduceResult = new MongoUpdateWritable();
    }

    @Override
    public void reduce(ItemPairWritable pKey, Iterable<IntWritable> pValues, Context pContext)
            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        BasicBSONObject keyObject = new BasicBSONObject();
        keyObject.put("a", pKey.getA());
        keyObject.put("b", pKey.getB());
        BasicBSONObject query = new BasicBSONObject("_id", keyObject);
        int count = StreamSupport
                .stream(pValues.spliterator(), false)
                .mapToInt(v -> v.get())
                .sum();
        BasicBSONObject update = new BasicBSONObject("$set", new BasicBSONObject("value", count));
        reduceResult.setQuery(query);
        reduceResult.setModifiers(update);
        pContext.write(null, reduceResult);
    }

    @Override
    public void reduce(ItemPairWritable key, Iterator<IntWritable> values, OutputCollector<NullWritable,
                                MongoUpdateWritable> output, Reporter reporter) throws IOException {
        logger.info("Reduce OutputCollector with Context class");
        BasicBSONObject keyObject = new BasicBSONObject();
        keyObject.put("a", key.getA());
        keyObject.put("b", key.getB());
        BasicBSONObject query = new BasicBSONObject("_id", keyObject);
        int count = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                .mapToInt(v -> v.get())
                .sum();
        BasicBSONObject update = new BasicBSONObject("$set", new BasicBSONObject("value", count));
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
