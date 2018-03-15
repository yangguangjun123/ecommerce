package org.myproject.ecommerce.hadoop.crossselling;

import com.mongodb.hadoop.io.MongoUpdateWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BasicBSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ActivityReducer extends Reducer<Text, Text, NullWritable, MongoUpdateWritable>
    implements org.apache.hadoop.mapred.Reducer<Text, Text, NullWritable, MongoUpdateWritable> {

    private MongoUpdateWritable reduceResult;

    public ActivityReducer() {
        super();
        reduceResult = new MongoUpdateWritable();
    }

    public void reduce(Text pKey, Iterable<Text> pValues, Context pContext)
            throws IOException, InterruptedException {
        BasicBSONObject query = new BasicBSONObject("_id", pKey.toString());
        List<String> items = StreamSupport
                                    .stream(pValues.spliterator(), false)
                                    .map(v -> v.toString())
                                    .collect(Collectors.toList());

        BasicBSONObject update = new BasicBSONObject("$pushAll", new BasicBSONObject("items", items));
        reduceResult.setQuery(query);
        reduceResult.setModifiers(update);
        pContext.write(null, reduceResult);
    }

    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<NullWritable, MongoUpdateWritable> output,
                       Reporter reporter) throws IOException {
        BasicBSONObject query = new BasicBSONObject("_id", key.toString());
        List<String> items = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                .map(v -> v.toString())
                .collect(Collectors.toList());

        BasicBSONObject update = new BasicBSONObject("$pushAll", new BasicBSONObject("items", items));
        reduceResult.setQuery(query);
        reduceResult.setModifiers(update);
        output.collect(null, reduceResult);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf job) {

    }
}
