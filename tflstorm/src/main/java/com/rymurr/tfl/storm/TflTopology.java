package com.rymurr.tfl.storm;


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TflTopology {
    private static final Logger logger = LoggerFactory.getLogger(TflTopology.class);

    private TopologyBuilder builder = new TopologyBuilder();
    private Config config = new Config();
    private LocalCluster cluster;

    public TflTopology() {
        final String filename = "/data/xxx.xml";
        builder.setSpout("xml-file", new XmlFileSpout(filename));
        builder.setBolt("xml-parse", new XmlParserBolt()).shuffleGrouping("xml-file");
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
            topology.runLocal(10000);
        }
    }
}
