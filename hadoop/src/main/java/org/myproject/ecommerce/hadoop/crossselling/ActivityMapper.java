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
 * ActivityMapper/ActivityReducer classes crunch activity collections(timed series) to
 * product lastDayOrders collection.
 *
 */
public class ActivityMapper extends Mapper<Object, BSONObject, Text, Text>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONWritable, Text, Text> {

    private final Text keyText;
    private final Text valueText;
    private static final Logger logger = LoggerFactory.getLogger(ActivityMapper.class);

    public ActivityMapper() {
        super();
        keyText = new Text();
        valueText = new Text();
    }

    /**
     * Map method implementing MAPRED V1(Classic)
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void map(Object key, BSONObject value, final Context context)
            throws IOException, InterruptedException {
        logger.info("Map processing with Context class");
        logger.info("key: " + key.toString());
        String keyOut = (String) ((BSONObject) value.get("data")).get("userId");
        keyText.set(keyOut);
        valueText.set((String) ((BSONObject) value.get("data")).get("itemId"));
        context.write(keyText, valueText);
    }

    /**
     * Map method implementing MAPRED V2
     *
     * @param key
     * @param bsonWritable
     * @param outputCollector
     * @param reporter
     * @throws IOException
     */
    @Override
    public void map(Object key, BSONWritable bsonWritable, OutputCollector<Text, Text> outputCollector,
                    Reporter reporter) throws IOException {
        logger.info("processing with OutputCollector class");
        BSONObject doc = bsonWritable.getDoc();
        String keyOut = (String) ((BSONObject) doc.get("data")).get("userId");
        keyText.set(keyOut);
        valueText.set((String) ((BSONObject) doc.get("data")).get("itemId"));
        outputCollector.collect(keyText, valueText);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf jobConf) {
    }
}
