package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.BSONWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class MostPopularPairReducer extends Reducer<Text, Text, BSONWritable, BSONWritable>
        implements org.apache.hadoop.mapred.Reducer<Text, Text,
        BSONWritable, BSONWritable> {
    private BSONWritable keyBSONWritable;
    private BSONWritable resultWriteable;

    private static final Logger logger = LoggerFactory.getLogger(MostPopularPairReducer.class);

    public MostPopularPairReducer() {
        keyBSONWritable = new BSONWritable();
        resultWriteable = new BSONWritable();
    }

    @Override
    public void reduce(Text pKey, Iterable<Text> pValues, Context pContext)
                            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        keyBSONWritable.setDoc(new BasicBSONObject("_id", new BasicBSONObject()
                .append("itemId", pKey.toString())));
        List<BasicBSONObject> items =
                StreamSupport.stream(pValues.spliterator(), false)
                             .map(t -> t.toString())
                             .map(s -> s.split(" "))
                             .map(s -> new BasicBSONObject().append("itemId", Integer.parseInt(s[0]))
                                            .append("count", Integer.parseInt(s[1])))
                             .sorted((o1, o2) -> Integer.compare(o1.getInt("count"), o2.getInt("o2")))
                             .collect(toList());
        BasicBSONObject doc = new BasicBSONObject().append("recom", new BasicBSONList().addAll(items));
        resultWriteable.setDoc(doc);
        pContext.write(keyBSONWritable, resultWriteable);
    }

    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<BSONWritable, BSONWritable> output,
                       Reporter reporter) throws IOException {
        logger.info("Reduce processing with OutputCollector class");
        keyBSONWritable.setDoc(new BasicBSONObject("_id", new BasicBSONObject()
                .append("itemId", key.toString())));
        List<BasicBSONObject> items = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                .map(t -> t.toString())
                .map(s -> s.split(" "))
                .map(s -> new BasicBSONObject().append("itemId", Integer.parseInt(s[0]))
                        .append("count", Integer.parseInt(s[1])))
                .sorted((o1, o2) -> Integer.compare(o1.getInt("count"), o2.getInt("o2")))
                .collect(toList());
        BasicBSONObject doc = new BasicBSONObject().append("recom", new BasicBSONList().addAll(items));
        resultWriteable.setDoc(doc);
        output.collect(keyBSONWritable, resultWriteable);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}
