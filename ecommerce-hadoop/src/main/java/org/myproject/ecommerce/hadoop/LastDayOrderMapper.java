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
 * LastDayOrderMapper/LastDayOrderReducer classes crunch lastDayOrders to product the pairs
 * collection(compute the number of occurrences of each item pair).
 */
public class LastDayOrderMapper extends Mapper<Object, BSONObject, ItemPairWritable, IntWritable>
            implements org.apache.hadoop.mapred.Mapper<Object, BSONWritable, ItemPairWritable, IntWritable> {
    private final ItemPairWritable keyItemPairWritable;
    private final IntWritable valueIntWritable;
    private static final Logger logger = LoggerFactory.getLogger(LastDayOrderMapper.class);

    public LastDayOrderMapper() {
        keyItemPairWritable = new ItemPairWritable();
        valueIntWritable = new IntWritable(1);
    }

    @Override
    public void map(Object key, BSONObject value, final Context context) {
        logger.info("Map processing with Context class");
        List<String> items = (List<String>) ((BSONObject) value.get("data")).get("items");
        IntStream.rangeClosed(0, items.size() - 1).boxed()
                 .flatMap(a -> IntStream.rangeClosed(a + 1, items.size() - 1)
                                    .mapToObj(b -> new int[] {Integer.parseInt(items.get(a)),
                                            Integer.parseInt(items.get(b))}))
                 .forEach(c -> {
                     try {
                         keyItemPairWritable.setPair(c[0], c[1]);
                         context.write(keyItemPairWritable, valueIntWritable);
                     } catch (IOException | InterruptedException e) {
                         logger.info("key: " + key.toString());
                         logger.error("value: " + value);
                         e.printStackTrace();
                     }
                 });
    }

    @Override
    public void map(Object key, BSONWritable bsonWritable, OutputCollector<ItemPairWritable, IntWritable> output,
                    Reporter reporter) throws IOException {
        logger.info("Map processing with OutputCollector class");
        BSONObject doc = bsonWritable.getDoc();
        List<String> items = (List<String>) ((BSONObject) doc.get("data")).get("items");
        IntStream.rangeClosed(0, items.size() -1).boxed()
                .flatMap(a -> IntStream.rangeClosed(a + 1, items.size() - 1)
                        .mapToObj(b -> new int[] {Integer.parseInt(items.get(a)),
                                Integer.parseInt(items.get(b))}))
                .forEach(c -> {
                    try {
                        keyItemPairWritable.setPair(c[0], c[1]);
                        output.collect(keyItemPairWritable, valueIntWritable);
                    } catch (IOException e) {
                        logger.info("key: " + key.toString());
                        logger.error("value: " + doc);
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
