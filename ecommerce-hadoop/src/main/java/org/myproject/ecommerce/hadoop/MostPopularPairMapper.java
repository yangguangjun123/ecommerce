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

        int a = ((BasicDBObject) value.get("_id")).getInt("a");
        logger.info("a: " + a);
        int b = ((BasicDBObject) value.get("_id")).getInt("b");
        logger.info("b: " + b);
        String count = String.valueOf(value.get("value"));
        keyText.set(String.valueOf(a));
        valueText.set(a + " " + count);
        context.write(keyText, valueText);
        if(a != b) {
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
        int a = ((BasicDBObject) value.get("_id")).getInt("a");
        int b = ((BasicDBObject) value.get("_id")).getInt("b");
        String count = ((BSONObject) value.get("_id")).get("value").toString();
        keyText.set(String.valueOf(a));
        valueText.set(a + " " + count);
        output.collect(keyText, valueText);
        if(a != b) {
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
