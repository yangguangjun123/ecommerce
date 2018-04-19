package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.MongoUpdateWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Reducer;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class LastDayOrderReducer extends Reducer<Text, IntWritable, NullWritable, MongoUpdateWritable>
                implements org.apache.hadoop.mapred.Reducer<Text, IntWritable,
                                NullWritable, MongoUpdateWritable> {

    private static final Logger logger = LoggerFactory.getLogger(LastDayOrderReducer.class);

    public LastDayOrderReducer() {
        super();
    }

    @Override
    public void reduce(Text pKey, Iterable<IntWritable> pValues, Context pContext)
            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        BasicBSONObject query = new BasicBSONObject("_id", pKey.toString());
        List<Integer> items =
                StreamSupport.stream(pValues.spliterator(), false)
                             .mapToInt(v -> v.get())
                             .collect(ArrayList::new, List::add, List::addAll);
        BasicBSONObject items_list = new BasicBSONObject("items", items);
        BasicBSONObject update = new BasicBSONObject("$pushAll", items_list);
        pContext.write(null, new MongoUpdateWritable(query, update, true, false, false));
    }

    @Override
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<NullWritable,
                                MongoUpdateWritable> output, Reporter reporter) throws IOException {
        logger.info("Reduce OutputCollector with Context class");
        BasicBSONObject query = new BasicBSONObject("_id", key.toString());
        List<Integer> items =
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                        .mapToInt(v -> v.get())
                        .collect(ArrayList::new, List::add, List::addAll);
        BasicBSONObject items_list = new BasicBSONObject("items", items);
        BasicBSONObject update = new BasicBSONObject("$pushAll", items_list);
        output.collect(null, new MongoUpdateWritable(query, update, true, false, false));
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void configure(JobConf job) {
    }
}