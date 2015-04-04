package com.rymurr.tfl.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;


public class LineEventToRedis extends BaseRichBolt {
    public RedisControl redis;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        redis = new RedisControl((String)stormConf.get("redisHost"));
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        UniqueLineEvent event = (UniqueLineEvent) input.getValueByField("line-entry");
        RedisControl.RedisObject object = redis.entryList(event);
        String[] stationKey = event.getStationKey().split(":");
        collector.emit(new Values(event.getTrainKey(), stationKey[0], stationKey[1], object.isCompleted(), object));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("trainId", "station", "platform", "completed", "redisObject"));
    }
}
