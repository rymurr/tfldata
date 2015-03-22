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

    private TopologyBuilder builder = new TopologyBuilder();
    private Config config = new Config();
    private LocalCluster cluster;

    public TflTopology() {
        BrokerHosts hosts = new ZkHosts("192.168.0.115:49155");
        SpoutConfig spoutConfig = new SpoutConfig(hosts, TOPIC, "/tfl-xml-spout", UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        builder.setSpout("xml-kafka-spout", new KafkaSpout(spoutConfig), 2);
        builder.setBolt("xml-parse", new XmlParserBolt()).shuffleGrouping("xml-kafka-spout");
        builder.setBolt("print-bolt", new PrintBolt()).shuffleGrouping("xml-parse");
    }

    public void runLocal(int runTime) {
        config.setDebug(true);
        cluster = new LocalCluster();
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
        StormSubmitter.submitTopology(name, config, builder.createTopology());
    }

    public static void main(String args[]) throws AlreadyAliveException, InvalidTopologyException {
        TflTopology topology = new TflTopology();

        if (args != null && args.length > 1) {
            topology.runCluster(args[0], args[1], args[2]);
        } else {
            if (args != null && args.length == 1)
                logger.info("Running in local mode");
            topology.runLocal(120000);
        }
    }
}
