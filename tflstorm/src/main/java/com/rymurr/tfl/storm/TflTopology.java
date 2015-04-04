package com.rymurr.tfl.storm;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import java.util.UUID;

public class TflTopology {
    private final static String TOPIC = "tfl-xml";
    private static final Logger logger = LoggerFactory.getLogger(TflTopology.class);
    private static final String kafka = "192.168.0.115:49194";
    private static final String redis = "192.168.0.115:49193";

    private TopologyBuilder builder = new TopologyBuilder();
    private Config config = new Config();
    private LocalCluster cluster;

    public TflTopology() {
        BrokerHosts hosts = new ZkHosts(kafka);
        SpoutConfig spoutConfig = new SpoutConfig(hosts, TOPIC, "/tfl-xml-spout", UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        builder.setSpout("xml-kafka-spout", new KafkaSpout(spoutConfig), 2);
        builder.setBolt("xml-parse", new XmlParserBolt()).shuffleGrouping("xml-kafka-spout");
        builder.setBolt("unique-line", new LineEventConvert()).shuffleGrouping("xml-parse");
        //builder.setBolt("print-bolt", new PrintBolt<UniqueLineEvent>("line-entry")).shuffleGrouping("unique-line");
        builder.setBolt("to-redis", new LineEventToRedis()).shuffleGrouping("unique-line");
        builder.setBolt("to-cassandra", new LineEventToCassandra()).shuffleGrouping("to-redis");

    }

    public void runLocal(int runTime) {
        config.setDebug(true);
        cluster = new LocalCluster();
        config.put("redisHost", redis);

        config.put("cassandraHost", "172.17.0.96");
        config.put("cassandraKeyspace", "tfl");
        cluster.submitTopology("tfl-topology", config, builder.createTopology());
        if (runTime > 0) {
            Utils.sleep(runTime);
            shutDownLocal();
        }
    }

    public void shutDownLocal() {
        if (cluster != null) {
            cluster.killTopology("tfl-topology");
            cluster.shutdown();
        }
    }

    public void runCluster(String name, String redisHost, String cassandraHost)
            throws AlreadyAliveException, InvalidTopologyException {
        config.setNumWorkers(20);
        config.put("redisHost", redisHost);
        config.put("cassandraHost", cassandraHost);
        StormSubmitter.submitTopology(name, config, builder.createTopology());
    }

    public static void main(String args[]) throws AlreadyAliveException, InvalidTopologyException {
        TflTopology topology = new TflTopology();

        if (args != null && args.length > 1) {
            topology.runCluster(args[0], redis, args[2]);
        } else {
            if (args != null && args.length == 1)
                logger.info("Running in local mode");
            topology.runLocal(240000);
        }

    }
    
}
