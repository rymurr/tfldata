package com.rymurr.tfl.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class XmlParserBolt extends BaseRichBolt {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserBolt.class);
    private XmlParser xmlParser;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        xmlParser = new XmlParser();
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String entry = (String)input.getValueByField("str");
        if (entry.isEmpty() || !entry.contains("xml")) {
            return;
        }
        InputStream stream = new ByteArrayInputStream(entry.getBytes(StandardCharsets.UTF_8));
        try {
            List<LineEvent> events = xmlParser.run(stream);
            if (events.isEmpty()) {
                return;
            }
            //java8 is pretty snappy! map to new Values and emit to collector using lambdas and streams!
            //todo make xmlParser return a stream? Lazy evaluation!
            events.stream().map(Values::new).forEach(collector::emit);
        } catch (FileNotFoundException | XMLStreamException e) {
            logger.error("Parsing problem", e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line-entry"));
    }
}
