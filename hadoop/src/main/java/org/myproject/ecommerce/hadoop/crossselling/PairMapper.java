package org.myproject.ecommerce.hadoop.crossselling;

import com.mongodb.hadoop.io.BSONWritable;
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
public class PairMapper extends Mapper<Object, BSONObject, Text, MostPopularPairedItem>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONWritable, Text, MostPopularPairedItem> {
    private final Text keyText;
    private final MostPopularPairedItem valueMosPopularPairedItem;
    private static final Logger logger = LoggerFactory.getLogger(PairMapper.class);

    public PairMapper() {
        keyText = new Text();
        valueMosPopularPairedItem = new MostPopularPairedItem();
    }

    @Override
    public void map(Object key, BSONObject value, final Context context)
            throws IOException, InterruptedException {
        logger.info("Map processing with Context class");
        BSONObject keyPair = ((BSONObject) value.get("_id"));
        if(keyPair.get("a").equals(keyPair.get("b"))) {
            valueMosPopularPairedItem.setItemId((String) value.get("a"));
            valueMosPopularPairedItem.setCount((Integer) value.get("value"));
            keyText.set((String) value.get("a"));
            context.write(keyText, valueMosPopularPairedItem);
        } else {
            valueMosPopularPairedItem.setItemId((String) value.get("a"));
            valueMosPopularPairedItem.setCount((Integer) value.get("value"));
            keyText.set((String) value.get("a"));
            context.write(keyText, valueMosPopularPairedItem);
            valueMosPopularPairedItem.setItemId((String) value.get("b"));
            valueMosPopularPairedItem.setCount((Integer) value.get("value"));
            keyText.set((String) value.get("b"));
            context.write(keyText, valueMosPopularPairedItem);
        }
    }

    @Override
    public void map(Object key, BSONWritable bsonWritable, OutputCollector<Text, MostPopularPairedItem> output,
                    Reporter reporter) throws IOException {
        logger.info("Map processing with OutputCollector class");
        BSONObject doc = bsonWritable.getDoc();
        BSONObject keyPair = ((BSONObject) doc.get("_id"));
        if(keyPair.get("a").equals(keyPair.get("b"))) {
            valueMosPopularPairedItem.setItemId((String) doc.get("a"));
            valueMosPopularPairedItem.setCount((Integer) doc.get("value"));
            keyText.set((String) doc.get("a"));
            output.collect(keyText, valueMosPopularPairedItem);
        } else {
            valueMosPopularPairedItem.setItemId((String) doc.get("a"));
            valueMosPopularPairedItem.setCount((Integer) doc.get("value"));
            keyText.set((String) doc.get("a"));
            output.collect(keyText, valueMosPopularPairedItem);
            valueMosPopularPairedItem.setItemId((String) doc.get("b"));
            valueMosPopularPairedItem.setCount((Integer) doc.get("value"));
            keyText.set((String) doc.get("b"));
            output.collect(keyText, valueMosPopularPairedItem);
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
