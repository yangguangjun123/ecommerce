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
 * PairMapper/PairReducer crunch lastDayOrders collection to produce pair collection.
 */
public class PairReducer extends Reducer<Text, IntWritable, BSONWritable, BSONWritable>
                implements org.apache.hadoop.mapred.Reducer<Text, IntWritable,
        BSONWritable, BSONWritable> {
    private BSONWritable keyBSONWritable;
    private BSONWritable reduceResult;
    private static final Logger logger = LoggerFactory.getLogger(ItemPairReducer.class);

    public PairReducer() {
        super();
        keyBSONWritable = new BSONWritable();
        reduceResult = new BSONWritable();
    }

    @Override
    public void reduce(Text pKey, Iterable<IntWritable> pValues, Context pContext)
            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        int total = StreamSupport.stream(pValues.spliterator(), false)
                               .mapToInt(i -> i.get()).sum();
        logger.info("reduce pKey: " + pKey.toString());
        logger.info("reduce value: " + total);
        BasicBSONObject doc = new BasicBSONObject().append("value", total);
        String[] pairs = pKey.toString().split(" ");
        keyBSONWritable.setDoc(new BasicBSONObject("_id", new BasicBSONObject()
                                    .append("a", Integer.parseInt(pairs[0]))
                                    .append("b", Integer.parseInt(pairs[1]))));
        reduceResult.setDoc(doc);
        pContext.write(keyBSONWritable, reduceResult);
    }

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<BSONWritable,
            BSONWritable> output, Reporter reporter) throws IOException {
        logger.info("Reduce processing with OutputCollector class");
        int total = StreamSupport.stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                .mapToInt(i -> i.get()).sum();
        logger.info("reduce pKey: " + key.toString());
        logger.info("reduce value: " + total);
        BasicBSONObject doc = new BasicBSONObject().append("value", total);
        reduceResult.setDoc(doc);
        String[] pairs = key.toString().split(" ");
        keyBSONWritable.setDoc(new BasicBSONObject("_id", new BasicBSONObject()
                                    .append("a", Integer.parseInt(pairs[0]))
                                    .append("b", Integer.parseInt(pairs[1]))));
        output.collect(keyBSONWritable, reduceResult);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }

}
