package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.BSONWritable;
import org.apache.hadoop.io.IntWritable;
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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

/**
 * ItemPairMapper/ItemPairReducer crunch lastDayOrders collection to produce pair collection.
 */
public class ItemPairReducer extends Reducer<Text, IntWritable, Text, BSONWritable>
                implements org.apache.hadoop.mapred.Reducer<Text, IntWritable,
        Text, BSONWritable> {
    private BSONWritable reduceResult;
    private static final Logger logger = LoggerFactory.getLogger(ItemPairReducer.class);

    public ItemPairReducer() {
        super();
        reduceResult = new BSONWritable();
    }

    @Override
    public void reduce(Text pKey, Iterable<IntWritable> pValues, Context pContext)
            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        int total = StreamSupport.stream(pValues.spliterator(), false)
                               .mapToInt(i -> i.get()).sum();
        BasicBSONObject doc = new BasicBSONObject();
        doc.put("value", total);
        reduceResult.setDoc(doc);
        reduceResult.setDoc(doc);
        pContext.write(pKey, reduceResult);
    }

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text,
            BSONWritable> output, Reporter reporter) throws IOException {
        logger.info("Reduce processing with OutputCollector class");
        int total = StreamSupport.stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                .mapToInt(i -> i.get()).sum();
        BasicBSONObject doc = new BasicBSONObject();
        doc.put("value", total);
        reduceResult.setDoc(doc);
        reduceResult.setDoc(doc);
        output.collect(key, reduceResult);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
