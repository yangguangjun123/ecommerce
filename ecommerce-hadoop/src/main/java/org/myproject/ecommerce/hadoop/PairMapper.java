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
import java.util.List;
import java.util.stream.IntStream;

/**
 * PairMapper/PairReducer crunch lastDayOrders collection to compute the number of
 * occurrences of each item pair and store it in pair collection.
 */
public class PairMapper extends Mapper<Object, BSONObject, Text, IntWritable>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONObject, Text, IntWritable> {
    private final Text keyText;
    private final IntWritable valueIntWritable;
    private static final Logger logger = LoggerFactory.getLogger(ItemPairMapper.class);

    public PairMapper() {
        keyText = new Text();
        valueIntWritable = new IntWritable(1);
    }

    @Override
    public void map(Object key, BSONObject value, final Context context) {
        logger.info("Map processing with Context class(_id): " + value.get("_id"));
        List<Integer> items = ((List<Integer>) value.get("items"));
//        logger.info("items - " + items.stream().map(String::valueOf).collect(Collectors.joining(",")));
        IntStream.rangeClosed(0, items.size() - 2).boxed()
                 .flatMap(a -> IntStream.rangeClosed(a + 1, items.size() - 1)
                                        .mapToObj(b -> new int[] { a, b }))
                 .forEach(pair -> {
                     keyText.set(String.valueOf(items.get(pair[0])) + " " + String.valueOf(items.get(pair[1])));
                     try {
                         context.write(keyText, valueIntWritable);
                     } catch (IOException | InterruptedException e) {
                         logger.info(e.toString());
                         e.printStackTrace();
                     }
                 });
    }

    @Override
    public void map(Object key, BSONObject value, OutputCollector<Text, IntWritable> output,
                    Reporter reporter) {
        logger.info("Map processing with OutputCollector class(_id): " + value.get("_id"));
        List<Integer> items = ((List<Integer>) value.get("items"));
//        logger.info("items(OutputCollector) - " + items.stream().map(String::valueOf)
//                                .collect(Collectors.joining(",")));
        IntStream.rangeClosed(0, items.size() - 2).boxed()
                .flatMap(a -> IntStream.rangeClosed(a+1, items.size() - 1)
                        .mapToObj(b -> new int[] { a, b }))
                .forEach(pair -> {
                    keyText.set(String.valueOf(items.get(pair[0])) + " " + String.valueOf(items.get(pair[1])));
                    try {
                        output.collect(keyText, valueIntWritable);
                    } catch (IOException e) {
                        logger.info(e.toString());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(JobConf job) {
    }
}
