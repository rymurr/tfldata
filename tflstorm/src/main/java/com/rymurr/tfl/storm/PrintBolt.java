package com.rymurr.tfl.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PrintBolt<T> extends BaseRichBolt {
    private static final Logger logger = LoggerFactory.getLogger(PrintBolt.class);

    private final String field;

    public PrintBolt(String field) {
        this.field = field;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

    }

    @Override
    public void execute(Tuple input) {
        @SuppressWarnings("unchecked") T event = (T)input.getValueByField(field);
        logger.info(event.toString());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
