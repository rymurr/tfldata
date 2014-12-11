package com.rymurr.tfl.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class XmlFileSpout extends BaseRichSpout {
    private static final Logger logger = LoggerFactory.getLogger(XmlFileSpout.class);
    private final String filename;
    private InputStream is;
    private SpoutOutputCollector collector;

    public XmlFileSpout(String filename) {
        this.filename = filename;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("xml-message"));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        try {
            is = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            logger.info("exception with " + filename, e);
        }
    }

    @Override
    public void nextTuple() {
        StringBuilder builder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }
        } catch (IOException e) {
            logger.error("input problem with " + filename, e);
        }
        if (builder.length() == 0) {
            Utils.sleep(500);
        } else {
            collector.emit(new Values(builder.toString()));
        }
    }
}
