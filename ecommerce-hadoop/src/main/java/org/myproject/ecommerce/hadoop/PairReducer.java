package org.myproject.ecommerce.hadoop;

import com.mongodb.hadoop.io.MongoUpdateWritable;
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
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * In phase one, PairMapper/PairReducer crunch pairs collection to product most_popular_pairs collection.
 * in phase two, ActivityWeightMapper/ActivityWeightReducer crunch activity collections(timed series) to
 * extract the weight values for product items.
 */
public class PairReducer extends Reducer<Text, MostPopularPairedItem, NullWritable, MongoUpdateWritable>
                implements org.apache.hadoop.mapred.Reducer<Text, MostPopularPairedItem,
                        NullWritable, MongoUpdateWritable> {
    private MongoUpdateWritable reduceResult;
    private static final Logger logger = LoggerFactory.getLogger(PairReducer.class);

    public PairReducer() {
        super();
        reduceResult = new MongoUpdateWritable();
    }

    @Override
    public void reduce(Text pKey, Iterable<MostPopularPairedItem> pValues, Context pContext)
            throws IOException, InterruptedException {
        logger.info("Reduce processing with Context class");
        BasicBSONObject query = new BasicBSONObject("_id", pKey.toString());
        List<MostPopularPairedItem> items =
                StreamSupport.stream(pValues.spliterator(), false)
                             .sorted(comparing(MostPopularPairedItem::getCount))
                             .collect(toList());
        BasicBSONObject doc = new BasicBSONObject();
        doc.put("recom", items);
        BasicBSONObject update = new BasicBSONObject("$set", new BasicBSONObject("value", doc));
        reduceResult.setQuery(query);
        reduceResult.setModifiers(update);
        pContext.write(null, reduceResult);
    }

    @Override
    public void reduce(Text key, Iterator<MostPopularPairedItem> values, OutputCollector<NullWritable,
                            MongoUpdateWritable> output, Reporter reporter) throws IOException {
        logger.info("Reduce processing with OutputCollector class");
        BasicBSONObject query = new BasicBSONObject("_id", key.toString());
        List<MostPopularPairedItem> items =
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(values, Spliterator.ORDERED), false)
                             .sorted(comparing(MostPopularPairedItem::getCount))
                             .collect(toList());
        BasicBSONObject doc = new BasicBSONObject();
        doc.put("recom", items);
        BasicBSONObject update = new BasicBSONObject("$set", new BasicBSONObject("value", doc));
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
