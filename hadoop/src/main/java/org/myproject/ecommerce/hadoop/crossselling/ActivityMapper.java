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

    @Override
    public void map(Object key, BSONObject value, final Context context)
            throws IOException, InterruptedException {
        logger.info("key: " + key.toString());
        String keyOut = (String) ((BSONObject) value.get("data")).get("userId");
        keyText.set(keyOut);
        valueText.set((String) ((BSONObject) value.get("data")).get("itemId"));
        context.write(keyText, valueText);
    }

    @Override
    public void map(Object o, BSONWritable bsonWritable, OutputCollector<Text, Text> outputCollector,
                    Reporter reporter) throws IOException {
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
