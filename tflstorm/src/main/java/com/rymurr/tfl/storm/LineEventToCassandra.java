package com.rymurr.tfl.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;

import java.util.Map;

import static com.rymurr.tfl.storm.RedisControl.RedisObject;


public class LineEventToCassandra extends BaseRichBolt {
    private Cluster cluster;
    private Session session;
    private BoundStatement boundStatement;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        cluster = Cluster
                .builder()
                .addContactPoint((String) stormConf.get("cassandraHost"))
                .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                .withLoadBalancingPolicy(
                        new TokenAwarePolicy(new DCAwareRoundRobinPolicy()))
                .build();
        session = cluster.connect((String) stormConf.get("cassandraKeyspace"));
        PreparedStatement statement = session.prepare("insert into trains ("
                + "trainId,"
                + "station,"
                + "platform,"
                + "destination,"
                + "destinationChanges,"
                + "firstSeen,"
                + "lastSeen,"
                + "location,"
                + "secondsToStation,"
                + "seenCount,"
                + "completed,"
                + "line"
                + ") values (?,?,?,?,?,?,?,?,?,?,?,?)");
        boundStatement = new BoundStatement(statement);
    }

    @Override
    public void execute(Tuple input) {
        RedisObject event = (RedisObject) input.getValueByField("redisObject");
        BoundStatement statement = boundStatement.bind(
                input.getStringByField("trainId"),
                input.getStringByField("station"),
                Integer.parseInt(input.getStringByField("platform")),
                event.getDestination(),
                event.getDestinationChanges(),
                event.getFirstSeen().toDate(),
                event.getLastSeen().toDate(),
                event.getLocation(),
                event.getSecondsToStation(),
                event.getSeenCount(),
                input.getBooleanByField("completed"),
                event.getLine()
        );
        session.execute(statement);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
}
