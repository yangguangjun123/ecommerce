package org.myproject.ecommerce.hadoop;

import com.mongodb.BasicDBObject;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;
import org.bson.BSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MostPopularPairMapper extends Mapper<Object, BSONObject, Text, Text>
        implements org.apache.hadoop.mapred.Mapper<Object, BSONObject, Text, Text> {
    private final Text keyText;
    private final Text valueText;
    private static final Logger logger = LoggerFactory.getLogger(MostPopularPairMapper.class);

    public MostPopularPairMapper() {
        keyText = new Text();
        valueText = new Text();
    }

    @Override
    public void map(Object key, BSONObject value, final Context context) throws IOException, InterruptedException {
        logger.info("Map processing with Context class(key): " + key.toString());
        logger.info("Map processing with Context class(_id): " + value.get("_id"));
        logger.info("Map processing with Context class(value): " + value.get("value"));
        logger.info("_id class type: " + value.get("_id").getClass());

        String a = ((BasicDBObject) value.get("_id")).getString("a");
        logger.info("a: " + a);
        String b = ((BasicDBObject) value.get("_id")).getString("b");
        logger.info("b: " + b);
        String count = String.valueOf(value.get("value"));
        keyText.set(a);
        valueText.set(a + " " + count);
        context.write(keyText, valueText);
        if(!a.equals(b)) {
            valueText.set(b + " " + count);
            context.write(keyText, valueText);
        }
    }

    @Override
    public void map(Object key, BSONObject value, OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException {
        logger.info("Map processing with Context class(key): " + key.toString());
        logger.info("Map processing with Context class(_id): " + value.get("_id"));
        logger.info("Map processing with Context class(value): " + value.get("value"));
        String a = ((BSONObject) value.get("_id")).get("a").toString();
        String b = ((BSONObject) value.get("_id")).get("b").toString();
        String count = ((BSONObject) value.get("_id")).get("value").toString();
        keyText.set(a);
        valueText.set(a + " " + count);
        output.collect(keyText, valueText);
        if(!a.equals(b)) {
            valueText.set(b + " " + count);
            output.collect(keyText, valueText);
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
