package com.rymurr.tfl.kafka;


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Main {
    private final static String TOPIC = "tfl-xml";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private final Producer<String, String> producer;

    public Main() {
        Properties properties = new Properties();
        properties.put("metadata.broker.list", "192.168.0.116:49154");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(properties);
        producer = new Producer<>(config);
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        executor.scheduleAtFixedRate(new UrlFetch(producer), 10, 30, TimeUnit.SECONDS);
    }

    private static class UrlFetch implements Runnable {
        private final Producer<String, String> producer;

        public UrlFetch(Producer<String, String> producer) {
            this.producer = producer;
        }

        @Override
        public void run() {
            Function<String, String> urlStrToXml = urlStr -> {
                try {
                    URL url = new URL(urlStr);
                    URLConnection connection = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    final StringBuilder buffer = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine);
                    }
                    producer.send(new KeyedMessage<>(TOPIC, urlStr, buffer.toString()));
                    logger.info("Sent url " + urlStr + " to kafka. Message was " + buffer.length() + " long");
                } catch (MalformedURLException e) {
                    logger.error("MalformedURLException error at = " + urlStr, e);
                } catch (IOException e) {
                    logger.error("IOException error at = " + urlStr, e);
                }
                return urlStr;
            };
            Arrays.asList(Data.URLS).stream().parallel().map(urlStrToXml).forEach(logger::info);
        }
    }
}
