package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.BSONWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

/**
 * ItemPairMapper/ItemPairReducer crunch lastDayOrders collection to compute the number of
 * occurrences of each item pair and store it in pair collection.
 */
public class ItemPairMapper extends Mapper<Object, BSONObject, ItemPair, IntWritable>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONWritable, ItemPair, IntWritable> {
    private final ItemPair itemPair;
    private final IntWritable valueIntWritable;
    private static final Logger logger = LoggerFactory.getLogger(ItemPairMapper.class);

    public ItemPairMapper() {
        itemPair = new ItemPair();
        valueIntWritable = new IntWritable(1);
    }

    @Override
    public void map(Object key, BSONObject value, final Context context)
            throws IOException, InterruptedException {
        logger.info("Map processing with Context class");
        List<Integer> items = ((List<Integer>) value.get("items"));
        IntStream.rangeClosed(0, items.size() - 1).boxed()
                 .flatMap(a -> IntStream.rangeClosed(a, items.size() - 2)
                                        .mapToObj(b -> new int[] { a, b }))
                 .forEach(pair -> {
                     itemPair.setA(pair[0]);
                     itemPair.setB(pair[1]);
                     try {
                         context.write(itemPair, valueIntWritable);
                     } catch (IOException | InterruptedException e) {
                         logger.info(e.toString());
                         e.printStackTrace();
                     }
                 });
    }

    @Override
    public void map(Object key, BSONWritable bsonWritable, OutputCollector<ItemPair, IntWritable> output,
                    Reporter reporter) throws IOException {
        logger.info("Map processing with OutputCollector class");
        BSONObject doc = bsonWritable.getDoc();
        List<Integer> items = ((List<Integer>) doc.get("items"));
        IntStream.rangeClosed(0, items.size() - 1).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, items.size() - 2)
                        .mapToObj(b -> new int[] { a, b }))
                .forEach(pair -> {
                    itemPair.setA(pair[0]);
                    itemPair.setB(pair[1]);
                    try {
                        output.collect(itemPair, valueIntWritable);
                    } catch (IOException e) {
                        logger.info(e.toString());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
